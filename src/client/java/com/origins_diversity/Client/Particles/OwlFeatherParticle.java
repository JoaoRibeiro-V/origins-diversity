package com.origins_diversity.Client.Particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class OwlFeatherParticle extends TextureSheetParticle {
    private final float spinSpeed;
    private final float initialSize;

    protected OwlFeatherParticle(
            ClientLevel level,
            double x, double y, double z,
            double vx, double vy, double vz
    ) {
        super(level, x, y, z, vx, vy, vz);

        this.gravity = 0.03f;
        this.lifetime = 100 + this.random.nextInt(-70,80);
        this.setSize(0.08f, 0.08f);
        this.quadSize = 0.135f * Math.max(this.random.nextFloat(), 0.6f);
        this.initialSize = this.quadSize;
        this.roll = random.nextFloat() * 360f;
        this.spinSpeed = (this.random.nextFloat() - 0.5f) * 0.2f;
    }

    @Override
    public void tick() {
        super.tick();

        // Save previous rotation
        this.oRoll = this.roll;

        // Apply spin
        this.roll += this.spinSpeed;

        // Feather sway
        double sway = Math.sin(this.age * 0.15) * 0.01;
        this.xd += sway;

        // Air drag
        this.xd *= 0.96;
        this.zd *= 0.96;

        // Tween Out
        float lifeProgress = (float) this.age / (float) this.lifetime;
        this.quadSize = this.initialSize * (1.0f - lifeProgress);
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
            OwlFeatherParticle particle =
                    new OwlFeatherParticle(level, x, y, z, vx, vy, vz);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }

}