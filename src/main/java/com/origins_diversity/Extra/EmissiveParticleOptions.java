package com.origins_diversity.Extra;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class EmissiveParticleOptions extends ScalableParticleOptionsBase {

    private final ParticleType<EmissiveParticleOptions> type;
    private final Vector3f color;
    private final int lifetime;

    public EmissiveParticleOptions(ParticleType<EmissiveParticleOptions> type, Vector3f color, float scale, int lifetime) {
        super(scale);
        this.type  = type;
        this.color = color;
        this.lifetime = lifetime;
    }

    public int getLifetime() { return lifetime; }

    @Override
    public ParticleType<EmissiveParticleOptions> getType() {
        return type;
    }

    public Vector3f getColor() {
        return color;
    }

    // Codec and StreamCodec are per-type since they need the type reference
    public static MapCodec<EmissiveParticleOptions> codec(ParticleType<EmissiveParticleOptions> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(o -> o.color),
                SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale),
                Codec.INT.fieldOf("lifetime").forGetter(o -> o.lifetime)
        ).apply(instance, (color, scale, lifetime) -> new EmissiveParticleOptions(type, color, scale, lifetime)));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, EmissiveParticleOptions> streamCodec(ParticleType<EmissiveParticleOptions> type) {
        return StreamCodec.composite(
                ByteBufCodecs.VECTOR3F, o -> o.color,
                ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale,
                ByteBufCodecs.INT, o -> o.lifetime,
                (color, scale, lifetime) -> new EmissiveParticleOptions(type, color, scale, lifetime)
        );
    }
}