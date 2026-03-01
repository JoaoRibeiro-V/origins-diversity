package com.origins_diversity;

import com.origins_diversity.Client.ClientPowerHandlers;
import com.origins_diversity.Client.ParticleFactories;
import com.origins_diversity.Client.PowerHandlers.Kitsune.KitsuneIllusionManager;
import com.origins_diversity.Client.PowerHandlers.Kitsune.KitsuneIllusionRenderer;
import io.github.apace100.apoli.power.PowerReference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.resources.ResourceLocation;

public class OriginsDiversityClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        // For this example, we will use the end rod particle behaviour.
        ParticleFactories.registerAll();
        ClientPowerHandlers.registerPowers();
	}
}