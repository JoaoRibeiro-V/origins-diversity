package com.origins_diversity.Extra;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class EmissiveParticleType extends ParticleType<EmissiveParticleOptions> {

    public EmissiveParticleType() {
        super(false);
    }

    @Override
    public MapCodec<EmissiveParticleOptions> codec() {
        return EmissiveParticleOptions.codec(this);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, EmissiveParticleOptions> streamCodec() {
        return EmissiveParticleOptions.streamCodec(this);
    }
}