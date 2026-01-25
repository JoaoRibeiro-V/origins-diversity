package com.origins_diversity.Client;

import com.origins_diversity.Client.Particles.OwlFeatherParticle;
import com.origins_diversity.Client.Particles.ParasiticLeechParticle;
import com.origins_diversity.Extra.Particles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParticleFactories {

    private static final Map<
            SimpleParticleType,
            ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType>
            > FACTORIES = new LinkedHashMap<>();

    private static void register(
            SimpleParticleType type,
            ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> factory
    ) {
        FACTORIES.put(type, factory);
    }

    // === FACTORY DEFINITIONS ===
    static {
        register(Particles.OWL_FEATHER, OwlFeatherParticle.Factory::new);
        register(Particles.PARASITIC_LEECH, ParasiticLeechParticle.Factory::new);
    }

    // === CLIENT REGISTRATION LOOP ===
    public static void registerAll() {
        FACTORIES.forEach((type, factory) ->
                ParticleFactoryRegistry.getInstance().register(type, factory)
        );
    }
}
