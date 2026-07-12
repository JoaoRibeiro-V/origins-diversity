package com.origins_diversity.client.mixin;

import net.minecraft.client.util.math.MatrixStack;
import io.github.apace100.apoli.power.PowerTypeReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context context, PlayerEntityModel<AbstractClientPlayerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    private static final Map<UUID, FoxEntity> fakeFoxes = new HashMap<>();

    @Inject(method = "render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void renderInject(Entity entity, float f, float g, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, CallbackInfo ci){
        boolean hasFoxForm = new PowerTypeReference<>(new Identifier("origins-diversity","kitsune/kitsune_fox_form")).isActive(entity);
        if (!hasFoxForm) return;
        PlayerEntity player = (PlayerEntity)entity;

        FoxEntity fox = fakeFoxes.computeIfAbsent(player.getUuid(), id -> new FoxEntity(EntityType.FOX, player.getWorld()));
        fox.handSwinging = player.handSwinging;
        fox.handSwingTicks = player.handSwingTicks;
        fox.bodyYaw = player.bodyYaw;
        fox.headYaw = player.headYaw;
        fox.prevBodyYaw = player.prevBodyYaw;
        fox.prevHeadYaw = player.prevHeadYaw;
        fox.setOnGround(player.isOnGround());
        fox.setVelocity(player.getVelocity());
        fox.setSneaking(player.isSneaking());
        fox.setSprinting(player.isSprinting());
        fox.setStackInHand(player.getActiveHand(), player.getStackInHand(player.getActiveHand()));
        fox.setPose(player.getPose());
        fox.equipStack(EquipmentSlot.MAINHAND, player.getStackInHand(player.getActiveHand()));

        float speed1 = (float) MathHelper.magnitude(player.getX() - player.prevX, 0.0F, player.getZ() - player.prevZ);
        float speed2 = Math.min(speed1 * 2.25F, 1.0F);

        fox.limbAnimator.updateLimbs(speed2, 0.4F);

        EntityRenderer<? super FoxEntity> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(fox);
        renderer.render(fox,f,g,poseStack,multiBufferSource, WorldRenderer.getLightmapCoordinates(player.getWorld(), BlockPos.ofFloored(player.getPos())));
        ci.cancel();
    }

}
