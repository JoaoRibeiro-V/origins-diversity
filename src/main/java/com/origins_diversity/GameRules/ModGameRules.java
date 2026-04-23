package com.origins_diversity.GameRules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;

public class ModGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> PREVENT_MOUNT_DAMAGE =
            GameRuleRegistry.register(
                    "preventMountDamage",
                    GameRules.Category.MOBS,
                    GameRuleFactory.createBooleanRule(true)
            );

    public static void register() {}
}
