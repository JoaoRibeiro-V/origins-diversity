package com.origins_diversity.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import io.github.apace100.apoli.power.PowerReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class CameraMixin {
    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Inject(method="turnPlayer", at = @At("HEAD"), cancellable = true)
    private void originsDiversity$invertCamera(double d, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player==null)return;
        LocalPlayer player = mc.player;

        boolean hasConfusion = PowerReference.of(ResourceLocation.fromNamespaceAndPath("origins-diversity","abysswyrm/confusion")).isActive(player);
        if (!hasConfusion) return;
        player.setYRot((float) (player.getYRot() - accumulatedDX * 0.125f));
        player.setXRot((float) (player.getXRot() - accumulatedDY * 0.125f));
        ci.cancel();
    }
}
