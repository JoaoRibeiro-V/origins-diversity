package com.origins_diversity.Entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.warden.Warden;

public class ModEntityAttributes {
    public static void register() {
        FabricDefaultAttributeRegistry.register(ModEntities.SCULK_SERVANT, Warden.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.SCULK_ZOMBIE, Zombie.createAttributes());
    }
}
