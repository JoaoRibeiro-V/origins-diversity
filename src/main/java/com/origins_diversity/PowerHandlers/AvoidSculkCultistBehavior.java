package com.origins_diversity.PowerHandlers;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class AvoidSculkCultistBehavior extends Behavior<Villager> {

    public AvoidSculkCultistBehavior() {
        super(Map.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Villager villager) {
        return level.getNearestPlayer(
                villager.getX(),
                villager.getY(),
                villager.getZ(),
                12.0,
                player -> OriginsUtil.hasOrigin((Player) player, "origins-diversity", "sculk_cultist")
        ) != null;
    }

    @Override
    protected void start(ServerLevel level, Villager villager, long gameTime) {
        Player player = level.getNearestPlayer(
                villager.getX(),
                villager.getY(),
                villager.getZ(),
                10.0,
                p -> OriginsUtil.hasOrigin((Player) p, "origins-diversity", "sculk_cultist")
        );

        if (player != null) {
            if(!villager.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE)) {
                villager.getBrain().setMemoryWithExpiry(
                        MemoryModuleType.NEAREST_HOSTILE,
                        player,
                        400L
                );
            }

        }
    }
}
