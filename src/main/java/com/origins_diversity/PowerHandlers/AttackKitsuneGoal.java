package com.origins_diversity.PowerHandlers;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;

public class AttackKitsuneGoal extends ActiveTargetGoal<PlayerEntity> {
    public AttackKitsuneGoal(WolfEntity wolf, int chance) {
        super(
                wolf,
                PlayerEntity.class,
                chance,
                true,
                false,
                player -> OriginsUtil.hasOrigin((PlayerEntity) player, "origins-diversity","kitsune")
        );
    }
}
