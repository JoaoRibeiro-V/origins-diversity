package com.origins_diversity.Entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class SculkZombieEntity extends Zombie {

    public SculkZombieEntity(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(25.0);
        this.setHealth(25.0f);
    }
}
