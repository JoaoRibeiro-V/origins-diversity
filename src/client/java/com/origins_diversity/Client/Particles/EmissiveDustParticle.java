package com.origins_diversity.Client.Particles;

import com.origins_diversity.Extra.EmissiveParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.DustParticleOptions;

public class EmissiveDustParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected EmissiveDustParticle(ClientLevel level, double x, double y, double z,
                                   double vx, double vy, double vz,
                                   EmissiveParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.xd = vx; // override the randomized velocity
        this.yd = vy;
        this.zd = vz;
        this.roll = random.nextFloat() * 360f;
        this.oRoll = this.roll;
        this.sprites  = sprites;
        this.rCol     = options.getColor().x();
        this.gCol     = options.getColor().y();
        this.bCol     = options.getColor().z();
        this.alpha    = 1.0f;
        this.quadSize = options.getScale();
        this.lifetime = options.getLifetime();
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        float progress = (float) this.age / this.lifetime;
        this.alpha = progress < 0.7f ? 1.0f : 1.0f - ((progress - 0.7f) / 0.3f);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<EmissiveParticleOptions> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(EmissiveParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            EmissiveDustParticle p = new EmissiveDustParticle(level, x, y, z, vx, vy, vz, options, sprites);
            p.pickSprite(sprites);
            return p;
        }
    }
}