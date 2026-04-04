package com.origins_diversity.Entities;

import com.origins_diversity.Data.SculkServantTameData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class SculkServantEntity extends Warden {
    public SculkServantEntity(EntityType<? extends Warden> type, Level level) {
        super(type, level);
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(600.0);
        this.setHealth(600.0f);
    }

    // Prevent actually hitting a tamed player
    @Override
    public boolean doHurtTarget(Entity target) {
        if (target instanceof ServerPlayer player
                && level() instanceof ServerLevel serverLevel
                && SculkServantTameData.get(serverLevel.getServer()).isTamed(player.getUUID())) {
            return false;
        }
        return super.doHurtTarget(target);
    }

    // Every tick, wipe anger toward any tamed player so it never chases them
    @Override
    public void tick() {
        super.tick();

        if (!(level() instanceof ServerLevel serverLevel)) return;
        SculkServantTameData data = SculkServantTameData.get(serverLevel.getServer());
        for (ServerPlayer player : serverLevel.players()) {
            if (data.isTamed(player.getUUID())) {
                getAngerManagement().clearAnger(player);
            }
        }
    }

}
