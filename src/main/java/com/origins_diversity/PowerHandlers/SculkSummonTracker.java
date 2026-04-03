package com.origins_diversity.PowerHandlers;

import net.minecraft.world.entity.monster.warden.Warden;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SculkSummonTracker {
    // Maps warden UUID -> summoner UUID, populated when the power fires
    public static final Map<UUID, UUID> pendingSummons = new ConcurrentHashMap<>();
}