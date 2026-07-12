package com.origins_diversity.client.Client.Particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class OwlFeatherParticle extends SpriteBillboardParticle {

    private final float spinSpeed;
    private final float initialSize;

    protected OwlFeatherParticle(
            ClientWorld level,
            double x,
            double y,
            double z,
            double vx,
            double vy,
            double vz
    ) {
        super(level, x, y, z, vx, vy, vz);

        this.gravityStrength = 0.03f;
        this.maxAge = 100 + this.random.nextBetween(-70, 80);

        this.scale = 0.135f * Math.max(this.random.nextFloat(), 0.6f);
        this.initialSize = this.scale;

        this.angle = random.nextFloat() * 360f;
        this.prevAngle = this.angle;

        this.spinSpeed = (this.random.nextFloat() - 0.5f) * 0.2f;
    }

    @Override
    public void tick() {
        super.tick();

        this.prevAngle = this.angle;
        this.angle += this.spinSpeed;

        double sway = Math.sin(this.age * 0.15) * 0.01;
        this.velocityX += sway;

        this.velocityX *= 0.96;
        this.velocityZ *= 0.96;

        float lifeProgress = (float) this.age / this.maxAge;
        this.scale = this.initialSize * (1.0f - lifeProgress);
    }

    @Override
    protected int getBrightness(float tint) {
        return 0xF000F0;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider sprites;

        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                DefaultParticleType type,
                ClientWorld level,
                double x,
                double y,
                double z,
                double vx,
                double vy,
                double vz
        ) {
            OwlFeatherParticle particle =
                    new OwlFeatherParticle(level, x, y, z, vx, vy, vz);

            particle.setSprite(this.sprites);

            return particle;
        }
    }
}