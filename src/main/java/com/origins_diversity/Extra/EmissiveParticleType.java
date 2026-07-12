package com.origins_diversity.Extra;

import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleType;

public class EmissiveParticleType extends ParticleType<EmissiveParticleOptions> {

    public EmissiveParticleType() {
        super(false, EmissiveParticleOptions.FACTORY);
    }

    @Override
    public Codec<EmissiveParticleOptions> getCodec() {
        return null;
    }
}