package com.origins_diversity.Events;

import com.origins_diversity.Extra.OriginsUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerReference;
import io.github.apace100.apoli.power.type.ResourcePowerType;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.*;

public class KitsuneEvents {

    private static final ResourceLocation TAILS_ID =
            ResourceLocation.fromNamespaceAndPath("origins-diversity", "kitsune/kitsune_tails");

    private static final Map<UUID, Integer> lastTailCount = new HashMap<>();

    private static final Map<UUID, Integer> pendingRefresh = new HashMap<>();
    private static void putOnQueue(ServerPlayer player) {
        if(!OriginsUtil.hasOrigin(player, "origins-diversity","kitsune")) return;
        pendingRefresh.put(player.getUUID(), 5);
    }

    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                putOnQueue(newPlayer));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            List<UUID> toRefresh = new ArrayList<>();

            pendingRefresh.replaceAll((uuid, ticks) -> ticks - 1);
            pendingRefresh.entrySet().removeIf(entry -> {
                if (entry.getValue() <= 0) {
                    toRefresh.add(entry.getKey());
                    return true;
                }
                return false;
            });

            for (UUID uuid : toRefresh) {
                ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                if (player != null) refreshTails(player);
            }

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                checkTailChange(player);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                refreshTails(handler.player));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            lastTailCount.remove(handler.player.getUUID());
            pendingRefresh.remove(handler.player.getUUID());
        });
    }

    private static void checkTailChange(ServerPlayer player) {
        PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {
            ResourcePowerType resource = (ResourcePowerType) holder.getPowerType(
                    PowerReference.resource(TAILS_ID).getPower());

            if (resource == null) {
                // no longer kitsune, clean up if we were tracking them
                if (lastTailCount.containsKey(player.getUUID())) {
                    lastTailCount.remove(player.getUUID());
                    removeAllTailModifiers(player);
                }
                return;
            }

            int current = resource.getValue();
            if (lastTailCount.getOrDefault(player.getUUID(), -1) != current) {
                lastTailCount.put(player.getUUID(), current);
                applyTailModifiers(player, current);
            }
        });
    }

    private static void removeAllTailModifiers(ServerPlayer player) {
        AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) return;

        attr.removeModifier(ResourceLocation.fromNamespaceAndPath("origins-diversity", "kitsune_hearts"));
        for (int i = 1; i <= 9; i++) {
            attr.removeModifier(ResourceLocation.fromNamespaceAndPath("origins-diversity", "kitsune_hearts" + i));
        }
    }

    private static void refreshTails(ServerPlayer player) {
        PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {
            ResourcePowerType resource = (ResourcePowerType) holder.getPowerType(
                    PowerReference.resource(TAILS_ID).getPower());
            if (resource == null) return;

            int tails = resource.getValue();
            lastTailCount.put(player.getUUID(), tails);
            applyTailModifiers(player, tails);
        });
    }

    private static void applyTailModifiers(ServerPlayer player, int tails) {
        AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) return;

        removeAllTailModifiers(player);

        attr.addPermanentModifier(new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("origins-diversity", "kitsune_hearts"),
                -10.0,
                AttributeModifier.Operation.ADD_VALUE
        ));

        for (int i = 1; i <= tails; i++) {
            attr.addPermanentModifier(new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath("origins-diversity", "kitsune_hearts" + i),
                    2.0,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }
}