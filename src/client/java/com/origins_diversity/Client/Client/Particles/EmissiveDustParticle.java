package com.origins_diversity.client.Client.Particles;

import com.origins_diversity.Extra.EmissiveParticleOptions;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;

import net.minecraft.client.world.ClientWorld;

public class EmissiveDustParticle extends SpriteBillboardParticle {

    private final SpriteProvider sprites;


    protected EmissiveDustParticle(
            ClientWorld level,
            double x,
            double y,
            double z,
            double vx,
            double vy,
            double vz,
            EmissiveParticleOptions options,
            SpriteProvider sprites
    ) {

        super(level, x, y, z, vx, vy, vz);


        this.velocityX = vx;
        this.velocityY = vy;
        this.velocityZ = vz;


        this.angle = random.nextFloat() * 360f;
        this.prevAngle = this.angle = this.prevAngle = random.nextFloat() * 360f;


        this.sprites = sprites;


        this.red = options.getColor().x();
        this.green = options.getColor().y();
        this.blue = options.getColor().z();


        this.alpha = 1.0f;


        this.scale = options.getScale();


        this.maxAge = options.getLifetime();


        this.collidesWithWorld = false;
    }



    @Override
    public void tick() {

        super.tick();


        this.setSpriteForAge(sprites);


        float progress =
                (float) this.age / this.maxAge;


        this.alpha =
                progress < 0.7f
                        ? 1.0f
                        : 1.0f -
                        ((progress - 0.7f) / 0.3f);
    }



    @Override
    protected int getBrightness(float tint) {
        return 0xF000F0;
    }



    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }



    public static class Factory
            implements ParticleFactory<EmissiveParticleOptions> {


        private final SpriteProvider sprites;


        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }



        @Override
        public Particle createParticle(
                EmissiveParticleOptions options,
                ClientWorld level,
                double x,
                double y,
                double z,
                double vx,
                double vy,
                double vz
        ) {


            EmissiveDustParticle particle =
                    new EmissiveDustParticle(
                            level,
                            x,
                            y,
                            z,
                            vx,
                            vy,
                            vz,
                            options,
                            sprites
                    );


            particle.setSprite(sprites);


            return particle;
        }
    }
}