package com.origins_diversity.Events;

import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.Data.SculkServantTameData;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

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
    }
}