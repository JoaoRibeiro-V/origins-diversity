package com.origins_diversity.Extra;

import com.origins_diversity.OriginsDiversity;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

public class Particles {

    // Central particle storage
    private static final Map<String, SimpleParticleType> PARTICLES = new LinkedHashMap<>();

    private static SimpleParticleType create(String id) {
        SimpleParticleType particle = FabricParticleTypes.simple();
        PARTICLES.put(id, particle);
        return particle;
    }

    // PARTICLE DEFINITIONS
    public static final SimpleParticleType OWL_FEATHER =
            create("owl_feather");
    public static final SimpleParticleType PARASITIC_LEECH =
            create("parasitic_leech");

    // COMMON REGISTRATION
    public static void register() {
        PARTICLES.forEach((id, particle) -> {
            Registry.register(
                    BuiltInRegistries.PARTICLE_TYPE,
                    "origins-diversity:" + id,
                    particle
            );
        });
    }

    // CLIENT ACCESS
    public static Map<String, SimpleParticleType> all() {
        return PARTICLES;
    }
}
