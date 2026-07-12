package com.origins_diversity.Entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.mob.ZombieEntity;

public class ModEntityAttributes {
    public static void register() {
        FabricDefaultAttributeRegistry.register(ModEntities.SCULK_SERVANT, SculkServantEntity.createSculkServantAttributes());
        FabricDefaultAttributeRegistry.register(
                ModEntities.SCULK_ZOMBIE,
                SculkZombieEntity.createSculkZombieAttributes()
        );
    }
}
