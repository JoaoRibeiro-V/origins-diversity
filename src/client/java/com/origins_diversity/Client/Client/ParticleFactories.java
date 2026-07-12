package com.origins_diversity.client.Client;

import com.origins_diversity.Extra.EmissiveParticleOptions;
import com.origins_diversity.Extra.ModParticles;
import com.origins_diversity.client.Client.Particles.EmissiveDustParticle;
import com.origins_diversity.client.Client.Particles.OwlFeatherParticle;
import com.origins_diversity.client.Client.Particles.ParasiticLeechParticle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParticleFactories {

    private static final Map<DefaultParticleType, ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType>> FACTORIES =
            new LinkedHashMap<>();

    private static final Map<ParticleType<EmissiveParticleOptions>, ParticleFactoryRegistry.PendingParticleFactory<EmissiveParticleOptions>> EMISSIVE_FACTORIES =
            new LinkedHashMap<>();


    private static void registerEmissive(
            ParticleType<EmissiveParticleOptions> type,
            ParticleFactoryRegistry.PendingParticleFactory<EmissiveParticleOptions> factory
    ) {
        EMISSIVE_FACTORIES.put(type, factory);
    }


    private static void register(
            DefaultParticleType type,
            ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType> factory
    ) {
        FACTORIES.put(type, factory);
    }


    static {
        register(
                ModParticles.OWL_FEATHER,
                OwlFeatherParticle.Factory::new
        );

        register(
                ModParticles.PARASITIC_LEECH,
                ParasiticLeechParticle.Factory::new
        );


        registerEmissive(
                ModParticles.BUBBLE_POP,
                EmissiveDustParticle.Factory::new
        );

        registerEmissive(
                ModParticles.SCULK_CHARGE,
                EmissiveDustParticle.Factory::new
        );

        registerEmissive(
                ModParticles.SCULK_CHARGE_POP,
                EmissiveDustParticle.Factory::new
        );
    }


    public static void registerAll() {
        FACTORIES.forEach((type, factory) ->
                ParticleFactoryRegistry.getInstance()
                        .register(type, factory)
        );

        EMISSIVE_FACTORIES.forEach((type, factory) ->
                ParticleFactoryRegistry.getInstance()
                        .register(type, factory)
        );
    }
}