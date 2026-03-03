package com.origins_diversity.Client;

import com.origins_diversity.Client.PowerHandlers.Kitsune.KitsuneIllusionManager;
import com.origins_diversity.Client.PowerHandlers.Kitsune.KitsuneIllusionRenderer;
import io.github.apace100.apoli.power.PowerReference;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ClientPowerHandlers {
    public static void registerPowers(){
        // KITSUNE
        WorldRenderEvents.AFTER_ENTITIES.register(context ->{
            Minecraft mc = Minecraft.getInstance();
            if(mc.player == null || mc.level == null) return;

            boolean hasConfusion = PowerReference.of(ResourceLocation.fromNamespaceAndPath("origins-diversity","kitsune/illusion_affect")).isActive(mc.player);
            if(!hasConfusion) return;
            KitsuneIllusionManager.tick(mc);
            KitsuneIllusionRenderer.render(context);
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context ->{
            Minecraft mc = Minecraft.getInstance();
            if(mc.player == null || mc.level == null) return;
            boolean hasKitsuneForm = PowerReference.of(ResourceLocation.fromNamespaceAndPath("origins-diversity","kitsune/kitsune_fox_form")).isActive(mc.player);
            if(!hasKitsuneForm) return;

        });
    }
}
