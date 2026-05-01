package com.origins_diversity.Events;

import com.origins_diversity.OriginsDiversity;
import com.origins_diversity.PowerHandlers.CelestialLinkHandler;
import com.origins_diversity.PowerHandlers.MoonPhaseHandler;
import com.origins_diversity.PowerHandlers.SupernovaHandler;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

public class StarbornEvents {

    public static void register() {
        MoonPhaseHandler.register();
        onDeath();
        link();
    }

    private static void onDeath() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(entity instanceof ServerPlayer player)) return;
            SupernovaHandler.onDeath(player);
        });
    }

    private static void link() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
           if(CelestialLinkHandler.getStarbornLink(entity) instanceof ServerPlayer starborn) {
               CelestialLinkHandler.unlink(starborn, entity);
           }
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if ((entity instanceof ServerPlayer target)){
                if (CelestialLinkHandler.hasCelestialLink(target)){
                    CelestialLinkHandler.unlinkAll(target);
                    OriginsDiversity.LOGGER.info("Unlinked Starborn links from death");
                }
            }

        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!player.isShiftKeyDown()) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer starborn)) return InteractionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return InteractionResult.PASS;
            if (!CelestialLinkHandler.hasCelestialLink(starborn)) return InteractionResult.PASS;

            CelestialLinkHandler.tryInteract(starborn, target);
            return InteractionResult.SUCCESS;
        });
    }
}