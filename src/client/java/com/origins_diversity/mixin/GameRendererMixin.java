package com.origins_diversity.mixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;

import static com.origins_diversity.GameRules.ModGameRules.PREVENT_MOUNT_DAMAGE;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final
    Minecraft minecraft;

    public Entity crosshairPickEntity;

    @Inject(method = "pick(F)V", at = @At("TAIL"))
    private void originsDiversity$ignoreMountHit(float partialTick, CallbackInfo ci) {
        if (minecraft.player == null) return;
        if (!minecraft.player.level().getGameRules().getRule(PREVENT_MOUNT_DAMAGE).get()) return;
        if (!(minecraft.hitResult instanceof EntityHitResult entityHit)) return;

        Entity target = entityHit.getEntity();
        Entity self = minecraft.player;
        if (target.getVehicle() != self) return;
        LocalPlayer player = minecraft.player;
        double reach = minecraft.player.blockInteractionRange();
        Vec3 from = player.getEyePosition(partialTick);
        Vec3 look = player.getViewVector(partialTick);
        Vec3 to = from.add(look.x * reach, look.y * reach, look.z * reach);
        BlockHitResult blockHit = minecraft.level.clip(
                new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)
        );
        double blockDist = blockHit.getType() == HitResult.Type.MISS
                ? reach * reach
                : from.distanceToSqr(blockHit.getLocation());

        HitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                player,
                from,
                to,
                new AABB(from, to).inflate(1.0),
                e -> !e.isSpectator() && e.isPickable() && e != target, // ignora o passageiro
                blockDist
        );

        if (entityHitResult != null) {
            minecraft.hitResult = entityHitResult;
            minecraft.crosshairPickEntity = ((EntityHitResult) entityHitResult).getEntity();
        } else {
            minecraft.hitResult = blockHit;
            minecraft.crosshairPickEntity = null;
        }
    }
}