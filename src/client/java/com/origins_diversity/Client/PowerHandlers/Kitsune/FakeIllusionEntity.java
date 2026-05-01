package com.origins_diversity.Client.PowerHandlers.Kitsune;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static net.minecraft.world.entity.EntityType.*;

public class FakeIllusionEntity {

    public Entity renderEntity;
    public Vec3 pos;
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
    private Vec3 randomLookTarget;
    public boolean hasSetYaw = false;

    private static final float BODY_SYNC_LIMIT = 55f;
    private static final float BODY_DAMP = 0.82f;
    private static final float HEAD_SMOOTH = 0.18f;
    private final RandomSource random = RandomSource.create();

    public FakeIllusionEntity(Entity renderEntity, Vec3 pos, int lifetime) {
        this.renderEntity = renderEntity;
        this.pos = pos;
        this.lifetime = lifetime;

        float start = renderEntity.getYRot();

        this.currentYaw = start;
        this.headYaw = start;

        this.targetYaw = start;
        this.targetPitch = 0f;
        this.soundTimer = random.nextInt(20,40);
    }

    private void tickSound(Minecraft mc) {
        if (soundTimer-- > 0) return;

        var profile = IllusionSoundRegistry.SOUND_MAP.get(renderEntity.getType());
        if (profile == null) return;

        var entry = profile.getRandom(mc.level.random);
        if (entry == null) return;

        mc.level.playLocalSound(
                renderEntity.getX(),
                renderEntity.getY(),
                renderEntity.getZ(),
                entry.sound,
                renderEntity.getSoundSource(),
                entry.volume,
                0.9f + mc.level.random.nextFloat() * 0.2f,
                false
        );

        soundTimer = 40 + mc.level.random.nextInt(80);
    }

    public void tick(Minecraft mc) {
        if (mc.player == null) return;

        renderEntity.baseTick();
        tickBehavior(mc);
        applyRotations();
        tickSound(mc);
    }

    private void tickBehavior(Minecraft mc) {

        Vec3 eyeSelf = renderEntity.position().add(0, renderEntity.getEyeHeight(), 0);

        if (focusTimer <= 0) {

            if (mc.level.random.nextFloat() < 0.3f) {
                focusType = FocusType.PLAYER;
                focusTimer = 60 + mc.level.random.nextInt(60);
            } else {
                focusType = FocusType.RANDOM;
                focusTimer = 60 + mc.level.random.nextInt(120);

                randomLookTarget = new Vec3(
                        pos.x + (mc.level.random.nextDouble() - 0.5) * 6,
                        pos.y,
                        pos.z + (mc.level.random.nextDouble() - 0.5) * 6
                );
            }
        }

        focusTimer--;

        Vec3 targetEye = (focusType == FocusType.PLAYER)
                ? mc.player.position().add(0, mc.player.getEyeHeight(), 0)
                : randomLookTarget;

        Vec3 dir = targetEye.subtract(eyeSelf);

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

        float diff = Mth.wrapDegrees(headYaw - currentYaw);

        if (Math.abs(diff) > BODY_SYNC_LIMIT) {
            yawVelocity += diff * 0.015f;
        }

        yawVelocity *= BODY_DAMP;
        currentYaw += yawVelocity;
    }

    private void applyRotations() {
        renderEntity.setYRot(currentYaw);
        renderEntity.setYBodyRot(currentYaw);
        renderEntity.setYHeadRot(headYaw);
        renderEntity.setXRot(headPitch);
        renderEntity.setOldPosAndRot();
    }

    private float damp(float current, float target, float factor) {
        float diff = Mth.wrapDegrees(target - current);
        return current + diff * factor;
    }
}