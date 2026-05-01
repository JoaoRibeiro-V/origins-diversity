package com.origins_diversity.Client.PowerHandlers.Kitsune;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IllusionSoundRegistry {

    public static final Map<EntityType<?>, SoundProfile> SOUND_MAP = new HashMap<>();

    static {
        SOUND_MAP.put(EntityType.ZOMBIE, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.ZOMBIE_AMBIENT, 0.6f, 0.8f)
        )));

        SOUND_MAP.put(EntityType.SKELETON, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.SKELETON_AMBIENT, 0.6f, 0.8f)
        )));

        SOUND_MAP.put(EntityType.CREEPER, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.CREEPER_PRIMED, 0.3f, 0.3f)
        )));

        SOUND_MAP.put(EntityType.ENDERMAN, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.ENDERMAN_AMBIENT, 0.5f, 0.85f),
                new SoundProfile.Entry(SoundEvents.ENDERMAN_STARE, 0.7f, 0.15f)
        )));

        SOUND_MAP.put(EntityType.PLAYER, new SoundProfile(List.of(
                new SoundProfile.Entry(SoundEvents.PLAYER_BREATH, 0.4f, 0.3f)
        )));
    }
}