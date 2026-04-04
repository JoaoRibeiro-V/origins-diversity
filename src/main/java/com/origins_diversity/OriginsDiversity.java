package com.origins_diversity;

import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.Extra.Particles;
import com.origins_diversity.Data.SculkServantTameData;
import com.origins_diversity.PowerHandlers.SculkSummonListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.warden.Warden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class OriginsDiversity implements ModInitializer {
	public static final String MOD_ID = "origins-diversity";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Wrapper for Origins Diversity initialized");
        // This DefaultParticleType gets called when you want to use your particle in code.


        // Register our custom particle type in the mod initializer.
        Particles.register();
        SculkSummonListener.register();
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(entity instanceof SculkServantEntity servant)) return;
            if (!(entity.level() instanceof ServerLevel serverLevel)) return;
            if (!(source.getEntity() instanceof ServerPlayer killer)) return;
            String summonerTag = "summoner_" + killer.getStringUUID();
            if (servant.getTags().contains(summonerTag)) {
                SculkServantTameData.get(serverLevel.getServer()).markTamed(killer.getUUID(), serverLevel.getServer());
            }
        });
        FabricDefaultAttributeRegistry.register(ModEntities.SCULK_SERVANT, Warden.createAttributes());
	}

}