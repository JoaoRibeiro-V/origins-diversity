package com.origins_diversity.PowerHandlers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerReference;
import io.github.apace100.apoli.power.type.ResourcePowerType;
import io.github.apace100.apoli.util.PowerUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class SupernovaHandler {

    private static final ResourceLocation STARLIGHT_ENERGY =
            ResourceLocation.fromNamespaceAndPath("origins-diversity", "starborn/starlight_energy");

    public static void onDeath(ServerPlayer player) {
        PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {
            if (holder.getPowerType(PowerReference.resource(STARLIGHT_ENERGY).getPower()) == null) return;
            ResourcePowerType resource = (ResourcePowerType) holder.getPowerType(PowerReference.resource(STARLIGHT_ENERGY).getPower());
            if (resource == null) return;
            int energy = resource.getValue();
            if (energy < 100) return;
            resource.setValue(energy - 100);
            ServerLevel level = player.serverLevel();
            boolean griefing = level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

            level.explode(
                    player,
                    player.getX(), player.getY(), player.getZ(),
                    6.0f,
                    true,
                    griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE
            );
        });
    }
}