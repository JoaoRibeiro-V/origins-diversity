package com.origins_diversity.PowerHandlers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.ResourcePower;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SupernovaHandler {

    private static final Identifier STARLIGHT_ENERGY =
            new Identifier("origins-diversity", "starborn/starlight_energy");

    private static final PowerTypeReference<ResourcePower> STARLIGHT_ENERGY_POWER = new PowerTypeReference<>(STARLIGHT_ENERGY);

    public static void onDeath(ServerPlayerEntity player) {
        PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder -> {
            if (!holder.hasPower(STARLIGHT_ENERGY_POWER)) return;
            ResourcePower resource = holder.getPower(STARLIGHT_ENERGY_POWER);
            int energy = resource.getValue();
            if (energy < 100) return;
            resource.setValue(energy - 100);
            ServerWorld level = (ServerWorld) player.getWorld();
            boolean griefing = level.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);

            level.createExplosion(
                    player,
                    player.getX(), player.getY(), player.getZ(),
                    6.0f,
                    true,
                    griefing ? World.ExplosionSourceType.MOB : World.ExplosionSourceType.NONE
            );
        });
    }
}
