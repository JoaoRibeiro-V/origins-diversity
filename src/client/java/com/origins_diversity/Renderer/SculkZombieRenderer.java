package com.origins_diversity.Renderer;

import com.origins_diversity.Entities.SculkZombieEntity;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SculkZombieRenderer extends MobRenderer<SculkZombieEntity, ZombieModel<SculkZombieEntity>> {
    private static final ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath("origins-diversity", "textures/entity/sculk_zombie/sculk_zombie.png");
    public SculkZombieRenderer(EntityRendererProvider.Context context, ZombieModel<SculkZombieEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Override
    public ResourceLocation getTextureLocation(SculkZombieEntity entity) {
        return BASE;
    }
}
