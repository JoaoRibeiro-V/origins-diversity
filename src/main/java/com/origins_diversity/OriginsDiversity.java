package com.origins_diversity;

import com.origins_diversity.Block.ModBlocks;
import com.origins_diversity.Entities.ModEntities;
import com.origins_diversity.Entities.ModEntityAttributes;
import com.origins_diversity.Events.ModEntityEvents;
import com.origins_diversity.Extra.ModParticles;
import com.origins_diversity.GameRules.ModGameRules;
import com.origins_diversity.PowerHandlers.SculkSummonListener;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


        // Register mod stuff
        ModParticles.register();
        SculkSummonListener.register();
        ModEntities.register();
        ModEntityAttributes.register();
        ModEntityEvents.register();
        ModBlocks.registerModBlocks();
        ModGameRules.register();
    }

}