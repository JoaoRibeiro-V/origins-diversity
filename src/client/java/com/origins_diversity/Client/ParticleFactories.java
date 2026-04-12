package com.origins_diversity.Client;

import com.origins_diversity.Client.Particles.EmissiveDustParticle;
import com.origins_diversity.Client.Particles.OwlFeatherParticle;
import com.origins_diversity.Client.Particles.ParasiticLeechParticle;
import com.origins_diversity.Extra.EmissiveParticleOptions;
import com.origins_diversity.Extra.EmissiveParticleType;
import com.origins_diversity.Extra.ModParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParticleFactories {
    private static final Map<
            SimpleParticleType,
            ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType>
            > FACTORIES = new LinkedHashMap<>();

    private static final Map<
            ParticleType<EmissiveParticleOptions>,
            ParticleFactoryRegistry.PendingParticleFactory<EmissiveParticleOptions>
            > EMISSIVE_FACTORIES = new LinkedHashMap<>();

    private static void registerEmissive(ParticleType<EmissiveParticleOptions> type,
                                         ParticleFactoryRegistry.PendingParticleFactory<EmissiveParticleOptions> factory) {
        EMISSIVE_FACTORIES.put(type, factory);
    }

    private static void register(
            SimpleParticleType type,
            ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> factory
    ) {
        FACTORIES.put(type, factory);
    }

    // === FACTORY DEFINITIONS ===
    static {
        register(ModParticles.OWL_FEATHER,     OwlFeatherParticle.Factory::new);
        register(ModParticles.PARASITIC_LEECH, ParasiticLeechParticle.Factory::new);

        registerEmissive(ModParticles.BUBBLE_POP,       EmissiveDustParticle.Factory::new);
        registerEmissive(ModParticles.SCULK_CHARGE,     EmissiveDustParticle.Factory::new);
        registerEmissive(ModParticles.SCULK_CHARGE_POP, EmissiveDustParticle.Factory::new);
    }

    // === CLIENT REGISTRATION LOOP ===
    public static void registerAll() {
        FACTORIES.forEach((type, factory) ->
                ParticleFactoryRegistry.getInstance().register(type, factory));
        EMISSIVE_FACTORIES.forEach((type, factory) ->
                ParticleFactoryRegistry.getInstance().register(type, factory));
    }
}
