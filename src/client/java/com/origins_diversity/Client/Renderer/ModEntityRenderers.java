package com.origins_diversity.client.Renderer;

import com.origins_diversity.Entities.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;

public class ModEntityRenderers {
    public static void register() {
        EntityRendererRegistry.register(ModEntities.SCULK_SERVANT, SculkServantRenderer::new);

        EntityRendererRegistry.register(
                ModEntities.SCULK_ZOMBIE,
                SculkZombieRenderer::new
        );
    }
}