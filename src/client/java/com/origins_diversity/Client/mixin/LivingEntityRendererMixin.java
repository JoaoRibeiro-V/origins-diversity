package com.origins_diversity.client.mixin;

import io.github.apace100.apoli.power.PowerTypeReference;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(
            method = "hasLabel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideNameTag(
            Entity entity,
            CallbackInfoReturnable<Boolean> cir
    ) {

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null)
            return;

        boolean hasConfusion =
                new PowerTypeReference<>(
                        new Identifier(
                                "origins-diversity",
                                "kitsune/illusion_affect"
                        )
                ).isActive(mc.player);


        if (hasConfusion) {
            cir.setReturnValue(false);
        }
    }
}