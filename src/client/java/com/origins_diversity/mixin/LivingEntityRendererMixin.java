package com.origins_diversity.mixin;

import io.github.apace100.apoli.power.PowerReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.renderer.entity.LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends net.minecraft.world.entity.LivingEntity, M extends EntityModel<T>> {
    @Inject(method="shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void shouldShowName(net.minecraft.world.entity.LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player==null) return;
        boolean hasConfusion = PowerReference.of(ResourceLocation.fromNamespaceAndPath("origins-diversity","kitsune/illusion_affect")).isActive(mc.player);
        if(hasConfusion){
            cir.cancel();
        };
    }
}
