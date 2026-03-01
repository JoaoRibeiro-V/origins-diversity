package com.origins_diversity.Client.PowerHandlers.Kitsune;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class FakeIllusionEntity {
    private final Vec3 pos;
    public final Entity base;
    private final float yaw;
    public boolean hasSetYaw;
    private int age = 0;
    public int lifetime;
    public final float initialYaw;

    public FakeIllusionEntity(Entity base, Vec3 pos, int lifetime) {
        this.pos = pos;
        this.initialYaw = RandomSource.create().nextFloat() * 360f;
        this.base = base;
        this.yaw = base.getYRot();
        this.lifetime = lifetime;
    }

    public boolean tick() {
        age++;
        return age > this.lifetime;
    }

    public Vec3 getPos() {
        return pos;
    }

    public float getYaw() {
        return yaw;
    }
}
