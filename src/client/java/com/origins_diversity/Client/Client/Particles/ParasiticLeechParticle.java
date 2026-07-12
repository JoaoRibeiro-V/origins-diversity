package com.origins_diversity.client.Client.Particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;


public class ParasiticLeechParticle extends SpriteBillboardParticle {

    private final float spinSpeed;
    private final float initialSize;
    private final float pulseOffset;
    private final SpriteProvider sprites;


    protected ParasiticLeechParticle(
            ClientWorld level,
            double x,
            double y,
            double z,
            double vx,
            double vy,
            double vz,
            SpriteProvider sprites
    ) {
        super(level, x, y, z, vx, vy, vz);

        this.sprites = sprites;

        this.gravityStrength = 0.0f;

        this.maxAge = 40 + this.random.nextBetween(-30, 35);


        this.scale =
                0.075f *
                        (0.7f + this.random.nextFloat() * 0.72f);


        this.initialSize = this.scale;


        this.angle = random.nextFloat() * 360f;
        this.prevAngle = this.angle;


        this.spinSpeed =
                (this.random.nextFloat() - 0.5f) * 0.05f;


        this.pulseOffset =
                random.nextFloat() * 6.67f;


        // Slow initial drift
        this.velocityX *= 0.15;
        this.velocityY *= 0.15;
        this.velocityZ *= 0.15;


        this.setSpriteForAge(this.sprites);
    }


    @Override
    public void tick() {
        super.tick();


        this.setSpriteForAge(this.sprites);


        this.prevAngle = this.angle;
        this.angle += this.spinSpeed;


        float life =
                (float) this.age /
                        (float) this.maxAge;


        // Subtle organic pulse
        float pulse =
                1.0f +
                        0.08f *
                                (float) Math.sin(
                                        this.age * 0.35f + pulseOffset
                                );


        this.scale =
                this.initialSize * pulse;



        // Collapse inward near death
        if (life > 0.75f) {

            float collapse =
                    1.0f -
                            ((life - 0.75f) / 0.25f);

            this.scale *= collapse;
        }



        // Slight inward drag
        this.velocityX *= 0.92;
        this.velocityY *= 0.92;
        this.velocityZ *= 0.92;
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

            return new ParasiticLeechParticle(
                    level,
                    x,
                    y,
                    z,
                    vx,
                    vy,
                    vz,
                    sprites
            );
        }
    }
}