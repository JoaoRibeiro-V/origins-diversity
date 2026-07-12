package com.origins_diversity.Extra;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.joml.Vector3f;

public class EmissiveParticleOptions implements ParticleEffect {

    public static final ParticleEffect.Factory<EmissiveParticleOptions> FACTORY =
            new ParticleEffect.Factory<>() {
                @Override
                public EmissiveParticleOptions read(
                        ParticleType<EmissiveParticleOptions> type,
                        StringReader reader
                ) throws CommandSyntaxException {

                    reader.expect(' ');

                    float r = (float) reader.readDouble();
                    reader.expect(' ');

                    float g = (float) reader.readDouble();
                    reader.expect(' ');

                    float b = (float) reader.readDouble();
                    reader.expect(' ');

                    float scale = (float) reader.readDouble();
                    reader.expect(' ');

                    int lifetime = reader.readInt();

                    return new EmissiveParticleOptions(
                            type,
                            new Vector3f(r, g, b),
                            scale,
                            lifetime
                    );
                }

                @Override
                public EmissiveParticleOptions read(
                        ParticleType<EmissiveParticleOptions> type,
                        PacketByteBuf buf
                ) {
                    Vector3f color = new Vector3f(
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat()
                    );

                    float scale = buf.readFloat();
                    int lifetime = buf.readVarInt();

                    return new EmissiveParticleOptions(
                            type,
                            color,
                            scale,
                            lifetime
                    );
                }
            };


    private final ParticleType<EmissiveParticleOptions> type;
    private final Vector3f color;
    private final float scale;
    private final int lifetime;


    public EmissiveParticleOptions(
            ParticleType<EmissiveParticleOptions> type,
            Vector3f color,
            float scale,
            int lifetime
    ) {
        this.type = type;
        this.color = color;
        this.scale = scale;
        this.lifetime = lifetime;
    }


    public int getLifetime() {
        return lifetime;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getColor() {
        return color;
    }


    @Override
    public ParticleType<EmissiveParticleOptions> getType() {
        return type;
    }


    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(color.x());
        buf.writeFloat(color.y());
        buf.writeFloat(color.z());
        buf.writeFloat(scale);
        buf.writeVarInt(lifetime);
    }


    @Override
    public String asString() {
        return String.format(
                "%.2f %.2f %.2f %.2f %d",
                color.x(),
                color.y(),
                color.z(),
                scale,
                lifetime
        );
    }
}