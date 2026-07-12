package com.origins_diversity.PowerHandlers;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class AttackSculkCultistGoal extends ActiveTargetGoal<PlayerEntity> {
    public AttackSculkCultistGoal(MobEntity entity, int chance){
        super(entity,
                PlayerEntity.class,
                chance,
                true,
                false,
                player -> OriginsUtil.hasOrigin((PlayerEntity) player, "origins-diversity","sculk_cultist")
        );
    }
}
