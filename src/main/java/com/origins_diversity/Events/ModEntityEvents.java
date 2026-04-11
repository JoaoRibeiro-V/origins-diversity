package com.origins_diversity.Events;

import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.Data.SculkServantTameData;
import com.origins_diversity.Entities.SculkZombieEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class ModEntityEvents {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(entity instanceof SculkServantEntity servant)) return;
            if (!(entity.level() instanceof ServerLevel serverLevel)) return;
            if (!(source.getEntity() instanceof ServerPlayer killer)) return;
            String summonerTag = "summoner_" + killer.getStringUUID();
            if (servant.getTags().contains(summonerTag)) {
                SculkServantTameData.get(serverLevel.getServer()).markTamed(killer.getUUID(), serverLevel.getServer());
            }
        });
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damage, absorbed) -> {
            if (!(entity.level() instanceof ServerLevel serverLevel)) return;
            if (source.getEntity() == null) return;
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            // find nearby sculk zombies whose summoner is this entity
            serverLevel.getEntitiesOfClass(SculkZombieEntity.class,
                    entity.getBoundingBox().inflate(32),
                    zombie -> {
                        UUID sid = zombie.getSummonerUUID();
                        return sid != null && sid.equals(entity.getUUID());
                    }
            ).forEach(zombie -> zombie.setTarget(attacker));
        });
    }
}