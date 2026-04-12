package com.origins_diversity.Extra;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class ModParticles {

    public static final SimpleParticleType OWL_FEATHER = register("owl_feather");
    public static final SimpleParticleType PARASITIC_LEECH = register("parasitic_leech");

    public static final ParticleType<EmissiveParticleOptions> BUBBLE_POP       = registerEmissive("bubble_pop");
    public static final ParticleType<EmissiveParticleOptions> SCULK_CHARGE     = registerEmissive("sculk_charge");
    public static final ParticleType<EmissiveParticleOptions> SCULK_CHARGE_POP = registerEmissive("sculk_charge_pop");

    private static ParticleType<EmissiveParticleOptions> registerEmissive(String name) {
        ParticleType<EmissiveParticleOptions> type = FabricParticleTypes.complex(
                false,
                type2 -> (MapCodec<EmissiveParticleOptions>) EmissiveParticleOptions.codec(type2).codec(),
                EmissiveParticleOptions::streamCodec
        );
        return Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                ResourceLocation.fromNamespaceAndPath("origins-diversity", name),
                type
        );
    }

    private static SimpleParticleType register(String name) {
        return Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                ResourceLocation.fromNamespaceAndPath("origins-diversity", name),
                FabricParticleTypes.simple()
        );
    }

    public static void register() {}

    public static EmissiveParticleOptions emissive(ParticleType<EmissiveParticleOptions> type, float r, float g, float b, float size, int lifetime) {
        return new EmissiveParticleOptions(type, new Vector3f(r, g, b), size, lifetime);
    }
}
