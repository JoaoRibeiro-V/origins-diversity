package com.origins_diversity.Client.PowerHandlers.Kitsune;

import net.minecraft.client.Minecraft;
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
        if (ACTIVE.size() < 5 && RANDOM.nextFloat() < 0.04f) {
            spawn(mc);
        }

        ACTIVE.removeIf(FakeIllusionEntity::tick);
    }

    private static void spawn(Minecraft mc) {
        assert mc.player != null;
        List<Entity> candidates = mc.level.getEntities(mc.player,mc.player.getBoundingBox().inflate(16),e -> isValidIllusionTarget(e, mc.player));

        Entity ebase;

        if(!candidates.isEmpty()){
            ebase = candidates.get(mc.level.random.nextInt(candidates.size()));
        }else{
            ebase = createExampleEntity(mc);
            if(ebase == null) return;
        }

        ebase.setInvisible(false);
        ebase.setNoGravity(false);
        ebase.setSilent(true);

        Vec3 basePos = mc.player.position();
        Vec3 offset = new Vec3(mc.level.random.nextDouble() * 12 - 5, 0, mc.level.random.nextDouble() * 12 - 5);
        Vec3 spawnPos = basePos.add(offset);
        ebase.setPos(spawnPos);
        ebase.setBoundingBox(ebase.getDimensions(ebase.getPose()).makeBoundingBox(spawnPos));

        ebase.setDeltaMovement(Vec3.ZERO);

        ACTIVE.add(new FakeIllusionEntity(
                ebase,
                spawnPos,
                70 + RANDOM.nextInt(-10,170)
        ));

    }

    public static List<FakeIllusionEntity> getActive() {
        return ACTIVE;
    }
}