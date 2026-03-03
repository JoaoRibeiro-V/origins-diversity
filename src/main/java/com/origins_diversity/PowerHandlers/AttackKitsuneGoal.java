package com.origins_diversity.PowerHandlers;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;

public class AttackKitsuneGoal extends NearestAttackableTargetGoal<Player> {
    public AttackKitsuneGoal(Wolf wolf, int chance) {
        super(
                wolf,
                Player.class,
                chance,
                true,
                false,
                player -> OriginsUtil.hasOrigin((Player) player, "origins-diversity","kitsune")
        );
    }
}
