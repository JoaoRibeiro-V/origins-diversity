package com.origins_diversity.Client.Particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class ParasiticLeechParticle extends TextureSheetParticle {

    private final float spinSpeed;
    private final float initialSize;
    private final float pulseOffset;
    private final SpriteSet sprites;

    protected ParasiticLeechParticle(
            ClientLevel level,
            double x, double y, double z,
            double vx, double vy, double vz,
            SpriteSet sprites
    ) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.gravity = 0.0f;
        this.lifetime = 40 + this.random.nextInt(-30,35);

        this.quadSize = 0.075f * (0.7f + this.random.nextFloat() * 0.72f);
        this.initialSize = this.quadSize;
        this.roll = random.nextFloat() * 360f;
        this.oRoll = this.roll;
        this.spinSpeed = (this.random.nextFloat() - 0.5f) * 0.05f;

        this.pulseOffset = random.nextFloat() * 6.67f;

        // Slow initial drift
        this.xd *= 0.15;
        this.yd *= 0.15;
        this.zd *= 0.15;

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void tick() {
        super.tick();

        this.setSpriteFromAge(this.sprites);

        this.oRoll = this.roll;
        this.roll += this.spinSpeed;

        float life = (float) this.age / (float) this.lifetime;

        // Subtle organic pulse
        float pulse = 1.0f + 0.08f * (float)Math.sin(this.age * 0.35f + pulseOffset);
        this.quadSize = this.initialSize * pulse;

        // Collapse inward near death
        if (life > 0.75f) {
            float collapse = 1.0f - ((life - 0.75f) / 0.25f);
            this.quadSize *= collapse;
        }

        // Slight inward drag
        this.xd *= 0.92;
        this.yd *= 0.92;
        this.zd *= 0.92;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    // === FACTORY ===
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double vx, double vy, double vz
        ) {
            return new ParasiticLeechParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}

