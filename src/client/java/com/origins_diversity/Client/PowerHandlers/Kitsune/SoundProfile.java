package com.origins_diversity.Client.PowerHandlers.Kitsune;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;

import java.util.List;

public class SoundProfile {

    public static class Entry {
        public final SoundEvent sound;
        public final float volume;
        public final float chance;

        public Entry(SoundEvent sound, float volume, float chance) {
            this.sound = sound;
            this.volume = volume;
            this.chance = chance;
        }
    }

    private final List<Entry> sounds;

    public SoundProfile(List<Entry> sounds) {
        this.sounds = sounds;
    }

    public Entry getRandom(RandomSource random) {
        if (sounds.isEmpty()) return null;
        return sounds.get(random.nextInt(sounds.size()));
    }
}