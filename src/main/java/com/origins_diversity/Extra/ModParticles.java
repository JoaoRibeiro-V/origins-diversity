package com.origins_diversity.Extra;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ModParticles {

    public static final DefaultParticleType OWL_FEATHER =
            register("owl_feather");

    public static final DefaultParticleType PARASITIC_LEECH =
            register("parasitic_leech");

    public static final ParticleType<EmissiveParticleOptions> BUBBLE_POP =
            registerEmissive("bubble_pop");

    public static final ParticleType<EmissiveParticleOptions> SCULK_CHARGE =
            registerEmissive("sculk_charge");

    public static final ParticleType<EmissiveParticleOptions> SCULK_CHARGE_POP =
            registerEmissive("sculk_charge_pop");


    private static ParticleType<EmissiveParticleOptions> registerEmissive(String name) {
        return Registry.register(
                Registries.PARTICLE_TYPE,
                new Identifier("origins-diversity", name),
                new EmissiveParticleType()
        );
    }


    private static DefaultParticleType register(String name) {
        return Registry.register(
                Registries.PARTICLE_TYPE,
                new Identifier("origins-diversity", name),
                FabricParticleTypes.simple()
        );
    }


    public static void register() {
    }


    public static EmissiveParticleOptions emissive(
            ParticleType<EmissiveParticleOptions> type,
            float r,
            float g,
            float b,
            float size,
            int lifetime
    ) {
        return new EmissiveParticleOptions(
                type,
                new Vector3f(r, g, b),
                size,
                lifetime
        );
    }
}