package com.origins_diversity.Extra;

import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.ResourcePower;
import io.github.apace100.origins.origin.Origin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;


public class OriginsUtil {

    public static boolean hasOrigin(
            PlayerEntity player,
            String namespace,
            String path
    ) {
        Identifier target = new Identifier(namespace, path);

        for (Origin origin : Origin.get(player).values()) {
            if (origin.getIdentifier().equals(target)) {
                return true;
            }
        }

        return false;
    }


    public static int getResourceValue(
            PlayerEntity player,
            String namespace,
            String path
    ) {
        PowerTypeReference<ResourcePower> powerType =
                new PowerTypeReference<>(
                        new Identifier(namespace, path)
                );

        ResourcePower power = powerType.get(player);

        if (power != null) {
            return power.getValue();
        }

        return -1;
    }
}