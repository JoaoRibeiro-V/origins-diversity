package com.origins_diversity.Renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.origins_diversity.Entities.SculkServantEntity;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WardenEmissiveLayer;
import net.minecraft.resources.ResourceLocation;

public class SculkServantRenderer extends MobRenderer<SculkServantEntity, WardenModel<SculkServantEntity>> {

    private static final ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath("origins-diversity", "textures/entity/sculk_servant/sculk_servant.png");
    private static final ResourceLocation BIO = ResourceLocation.fromNamespaceAndPath("origins-diversity", "textures/entity/sculk_servant/sculk_servant_bioluminescent_layer.png");
    private static final ResourceLocation HEART = ResourceLocation.fromNamespaceAndPath("origins-diversity", "textures/entity/sculk_servant/sculk_servant_heart.png");
    private static final ResourceLocation SPOTS_1 = ResourceLocation.fromNamespaceAndPath("origins-diversity", "textures/entity/sculk_servant/sculk_servant_pulsating_spots_1.png");
    private static final ResourceLocation SPOTS_2 = ResourceLocation.fromNamespaceAndPath("origins-diversity", "textures/entity/sculk_servant/sculk_servant_pulsating_spots_2.png");

    public SculkServantRenderer(EntityRendererProvider.Context context) {
        super(context, new WardenModel<>(context.bakeLayer(ModelLayers.WARDEN)), 0.9f);

        // Bioluminescent — always visible
        this.addLayer(new WardenEmissiveLayer<>(this, BIO,
                (entity, renderState, ageInTicks) -> 1.0f,
                WardenModel::getBioluminescentLayerModelParts));

        // Spots 1 — calm
        this.addLayer(new WardenEmissiveLayer<>(this, SPOTS_1,
                (entity, renderState, ageInTicks) ->
                        entity.getClientAngerLevel() < 40 ? 1.0f : 0.0f,
                WardenModel::getPulsatingSpotsLayerModelParts));

        // Spots 2 — angry
        this.addLayer(new WardenEmissiveLayer<>(this, SPOTS_2,
                (entity, renderState, ageInTicks) ->
                        entity.getClientAngerLevel() >= 40 ? 1.0f : 0.0f,
                WardenModel::getPulsatingSpotsLayerModelParts));

        // Heart — max anger
        this.addLayer(new WardenEmissiveLayer<>(this, HEART,
                (entity, renderState, ageInTicks) ->
                        entity.getClientAngerLevel() >= 80 ? 1.0f : 0.0f,
                WardenModel::getHeartLayerModelParts));

        // Overlay for ritual summon
        this.addLayer(new net.minecraft.client.renderer.entity.layers.RenderLayer<>(this) {
            private static final ResourceLocation PURPLE_OVERLAY = ResourceLocation.fromNamespaceAndPath(
                    "origins-diversity", "textures/entity/sculk_servant/sculk_servant_overlay.png"
            );

            @Override
            public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, SculkServantEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
                if (!entity.getTags().contains("sculk_ritual_active")) return; // skip after ritual
                VertexConsumer consumer = bufferSource.getBuffer( net.minecraft.client.renderer.RenderType.entityTranslucentEmissive(PURPLE_OVERLAY));
                // K is : 0xAA_RR_GG_BB on the hexadecimal scale
                getParentModel().renderToBuffer(poseStack, consumer, packedLight, LightTexture.FULL_BRIGHT, 0xFA_00_00_00);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(SculkServantEntity entity) {
        return BASE;
    }
}