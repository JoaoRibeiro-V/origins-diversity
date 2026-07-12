package com.origins_diversity.client.Client;

import com.origins_diversity.client.Client.PowerHandlers.Kitsune.KitsuneIllusionManager;
import com.origins_diversity.client.Client.PowerHandlers.Kitsune.KitsuneIllusionRenderer;
import io.github.apace100.apoli.power.PowerTypeReference;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ClientPowerHandlers {
    public static void registerPowers(){
        // KITSUNE
        WorldRenderEvents.AFTER_ENTITIES.register(context ->{
            MinecraftClient mc = MinecraftClient.getInstance();
            if(mc.player == null || mc.world == null) return;

            boolean hasConfusion = new PowerTypeReference<>(new Identifier("origins-diversity","kitsune/illusion_affect")).isActive(mc.player);
            if(!hasConfusion) return;
            KitsuneIllusionManager.tick(mc);
            KitsuneIllusionRenderer.render(context);
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context ->{
            MinecraftClient mc = MinecraftClient.getInstance();
            if(mc.player == null || mc.world == null) return;
            boolean hasKitsuneForm = new PowerTypeReference<>(new Identifier("origins-diversity","kitsune/kitsune_fox_form")).isActive(mc.player);
            if(!hasKitsuneForm) return;

        });
    }
}
