package com.origins_diversity.client.mixin;

import io.github.apace100.apoli.power.PowerTypeReference;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public abstract class CameraMixin {

    @Inject(
            method = "changeLookDirection",
            at = @At("HEAD"),
            cancellable = true
    )
    private void originsDiversity$invertCamera(
            double cursorDeltaX,
            double cursorDeltaY,
            CallbackInfo ci
    ) {

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null)
            return;


        boolean hasConfusion =
                new PowerTypeReference<>(
                        new Identifier(
                                "origins-diversity",
                                "abysswyrm/confusion"
                        )
                ).isActive(mc.player);


        if (!hasConfusion)
            return;


        mc.player.setYaw(
                mc.player.getYaw() - (float)(cursorDeltaX * 0.125F)
        );

        mc.player.setPitch(
                mc.player.getPitch() - (float)(cursorDeltaY * 0.125F)
        );


        ci.cancel();
    }
}