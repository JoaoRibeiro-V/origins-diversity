package com.origins_diversity.Events;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerReference;
import io.github.apace100.apoli.power.type.ResourcePowerType;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class KitsuneEvents {

    private static final ResourceLocation TAILS_ID =
            ResourceLocation.fromNamespaceAndPath("origins-diversity", "kitsune/kitsune_tails");

    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                refreshTails(newPlayer));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.tickCount == 1) refreshTails(player);
            }
        });
    }

    private static void refreshTails(ServerPlayer player) {
        PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {
            ResourcePowerType resource = (ResourcePowerType) holder.getPowerType(PowerReference.resource(TAILS_ID).getPower());
            if (resource == null) return;

            int current = resource.getValue();
            resource.setValue(current - 1);
            resource.setValue(current);
        });
    }
}