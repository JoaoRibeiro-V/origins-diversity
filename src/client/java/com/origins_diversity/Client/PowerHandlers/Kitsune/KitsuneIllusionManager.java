package com.origins_diversity.Client.PowerHandlers.Kitsune;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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

    private static Entity createExampleEntity(Minecraft mc) {
        EntityType<?> type = DEFAULT_TYPES[mc.level.random.nextInt(DEFAULT_TYPES.length)];
        Entity e = type.create(mc.level);
        if (e == null) return null;

        e.setYRot(mc.level.random.nextFloat() * 360f);
        e.setXRot(0f);

        return e;
    }

    private static Entity createFakeEntity(Minecraft mc, Entity original) {
        Entity e = original.getType().create(mc.level);
        if (e == null) return null;

        e.setPos(original.getX(), original.getY(), original.getZ());

        float yaw = original.getYRot();

        e.setYRot(yaw);
        e.setYBodyRot(yaw);
        e.setYHeadRot(yaw);

        e.setXRot(original.getXRot());

        e.setOnGround(true);
        e.fallDistance = 0f;

        return e;
    }

    private static Entity createIllusion(Minecraft mc, Entity original) {

        if (original instanceof Player player) {
            return createFakePlayer(mc, player);
        }

        return createFakeEntity(mc, original);
    }

    private static Entity createFakePlayer(Minecraft mc, Player original) {
        if (!(mc.level instanceof ClientLevel clientLevel)) {
            return null;
        }

        GameProfile profile = original.getGameProfile();

        RemotePlayer fake = new RemotePlayer(clientLevel, profile);
        fake.setPos(original.getX(), original.getY()+0.05, original.getZ());
        float yaw = original.getYRot();

        fake.setYRot(yaw);
        fake.setYBodyRot(yaw);
        fake.setYHeadRot(yaw);

        fake.yRotO = yaw;
        fake.yBodyRotO = yaw;
        fake.yHeadRotO = yaw;

        fake.setXRot(0f);
        fake.xRotO = 0f;
        fake.setCustomName(original.getName());
        fake.setCustomNameVisible(true);
        fake.setOnGround(true);
        fake.fallDistance = 0f;

        return fake;
    }

    private static Entity createVisualEntity(Minecraft mc, Entity original) {

        Entity e;

        if (original instanceof Player player) {
            e = new RemotePlayer((ClientLevel) mc.level, player.getGameProfile());

            ((RemotePlayer)e).setCustomName(player.getName());
            ((RemotePlayer)e).setCustomNameVisible(true);

        } else {
            e = original.getType().create(mc.level);
            if (e == null) return null;
        }

        // copy ONLY visual state
        e.setYRot(original.getYRot());
        e.setXRot(original.getXRot());

        e.setYBodyRot(original.getYRot());
        e.setYHeadRot(original.getYRot());

        return e;
    }

    private static boolean isValidIllusionTarget(Entity e, Player player) {
        if (e == player) return false;
        if (!e.isAlive()) return false;

        if (e instanceof Mob mob) {
            if (mob.getTarget() == player) return true;
            if (mob.isAggressive()) return true;
        }

        return e instanceof Player other && other != player;
    }

    public static void tick(Minecraft mc) {
        if (ACTIVE.size() < 3 && RANDOM.nextFloat() < 0.04f) {
            spawn(mc);
        }

        ACTIVE.removeIf(illusion -> {
            return illusion.renderEntity.tickCount++ > illusion.lifetime;
        });
    }

    private static void spawn(Minecraft mc) {
        assert mc.player != null;
        List<Entity> candidates = mc.level.getEntities(
                mc.player,
                mc.player.getBoundingBox().inflate(16),
                e -> isValidIllusionTarget(e, mc.player)
        );
        List<AbstractClientPlayer> players = mc.level.players();

        Player debugTarget = players.stream()
                .filter(p -> p != mc.player)
                .findFirst()
                .orElse(mc.player); // fallback
        Entity ebase;
        if (!candidates.isEmpty()) {
            Entity candidate = candidates.get(mc.level.random.nextInt(candidates.size()));

            if (candidate instanceof Player player) {
                ebase = createFakePlayer(mc, player);
            } else {
                ebase = candidate.getType().create(mc.level);

                if (ebase != null) {
                    ebase.load(candidate.saveWithoutId(new net.minecraft.nbt.CompoundTag()));
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

        Vec3 basePos = mc.player.position();
        Vec3 offset = new Vec3(mc.level.random.nextDouble() * 12 - 5, 0, mc.level.random.nextDouble() * 12 - 5);
        Vec3 spawnPos = basePos.add(offset);
        double y = getGroundY(mc, spawnPos.x, spawnPos.z, spawnPos.y);
        spawnPos = new Vec3(spawnPos.x, y+0.01, spawnPos.z);
        ebase.setPos(spawnPos);
        ebase.setOldPosAndRot();
        //ebase.setBoundingBox(ebase.getDimensions(ebase.getPose()).makeBoundingBox(spawnPos));

        ACTIVE.add(new FakeIllusionEntity(
                ebase,
                spawnPos,
                80 + RANDOM.nextInt(0,220)
        ));

    }

    private static double getGroundY(Minecraft mc, double x, double z, double startY) {
        Vec3 from = new Vec3(x, startY + 4, z);
        Vec3 to   = new Vec3(x, startY - 20, z);

        assert mc.level != null;
        assert mc.player != null;
        BlockHitResult hit = mc.level.clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mc.player
        ));

        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            return hit.getLocation().y;
        }
        return startY; // fallback if no block hit
    }

    public static List<FakeIllusionEntity> getActive() {
        return ACTIVE;
    }
}