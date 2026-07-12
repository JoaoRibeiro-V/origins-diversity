package com.origins_diversity.client.Renderer;

import com.origins_diversity.Entities.SculkZombieEntity;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.Identifier;

public class SculkZombieRenderer extends MobEntityRenderer<SculkZombieEntity, ZombieEntityModel<SculkZombieEntity>> {

    private static final Identifier TEXTURE =
            new Identifier("origins-diversity", "textures/entity/sculk_zombie/sculk_zombie.png");

    public SculkZombieRenderer(EntityRendererFactory.Context context) {
        super(context, new ZombieEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    public Identifier getTexture(SculkZombieEntity entity) {
        return TEXTURE;
    }
}