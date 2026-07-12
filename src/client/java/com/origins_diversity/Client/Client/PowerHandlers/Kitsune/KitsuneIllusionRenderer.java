package com.origins_diversity.client.Client.PowerHandlers.Kitsune;

import net.minecraft.client.util.math.MatrixStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public final class KitsuneIllusionRenderer {

    public static void render(WorldRenderContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        VertexConsumerProvider.Immediate buffers = mc.getBufferBuilders().getEntityVertexConsumers();
        MatrixStack poseStack = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        List<FakeIllusionEntity> illusions = KitsuneIllusionManager.getActive();
        for (FakeIllusionEntity illusion : illusions) {
            illusion.tick(mc);
            Entity fake = illusion.renderEntity;

            if (!illusion.hasSetYaw) {
                assert mc.world != null;
                float yaw = mc.world.random.nextFloat() * 360f;
                fake.setYaw(yaw);
                fake.setBodyYaw(yaw);
                fake.setHeadYaw(yaw);

                fake.prevYaw = yaw;

                illusion.hasSetYaw = true;
            }
            Vec3d relPos = fake.getPos().subtract(camPos);
            assert poseStack != null;
            poseStack.push();
            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
            assert mc.world != null;
            dispatcher.render(
                    fake,
                    relPos.x,
                    relPos.y,
                    relPos.z,
                    fake.getYaw(),
                    context.tickDelta(),
                    poseStack,
                    buffers,
                    WorldRenderer.getLightmapCoordinates(mc.world, BlockPos.ofFloored(fake.getPos()))
            );
            poseStack.pop();
        }

        buffers.draw();
    }
}
