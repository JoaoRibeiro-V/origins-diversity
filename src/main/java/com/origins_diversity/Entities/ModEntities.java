package com.origins_diversity.Entities;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static final EntityType<SculkServantEntity> SCULK_SERVANT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("origins-diversity", "sculk_servant"),
            EntityType.Builder.<SculkServantEntity>of(SculkServantEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 2.9f).build("sculk_servant")
    );

    public static final EntityType<SculkZombieEntity> SCULK_ZOMBIE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("origins-diversity", "sculk_zombie"),
            EntityType.Builder.<SculkZombieEntity>of(SculkZombieEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).build("sculk_zombie")
    );

    public static void register() {} // triggers static init
}