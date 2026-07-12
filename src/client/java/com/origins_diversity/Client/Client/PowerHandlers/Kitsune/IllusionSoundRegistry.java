package com.origins_diversity.client.Client.PowerHandlers.Kitsune;

import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IllusionSoundRegistry {

    public static final Map<EntityType<?>, SoundProfile> SOUND_MAP = new HashMap<>();

    static {
        SOUND_MAP.put(EntityType.ZOMBIE, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.ENTITY_ZOMBIE_AMBIENT, 0.6f, 0.8f)
        )));

        SOUND_MAP.put(EntityType.SKELETON, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.ENTITY_SKELETON_AMBIENT, 0.6f, 0.8f)
        )));

        SOUND_MAP.put(EntityType.CREEPER, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.ENTITY_CREEPER_PRIMED, 0.3f, 0.3f)
        )));

        SOUND_MAP.put(EntityType.ENDERMAN, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.ENTITY_ENDERMAN_AMBIENT, 0.5f, 0.85f),
                new SoundProfile.Entry(SoundEvents.ENTITY_ENDERMAN_STARE, 0.7f, 0.15f)
        )));

        SOUND_MAP.put(EntityType.PLAYER, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.ENTITY_PLAYER_BREATH, 0.4f, 0.3f)
        )));
    }
}
