package com.origins_diversity.PowerHandlers;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;

public class AttackSculkCultistGoal extends NearestAttackableTargetGoal<Player> {
    public AttackSculkCultistGoal(Mob entity, int chance){
        super(entity,
                Player.class,
                chance,
                true,
                false,
                player -> OriginsUtil.hasOrigin((Player) player, "origins-diversity","sculk_cultist")
        );
    }
}
