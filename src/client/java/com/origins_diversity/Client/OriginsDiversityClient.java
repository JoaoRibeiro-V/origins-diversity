package com.origins_diversity.client;

import com.origins_diversity.client.Client.ClientPowerHandlers;
import com.origins_diversity.client.Client.ParticleFactories;
import com.origins_diversity.client.Renderer.ModEntityRenderers;
import net.fabricmc.api.ClientModInitializer;

public class OriginsDiversityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        // For this example, we will use the end rod particle behaviour.
        ParticleFactories.registerAll();
        ClientPowerHandlers.registerPowers();
        ModEntityRenderers.register();
    }
}