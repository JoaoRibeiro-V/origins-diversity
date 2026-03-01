package com.origins_diversity.Client.PowerHandlers.Kitsune;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.List;

public final class KitsuneIllusionRenderer {

    public static void render(WorldRenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        PoseStack poseStack = context.matrixStack();
        Camera camera = context.camera();
        Vec3 camPos = camera.getPosition();
        List<FakeIllusionEntity> illusions = KitsuneIllusionManager.getActive();
        for (FakeIllusionEntity illusion : illusions) {
            Entity fake = illusion.base;

            double y = getGroundY(mc, fake.getX(), fake.getZ(), fake.getY());
            fake.setPos(fake.getX(), y, fake.getZ());

            if (!illusion.hasSetYaw) {
                assert mc.level != null;
                float yaw = mc.level.random.nextFloat() * 360f;
                fake.setYRot(yaw);
                fake.setYBodyRot(yaw);
                fake.setYHeadRot(yaw);

                fake.yRotO = yaw;

                illusion.hasSetYaw = true;
            }
            fake.baseTick();
            fake.setDeltaMovement(Vec3.ZERO);

            Vec3 relPos = fake.position().subtract(camPos);

            assert poseStack != null;
            poseStack.pushPose();
            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
            assert mc.level != null;
            dispatcher.render(
                    fake,
                    relPos.x,
                    relPos.y,
                    relPos.z,
                    fake.getYRot(),
                    context.tickCounter().getRealtimeDeltaTicks(),
                    poseStack,
                    buffers,
                    LevelRenderer.getLightColor(mc.level, BlockPos.containing(fake.position()))
            );
            poseStack.popPose();
        }

        buffers.endBatch();
    }
    private static double getGroundY(Minecraft mc, double x, double z, double startY) {
        Vec3 from = new Vec3(x, startY + 4, z);
        Vec3 to   = new Vec3(x, startY - 20, z);

        assert mc.level != null;
        assert mc.player != null;
        BlockHitResult hit = mc.level.clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mc.player
        ));

        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            return hit.getLocation().y;
        }
        return startY; // fallback if no block hit
    }
}
