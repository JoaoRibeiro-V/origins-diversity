package com.origins_diversity.client.Client.PowerHandlers.Kitsune;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class KitsuneIllusionManager {

    private static final Random RANDOM = new Random();
    private static final List<FakeIllusionEntity> ACTIVE = new ArrayList<>();
    private static final EntityType<?>[] DEFAULT_TYPES = new EntityType[]{
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
    };

    private static Entity createExampleEntity(MinecraftClient mc) {
        EntityType<?> type = DEFAULT_TYPES[mc.world.random.nextInt(DEFAULT_TYPES.length)];
        Entity e = type.create(mc.world);
        if (e == null) return null;

        e.setYaw(mc.world.random.nextFloat() * 360f);
        e.setPitch(0f);

        return e;
    }

    private static Entity createFakeEntity(MinecraftClient mc, Entity original) {
        Entity e = original.getType().create(mc.world);
        if (e == null) return null;

        e.setPos(original.getX(), original.getY(), original.getZ());

        float yaw = original.getYaw();

        e.setYaw(yaw);
        e.setBodyYaw(yaw);
        e.setHeadYaw(yaw);

        e.setPitch(original.getPitch());

        e.setOnGround(true);
        e.fallDistance = 0f;

        return e;
    }

    private static Entity createIllusion(MinecraftClient mc, Entity original) {

        if (original instanceof PlayerEntity player) {
            return createFakePlayer(mc, player);
        }

        return createFakeEntity(mc, original);
    }

    private static Entity createFakePlayer(MinecraftClient mc, PlayerEntity original) {
        if (mc.world == null) {
            return null;
        }

        ClientWorld clientLevel = mc.world;

        GameProfile profile = original.getGameProfile();

        OtherClientPlayerEntity fake = new OtherClientPlayerEntity(clientLevel, profile);
        fake.setPos(original.getX(), original.getY()+0.05, original.getZ());
        float yaw = original.getYaw();

        fake.setYaw(yaw);
        fake.setBodyYaw(yaw);
        fake.setHeadYaw(yaw);

        fake.prevYaw = yaw;
        fake.prevBodyYaw = yaw;
        fake.prevHeadYaw = yaw;

        fake.setPitch(0f);
        fake.prevPitch = 0f;
        fake.setCustomName(original.getName());
        fake.setCustomNameVisible(true);
        fake.setOnGround(true);
        fake.fallDistance = 0f;

        return fake;
    }

    private static Entity createVisualEntity(MinecraftClient mc, Entity original) {

        Entity e;

        if (original instanceof PlayerEntity player) {
            e = new OtherClientPlayerEntity((ClientWorld) mc.world, player.getGameProfile());

            ((OtherClientPlayerEntity)e).setCustomName(player.getName());
            ((OtherClientPlayerEntity)e).setCustomNameVisible(true);

        } else {
            e = original.getType().create(mc.world);
            if (e == null) return null;
        }

        // copy ONLY visual state
        e.setYaw(original.getYaw());
        e.setPitch(original.getPitch());

        e.setBodyYaw(original.getYaw());
        e.setHeadYaw(original.getYaw());

        return e;
    }

    private static boolean isValidIllusionTarget(Entity e, PlayerEntity player) {
        if (e == player) return false;
        if (!e.isAlive()) return false;

        if (e instanceof MobEntity mob) {
            if (mob.getTarget() == player) return true;
            if (mob.isAttacking()) return true;
        }

        return e instanceof PlayerEntity other && other != player;
    }

    public static void tick(MinecraftClient mc) {
        if (ACTIVE.size() < 3 && RANDOM.nextFloat() < 0.04f) {
            spawn(mc);
        }

        ACTIVE.removeIf(illusion -> {
            return illusion.renderEntity.age++ > illusion.lifetime;
        });
    }

    private static void spawn(MinecraftClient mc) {
        assert mc.player != null;
        List<Entity> candidates = mc.world.getOtherEntities(
                mc.player,
                mc.player.getBoundingBox().expand(16),
                e -> isValidIllusionTarget(e, mc.player)
        );
        List<AbstractClientPlayerEntity> players = mc.world.getPlayers();

        PlayerEntity debugTarget = players.stream()
                .filter(p -> p != mc.player)
                .findFirst()
                .map(p -> (PlayerEntity) p)
                .orElse(mc.player); // fallback
        Entity ebase;
        if (!candidates.isEmpty()) {
            Entity candidate = candidates.get(mc.world.random.nextInt(candidates.size()));

            if (candidate instanceof PlayerEntity player) {
                ebase = createFakePlayer(mc, player);
            } else {
                ebase = candidate.getType().create(mc.world);

                if (ebase != null) {
                    ebase.readNbt(candidate.writeNbt(new net.minecraft.nbt.NbtCompound()));
                }
            }

            if (ebase == null) {
                ebase = createExampleEntity(mc);
                if (ebase == null) return;
            }
        } else {
            ebase = createExampleEntity(mc);
            if (ebase == null) return;
        }

        ebase.setInvisible(false);
        ebase.setNoGravity(false);
        ebase.setSilent(true);
        ebase.setOnGround(true);

        Vec3d basePos = mc.player.getPos();
        Vec3d offset = new Vec3d(mc.world.random.nextDouble() * 12 - 5, 0, mc.world.random.nextDouble() * 12 - 5);
        Vec3d spawnPos = basePos.add(offset);
        double y = getGroundY(mc, spawnPos.x, spawnPos.z, spawnPos.y);
        spawnPos = new Vec3d(spawnPos.x, y+0.01, spawnPos.z);
        ebase.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        ebase.prevX = ebase.getX();
        ebase.prevY = ebase.getY();
        ebase.prevZ = ebase.getZ();
        //ebase.setBoundingBox(ebase.getDimensions(ebase.getPose()).getBoxAt(spawnPos));

        ACTIVE.add(new FakeIllusionEntity(
                ebase,
                spawnPos,
                80 + RANDOM.nextInt(220)
        ));

    }

    private static double getGroundY(MinecraftClient mc, double x, double z, double startY) {
        Vec3d from = new Vec3d(x, startY + 4, z);
        Vec3d to   = new Vec3d(x, startY - 20, z);

        assert mc.world != null;
        assert mc.player != null;
        BlockHitResult hit = mc.world.raycast(new RaycastContext(
                from,
                to,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));

        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            return hit.getPos().y;
        }
        return startY; // fallback if no block hit
    }

    public static List<FakeIllusionEntity> getActive() {
        return ACTIVE;
    }
}
