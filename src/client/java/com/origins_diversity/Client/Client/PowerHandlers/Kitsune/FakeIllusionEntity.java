package com.origins_diversity.client.Client.PowerHandlers.Kitsune;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class FakeIllusionEntity {

    public Entity renderEntity;
    public Vec3d pos;
    public int lifetime;

    public float currentYaw;
    private float yawVelocity = 0f;

    public float headYaw;
    public float headPitch;

    public float targetYaw;
    public float targetPitch;
    private int soundTimer = 0;

    private enum FocusType { PLAYER, RANDOM }
    private FocusType focusType = FocusType.RANDOM;

    private int focusTimer = 0;
    private Vec3d randomLookTarget;
    public boolean hasSetYaw = false;

    private static final float BODY_SYNC_LIMIT = 55f;
    private static final float BODY_DAMP = 0.82f;
    private static final float HEAD_SMOOTH = 0.18f;
    private final Random random = Random.create();

    public FakeIllusionEntity(Entity renderEntity, Vec3d pos, int lifetime) {
        this.renderEntity = renderEntity;
        this.pos = pos;
        this.lifetime = lifetime;

        float start = renderEntity.getYaw();

        this.currentYaw = start;
        this.headYaw = start;

        this.targetYaw = start;
        this.targetPitch = 0f;
        this.soundTimer = random.nextBetween(20,40);
    }

    private void tickSound(MinecraftClient mc) {
        if (soundTimer-- > 0) return;

        var profile = IllusionSoundRegistry.SOUND_MAP.get(renderEntity.getType());
        if (profile == null) return;

        var entry = profile.getRandom(mc.world.random);
        if (entry == null) return;

        mc.world.playSound(
                renderEntity.getX(),
                renderEntity.getY(),
                renderEntity.getZ(),
                entry.sound,
                renderEntity.getSoundCategory(),
                entry.volume,
                0.9f + mc.world.random.nextFloat() * 0.2f,
                false
        );

        soundTimer = 40 + mc.world.random.nextInt(80);
    }

    public void tick(MinecraftClient mc) {
        if (mc.player == null) return;

        renderEntity.baseTick();
        tickBehavior(mc);
        applyRotations();
        tickSound(mc);
    }

    private void tickBehavior(MinecraftClient mc) {

        Vec3d eyeSelf = renderEntity.getPos().add(0, renderEntity.getEyeHeight(renderEntity.getPose()), 0);

        if (focusTimer <= 0) {

            if (mc.world.random.nextFloat() < 0.3f) {
                focusType = FocusType.PLAYER;
                focusTimer = 60 + mc.world.random.nextInt(60);
            } else {
                focusType = FocusType.RANDOM;
                focusTimer = 60 + mc.world.random.nextInt(120);

                randomLookTarget = new Vec3d(
                        pos.x + (mc.world.random.nextDouble() - 0.5) * 6,
                        pos.y,
                        pos.z + (mc.world.random.nextDouble() - 0.5) * 6
                );
            }
        }

        focusTimer--;

        Vec3d targetEye = (focusType == FocusType.PLAYER)
                ? mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0)
                : randomLookTarget;

        Vec3d dir = targetEye.subtract(eyeSelf);

        float yaw = (float)(Math.atan2(dir.z, dir.x) * (180 / Math.PI)) - 90f;
        float pitch = (float)(-(Math.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z))
                * (180 / Math.PI)));

        targetYaw = yaw;
        targetPitch = pitch;

        headYaw = damp(headYaw, targetYaw, HEAD_SMOOTH);

        if (focusType == FocusType.RANDOM) {
            headPitch = damp(headPitch, 0f, 0.06f);
        } else {
            headPitch = damp(headPitch, targetPitch, 0.14f);
        }

        float diff = MathHelper.wrapDegrees(headYaw - currentYaw);

        if (Math.abs(diff) > BODY_SYNC_LIMIT) {
            yawVelocity += diff * 0.015f;
        }

        yawVelocity *= BODY_DAMP;
        currentYaw += yawVelocity;
    }

    private void applyRotations() {
        renderEntity.setYaw(currentYaw);
        renderEntity.setBodyYaw(currentYaw);
        renderEntity.setHeadYaw(headYaw);
        renderEntity.setPitch(headPitch);
        renderEntity.prevX = renderEntity.getX();
        renderEntity.prevY = renderEntity.getY();
        renderEntity.prevZ = renderEntity.getZ();
        renderEntity.prevYaw = currentYaw;
        renderEntity.prevPitch = headPitch;
    }

    private float damp(float current, float target, float factor) {
        float diff = MathHelper.wrapDegrees(target - current);
        return current + diff * factor;
    }
}
