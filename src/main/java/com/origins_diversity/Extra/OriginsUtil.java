package com.origins_diversity.Extra;

import io.github.apace100.origins.origin.Origin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

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
}
