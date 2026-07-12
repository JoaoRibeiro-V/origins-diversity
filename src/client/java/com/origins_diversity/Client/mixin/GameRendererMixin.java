package com.origins_diversity.client.mixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.world.RaycastContext;

import static com.origins_diversity.GameRules.ModGameRules.PREVENT_MOUNT_DAMAGE;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final
    MinecraftClient client;

    public Entity crosshairPickEntity;

    @Inject(method = "updateTargetedEntity(F)V", at = @At("TAIL"))
    private void originsDiversity$ignoreMountHit(float partialTick, CallbackInfo ci) {
        if (client.player == null) return;
        if (!client.player.getWorld().getGameRules().getBoolean(PREVENT_MOUNT_DAMAGE)) return;
        if (!(client.crosshairTarget instanceof EntityHitResult entityHit)) return;

        Entity target = entityHit.getEntity();
        Entity self = client.player;
        if (target.getVehicle() != self) return;
        ClientPlayerEntity player = client.player;
        double reach = client.interactionManager.getReachDistance();
        Vec3d from = player.getCameraPosVec(partialTick);
        Vec3d look = player.getRotationVec(partialTick);
        Vec3d to = from.add(look.x * reach, look.y * reach, look.z * reach);
        BlockHitResult blockHit = client.world.raycast(
                new RaycastContext(from, to, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player)
        );
        double blockDist = blockHit.getType() == HitResult.Type.MISS
                ? reach * reach
                : from.squaredDistanceTo(blockHit.getPos());

        HitResult entityHitResult = ProjectileUtil.getEntityCollision(
                client.world,
                player,
                from,
                to,
                new Box(from, to).expand(1.0),
                e -> !e.isSpectator() && e.isCollidable() && e != target, // ignora o passageiro
                (float) blockDist
        );

        if (entityHitResult != null) {
            client.crosshairTarget = entityHitResult;
            client.targetedEntity = ((EntityHitResult) entityHitResult).getEntity();
        } else {
            client.crosshairTarget = blockHit;
            client.targetedEntity = null;
        }
    }
}
