package com.origins_diversity.client.Model;

import com.origins_diversity.Entities.SculkServantEntity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.entity.animation.WardenAnimations;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class SculkServantModel<T extends SculkServantEntity> extends SinglePartEntityModel<T> {

    private final ModelPart root;

    protected final ModelPart bone;
    protected final ModelPart body;
    protected final ModelPart head;

    protected final ModelPart rightArm;
    protected final ModelPart leftArm;

    protected final ModelPart rightLeg;
    protected final ModelPart leftLeg;

    protected final ModelPart rightTendril;
    protected final ModelPart leftTendril;

    private final List<ModelPart> tendrils;


    public SculkServantModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);

        this.root = root;

        this.bone = root.getChild("bone");
        this.body = bone.getChild("body");
        this.head = body.getChild("head");

        this.rightArm = body.getChild("right_arm");
        this.leftArm = body.getChild("left_arm");

        this.rightLeg = bone.getChild("right_leg");
        this.leftLeg = bone.getChild("left_leg");

        this.rightTendril = head.getChild("right_tendril");
        this.leftTendril = head.getChild("left_tendril");


        this.tendrils = ImmutableList.of(
                leftTendril,
                rightTendril
        );
    }


    @Override
    public void setAngles(
            T entity,
            float limbAngle,
            float limbDistance,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {

        this.getPart()
                .traverse()
                .forEach(ModelPart::resetTransform);


        head.yaw = headYaw * ((float)Math.PI / 180F);
        head.pitch = headPitch * ((float)Math.PI / 180F);


        float time = animationProgress * 0.1F;

        head.roll += 0.06F * MathHelper.cos(time);
        head.pitch += 0.06F * MathHelper.sin(time);


        body.roll += 0.025F * MathHelper.sin(time);
        body.pitch += 0.025F * MathHelper.cos(time);


        setLimbAngles(
                limbAngle,
                limbDistance
        );


        setTendrils(
                entity,
                animationProgress
        );
    }


    private void setLimbAngles(
            float angle,
            float distance
    ) {

        float f = Math.min(
                0.5F,
                3.0F * distance
        );


        float g = angle * 0.8662F;

        float cos = MathHelper.cos(g);
        float sin = MathHelper.sin(g);


        leftLeg.pitch = cos * f;
        rightLeg.pitch = MathHelper.cos((float) (g + Math.PI)) * f;


        leftArm.pitch = -(0.8F * cos * f);
        rightArm.pitch = -(0.8F * sin * f);
    }


    private void setTendrils(
            T entity,
            float animationProgress
    ) {

        float value = MathHelper.sin(animationProgress * 0.15F) * 0.1F;

        leftTendril.pitch = value;
        rightTendril.pitch = -value;
    }


    @Override
    public ModelPart getPart() {
        return root;
    }


    public List<ModelPart> getTendrils() {
        return tendrils;
    }
}