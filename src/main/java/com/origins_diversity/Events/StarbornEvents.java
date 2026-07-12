package com.origins_diversity.Events;

import com.origins_diversity.OriginsDiversity;
import com.origins_diversity.PowerHandlers.CelestialLinkHandler;
import com.origins_diversity.PowerHandlers.MoonPhaseHandler;
import com.origins_diversity.PowerHandlers.SupernovaHandler;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;

public class StarbornEvents {

    public static void register() {
        MoonPhaseHandler.register();
        onDeath();
        link();
    }

    private static void onDeath() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return;
            SupernovaHandler.onDeath(player);
        });
    }

    private static void link() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            ServerPlayerEntity starborn = CelestialLinkHandler.getStarbornLink(entity);

            if (starborn != null) {
                CelestialLinkHandler.unlink(starborn, entity);
            }
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if ((entity instanceof ServerPlayerEntity target)){
                if (CelestialLinkHandler.hasCelestialLink(target)){
                    CelestialLinkHandler.unlinkAll(target);
                    OriginsDiversity.LOGGER.info("Unlinked Starborn links from death");
                }
            }

        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;
            if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
            if (!player.isSneaking()) return ActionResult.PASS;
            if (!(player instanceof ServerPlayerEntity starborn)) return ActionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return ActionResult.PASS;
            if (!CelestialLinkHandler.hasCelestialLink(starborn)) return ActionResult.PASS;

            CelestialLinkHandler.tryInteract(starborn, target);
            return ActionResult.SUCCESS;
        });
    }
}
