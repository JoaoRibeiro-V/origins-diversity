package com.origins_diversity;

import com.origins_diversity.Client.ParticleFactories;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.EndRodParticle;

public class OriginsDiversityClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        // For this example, we will use the end rod particle behaviour.
        ParticleFactories.registerAll();
	}
}