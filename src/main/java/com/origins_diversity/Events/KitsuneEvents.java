package com.origins_diversity.Events;

import com.origins_diversity.Extra.OriginsUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.ResourcePower;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.*;

public class KitsuneEvents {

    private static final Identifier TAILS_ID =
            new Identifier("origins-diversity", "kitsune/kitsune_tails");

    private static final PowerTypeReference<ResourcePower> TAILS_POWER = new PowerTypeReference<>(TAILS_ID);

    private static final UUID KITSUNE_HEARTS_ID = UUID.nameUUIDFromBytes("origins-diversity:kitsune_hearts".getBytes());

    private static final Map<UUID, Integer> lastTailCount = new HashMap<>();

    private static final Map<UUID, Integer> pendingRefresh = new HashMap<>();
    private static void putOnQueue(ServerPlayerEntity player) {
        if(!OriginsUtil.hasOrigin(player, "origins-diversity","kitsune")) return;
        pendingRefresh.put(player.getUuid(), 20);
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
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player != null) refreshTails(player);
            }

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkTailChange(player);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                refreshTails(handler.player));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            lastTailCount.remove(handler.player.getUuid());
            pendingRefresh.remove(handler.player.getUuid());
        });
    }

    private static void checkTailChange(ServerPlayerEntity player) {
        PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {
            if (!holder.hasPower(TAILS_POWER)) {
                // no longer kitsune, clean up if we were tracking them
                if (lastTailCount.containsKey(player.getUuid())) {
                    lastTailCount.remove(player.getUuid());
                    removeAllTailModifiers(player);
                }
                return;
            }

            ResourcePower resource = holder.getPower(TAILS_POWER);

            if (resource == null)
                return;

            int current = resource.getValue();
            if (lastTailCount.getOrDefault(player.getUuid(), -1) != current) {
                lastTailCount.put(player.getUuid(), current);
                applyTailModifiers(player, current);
            }
        });
    }

    private static void removeAllTailModifiers(ServerPlayerEntity player) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attr == null) return;

        attr.removeModifier(KITSUNE_HEARTS_ID);
        for (int i = 1; i <= 9; i++) {
            attr.removeModifier(UUID.nameUUIDFromBytes(("origins-diversity:kitsune_hearts" + i).getBytes()));
        }
    }

    private static void refreshTails(ServerPlayerEntity player) {
        PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {

            ResourcePower resource = holder.getPower(TAILS_POWER);

            if(resource == null) {
                System.out.println("TAIL POWER NULL");
                return;
            }

            int tails = resource.getValue();

            lastTailCount.put(player.getUuid(), tails);
            applyTailModifiers(player, tails);
        });
    }

    private static void applyTailModifiers(ServerPlayerEntity player, int tails) {

        if(player.isDead()) return;

        EntityAttributeInstance attr =
                player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);

        if(attr == null) return;


        removeAllTailModifiers(player);


        attr.addPersistentModifier(
                new EntityAttributeModifier(
                        KITSUNE_HEARTS_ID,
                        "kitsune_hearts",
                        -10.0,
                        EntityAttributeModifier.Operation.ADDITION
                )
        );


        for(int i = 1; i <= tails; i++) {

            attr.addPersistentModifier(
                    new EntityAttributeModifier(
                            UUID.nameUUIDFromBytes(
                                    ("origins-diversity:kitsune_hearts" + i).getBytes()
                            ),
                            "kitsune_hearts" + i,
                            2.0,
                            EntityAttributeModifier.Operation.ADDITION
                    )
            );
        }


        player.setHealth(Math.min(
                player.getHealth(),
                player.getMaxHealth()
        ));
    }
}
