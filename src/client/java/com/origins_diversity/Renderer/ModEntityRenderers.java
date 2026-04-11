package com.origins_diversity.Renderer;

import com.origins_diversity.Entities.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;

public class ModEntityRenderers {

    public static void register() {
        EntityRendererRegistry.register(ModEntities.SCULK_SERVANT, SculkServantRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_ZOMBIE,
                ctx -> new SculkZombieRenderer(ctx, new ZombieModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5f));
    }
}