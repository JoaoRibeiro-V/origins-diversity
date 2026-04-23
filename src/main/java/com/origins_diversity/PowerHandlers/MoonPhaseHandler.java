package com.origins_diversity.PowerHandlers;

import com.origins_diversity.PowerHandlers.CelestialLinkHandler;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerReference;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class MoonPhaseHandler {

    private static final ResourceLocation LUNAR_EMPOWERMENT =
            ResourceLocation.fromNamespaceAndPath("origins-diversity", "starborn/lunar_empowerment");

    // all effects lunar empowerment can apply
    private static final List<Holder<MobEffect>> LUNAR_EFFECTS = List.of(
            MobEffects.DAMAGE_BOOST,
            MobEffects.MOVEMENT_SPEED,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.REGENERATION
    );

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.tickCount % 100 != 0) continue;

                PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {
                    if (holder.getPowerType(PowerReference.resource(LUNAR_EMPOWERMENT).getPower()) == null) return;

                    ServerLevel world = player.serverLevel();
                    long time = world.getDayTime() % 24000;

                    if (time < 12300) {
                        // daytime strip all lunar effects
                        removeLunarEffects(player);
                        return;
                    }

                    applyMoonBuffs(player, world.getMoonPhase());
                });
            }
        });
    }

    private static void applyMoonBuffs(ServerPlayer player, int phase) {
        // clear previous lunar effects first so phase transitions are clean
        removeLunarEffects(player);

        switch (phase) {
            case 0 -> { // full moon
                apply(player, MobEffects.DAMAGE_BOOST, 1);
                apply(player, MobEffects.MOVEMENT_SPEED, 1);
                apply(player, MobEffects.DAMAGE_RESISTANCE, 0);
            }
            case 1, 7 -> { // gibbous
                apply(player, MobEffects.DAMAGE_BOOST, 0);
                apply(player, MobEffects.MOVEMENT_SPEED, 0);
            }
            case 2, 6 -> apply(player, MobEffects.DAMAGE_BOOST, 0); // half
            case 3, 5 -> apply(player, MobEffects.REGENERATION, 0); // crescent
            // case 4 = new moon, nothing
        }

        // share active buffs with linked target
        CelestialLinkHandler.syncEffectsToLinkedTarget(player);
    }

    private static void removeLunarEffects(ServerPlayer player) {
        LUNAR_EFFECTS.forEach(player::removeEffect);
    }

    private static void apply(ServerPlayer player, Holder<MobEffect> effect, int amplifier) {
        // 220 ticks duration
        player.addEffect(new MobEffectInstance(effect, 220, amplifier, false, false));
    }
}