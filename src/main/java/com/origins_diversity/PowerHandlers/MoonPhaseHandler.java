package com.origins_diversity.PowerHandlers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerTypeReference;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.List;

public class MoonPhaseHandler {

    private static final Identifier LUNAR_EMPOWERMENT =
            new Identifier("origins-diversity", "starborn/lunar_empowerment");

    private static final PowerTypeReference<?> LUNAR_EMPOWERMENT_POWER = new PowerTypeReference<>(LUNAR_EMPOWERMENT);

    // all effects lunar empowerment can apply
    private static final List<StatusEffect> LUNAR_EFFECTS = List.of(
            StatusEffects.STRENGTH,
            StatusEffects.SPEED,
            StatusEffects.RESISTANCE,
            StatusEffects.REGENERATION
    );

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.age % 100 != 0) continue;

                PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {
                    if (!holder.hasPower(LUNAR_EMPOWERMENT_POWER)) return;

                    ServerWorld world = (ServerWorld) player.getWorld();
                    long time = world.getTimeOfDay() % 24000;

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

    private static void applyMoonBuffs(ServerPlayerEntity player, int phase) {
        // clear previous lunar effects first so phase transitions are clean
        removeLunarEffects(player);

        switch (phase) {
            case 0 -> { // full moon
                apply(player, StatusEffects.STRENGTH, 1);
                apply(player, StatusEffects.SPEED, 1);
                apply(player, StatusEffects.RESISTANCE, 0);
            }
            case 1, 7 -> { // gibbous
                apply(player, StatusEffects.STRENGTH, 0);
                apply(player, StatusEffects.SPEED, 0);
            }
            case 2, 6 -> apply(player, StatusEffects.STRENGTH, 0); // half
            case 3, 5 -> apply(player, StatusEffects.REGENERATION, 0); // crescent
            // case 4 = new moon, nothing
        }

        // share active buffs with linked target
        CelestialLinkHandler.syncEffectsToLinkedTargets(player);
    }

    private static void removeLunarEffects(ServerPlayerEntity player) {
        LUNAR_EFFECTS.forEach(player::removeStatusEffect);
    }

    private static void apply(ServerPlayerEntity player, StatusEffect effect, int amplifier) {
        // 220 ticks duration
        player.addStatusEffect(new StatusEffectInstance(effect, 220, amplifier, false, false));
    }
}
