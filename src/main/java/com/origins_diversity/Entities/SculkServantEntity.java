package com.origins_diversity.Entities;

import com.origins_diversity.Data.SculkServantTameData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.Objects;

public class SculkServantEntity extends WardenEntity {
    public SculkServantEntity(EntityType<? extends WardenEntity> type, World level) {
        super(type, level);
    }

    // Prevent actually hitting a tamed player
    @Override
    public boolean tryAttack(Entity target) {
        if (target instanceof ServerPlayerEntity player && getEntityWorld() instanceof ServerWorld serverLevel
                && SculkServantTameData.get(serverLevel.getServer()).isTamed(player.getUuid())) {
            return false;
        }
        return super.tryAttack(target);
    }
    public static DefaultAttributeContainer.Builder createSculkServantAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 550.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
    }
    // Every tick, wipe anger toward any tamed player so it never chases them
    @Override
    public void tick() {
        super.tick();

        if (!(getEntityWorld() instanceof ServerWorld serverLevel)) return;
        SculkServantTameData data = SculkServantTameData.get(serverLevel.getServer());
        for (ServerPlayerEntity player : serverLevel.getPlayers()) {
            if (data.isTamed(player.getUuid())) {
                getAngerManager().removeSuspect(player);
            }
        }
    }

}
