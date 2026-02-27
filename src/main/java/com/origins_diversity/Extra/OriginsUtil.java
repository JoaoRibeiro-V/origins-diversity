package com.origins_diversity.Extra;

import io.github.apace100.apoli.command.ResourceCommand;
import io.github.apace100.apoli.command.argument.PowerArgumentType;
import io.github.apace100.apoli.command.argument.PowerHolderArgumentType;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.PowerUtil;
import io.github.apace100.origins.origin.Origin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.intellij.lang.annotations.Identifier;

import java.util.Optional;

public class OriginsUtil {
    public static boolean hasOrigin(Player player, String namespace, String path){
        ResourceLocation target = ResourceLocation.fromNamespaceAndPath(namespace, path);
        for(Origin origin : Origin.get(player).values()){
            if(origin.getId().equals(target)){
                return true;
            }
        }
        return false;
    }
    public static int getResourceValue(Player player, String namespace, String path){
        Power power = PowerManager.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
        PowerType powerType = PowerUtil.getNullablePowerType(power, player);

        if (powerType != null) {
            return PowerUtil.getResourceValue(powerType);
        }
        else {
            return -1;
        }
    }
}
