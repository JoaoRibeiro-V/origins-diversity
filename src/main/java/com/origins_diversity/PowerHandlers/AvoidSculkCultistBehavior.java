package com.origins_diversity.PowerHandlers;

import com.origins_diversity.Extra.OriginsUtil;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Objects;

public class AvoidSculkCultistBehavior extends MultiTickTask<VillagerEntity> {

    public AvoidSculkCultistBehavior() {
        super(Map.of(
                MemoryModuleType.AVOID_TARGET,
                MemoryModuleState.VALUE_ABSENT
        ), 400);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, VillagerEntity villager) {
        return world.getClosestPlayer(
                villager.getX(),
                villager.getY(),
                villager.getZ(),
                12.0,
                player -> OriginsUtil.hasOrigin(
                        (PlayerEntity) player,
                        "origins-diversity",
                        "sculk_cultist"
                )
        ) != null;
    }

    @Override
    protected void run(ServerWorld world, VillagerEntity villager, long time) {
        PlayerEntity player = world.getClosestPlayer(
                villager.getX(),
                villager.getY(),
                villager.getZ(),
                12.0,
                p -> OriginsUtil.hasOrigin(
                        (PlayerEntity) p,
                        "origins-diversity",
                        "sculk_cultist"
                )
        );

        if (player != null) {
            villager.getBrain().remember(
                    MemoryModuleType.AVOID_TARGET,
                    player,
                    400L
            );
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, VillagerEntity villager, long time) {
        return villager.getBrain()
                .hasMemoryModule(MemoryModuleType.AVOID_TARGET);
    }

    @Override
    protected void keepRunning(ServerWorld world, VillagerEntity villager, long time) {
        PlayerEntity player = (PlayerEntity) Objects.requireNonNull(villager.getBrain()
                        .getOptionalMemory(MemoryModuleType.AVOID_TARGET))
                .orElse(null);

        if (player == null || player.isRemoved()) {
            villager.getBrain().forget(
                    MemoryModuleType.AVOID_TARGET
            );
        }
    }

    @Override
    protected void finishRunning(ServerWorld world, VillagerEntity villager, long time) {
        villager.getBrain().forget(
                MemoryModuleType.AVOID_TARGET
        );
    }
}