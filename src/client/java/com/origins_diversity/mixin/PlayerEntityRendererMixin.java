package com.origins_diversity.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.mixin.EntityAccessor;
import io.github.apace100.apoli.power.PowerReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerEntityRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    private static final Map<UUID, Fox> fakeFoxes = new HashMap<>();

    @Inject(method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    private void renderInject(Entity entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci){
        boolean hasFoxForm = PowerReference.of(ResourceLocation.fromNamespaceAndPath("origins-diversity","kitsune/kitsune_fox_form")).isActive(entity);
        if (!hasFoxForm) return;
        Player player = (Player)entity;

        Fox fox = fakeFoxes.computeIfAbsent(player.getUUID(), id -> new Fox(EntityType.FOX, player.level()));
        fox.swinging = player.swinging;
        fox.swingTime = player.swingTime;
        fox.yBodyRot = player.yBodyRot;
        fox.yHeadRot = player.yHeadRot;
        fox.yBodyRotO = player.yBodyRotO;
        fox.yHeadRotO = player.yHeadRotO;
        fox.setOnGround(player.onGround());
        fox.setDeltaMovement(player.getDeltaMovement());
        fox.setIsCrouching(player.isCrouching());
        fox.setSprinting(player.isSprinting());
        fox.setItemInHand(player.getUsedItemHand(), player.getItemInHand(player.getUsedItemHand()));
        fox.setPose(player.getPose());
        fox.setItemSlot(EquipmentSlot.MAINHAND, player.getItemInHand(player.getUsedItemHand()));

        float speed1 = (float) Mth.length(player.getX() - player.xo, 0.0F, player.getZ() - player.zo);
        float speed2 = Math.min(speed1 * 2.25F, 1.0F);

        fox.walkAnimation.update(speed2, 0.4F);

        EntityRenderer<? super Fox> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(fox);
        renderer.render(fox,f,g,poseStack,multiBufferSource, LevelRenderer.getLightColor(player.level(), BlockPos.containing(player.position())));
        ci.cancel();
    }

}
