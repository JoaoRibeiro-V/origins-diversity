package com.origins_diversity.client.Renderer;

import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.client.Model.SculkServantModel;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;


public class SculkServantFeatureRenderer
        extends FeatureRenderer<
        SculkServantEntity,
        SculkServantModel<SculkServantEntity>
        > {


    private final Identifier texture;


    public SculkServantFeatureRenderer(
            FeatureRendererContext<
                    SculkServantEntity,
                    SculkServantModel<SculkServantEntity>
                    > context,
            Identifier texture
    ) {

        super(context);

        this.texture = texture;
    }


    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider consumers,
            int light,
            SculkServantEntity entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {


        VertexConsumer vertex =
                consumers.getBuffer(
                        RenderLayer.getEntityTranslucentEmissive(texture)
                );


        getContextModel()
                .render(
                        matrices,
                        vertex,
                        light,
                        OverlayTexture.DEFAULT_UV,
                        1.0F,
                        1.0F,
                        1.0F,
                        1.0F
                );
    }
}