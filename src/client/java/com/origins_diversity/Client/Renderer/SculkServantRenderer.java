package com.origins_diversity.client.Renderer;


import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.client.Model.SculkServantModel;


import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;



public class SculkServantRenderer
        extends MobEntityRenderer<
        SculkServantEntity,
        SculkServantModel<SculkServantEntity>
        > {


    private static final Identifier BASE =
            new Identifier(
                    "origins-diversity",
                    "textures/entity/sculk_servant/sculk_servant.png"
            );


    private static final Identifier BIO =
            new Identifier(
                    "origins-diversity",
                    "textures/entity/sculk_servant/sculk_servant_bioluminescent_layer.png"
            );


    public SculkServantRenderer(
            EntityRendererFactory.Context context
    ) {

        super(
                context,
                new SculkServantModel<>(
                        context.getPart(
                                EntityModelLayers.WARDEN
                        )
                ),
                0.9F
        );


        this.addFeature(
                new SculkServantFeatureRenderer(
                        this,
                        BIO
                )
        );
    }



    @Override
    public Identifier getTexture(
            SculkServantEntity entity
    ) {
        return BASE;
    }
}