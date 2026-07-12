package com.origins_diversity.Entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<SculkServantEntity> SCULK_SERVANT = Registry.register(
            Registries.ENTITY_TYPE  ,
            new Identifier("origins-diversity", "sculk_servant"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SculkServantEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 2.9f))
                    .build()
    );

    public static final EntityType<SculkZombieEntity> SCULK_ZOMBIE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("origins-diversity", "sculk_zombie"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SculkZombieEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static void register() {} // triggers static init
}