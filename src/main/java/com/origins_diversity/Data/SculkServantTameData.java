package com.origins_diversity.Data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class SculkServantTameData extends SavedData {

    private static final String KEY = "sculk_servant_tamed";
    private final Set<UUID> tamedPlayers = new HashSet<>();

    public static SculkServantTameData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(
                        new SavedData.Factory<>(
                                SculkServantTameData::new,
                                (tag, provider) -> load(tag),
                                null
                        ),
                        KEY
                );
    }

    private static SculkServantTameData load(CompoundTag tag) {
        SculkServantTameData data = new SculkServantTameData();
        String raw = tag.getString("tamed");
        if (!raw.isEmpty()) {
            for (String s : raw.split(",")) {
                data.tamedPlayers.add(UUID.fromString(s));
            }
        }
        return data;
    }

    public void markTamed(UUID playerUUID, MinecraftServer server) {
        tamedPlayers.add(playerUUID);
        setDirty();

        ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);
        if (player != null) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("✦ Sculk Servant Tamed ✦").withStyle(style -> style.withColor(net.minecraft.ChatFormatting.DARK_PURPLE).withBold(true)),true
            );
        }
    }

    public boolean isTamed(UUID playerUUID) {
        return tamedPlayers.contains(playerUUID);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        StringBuilder sb = new StringBuilder();
        for (UUID uuid : tamedPlayers) {
            if (sb.length() > 0) sb.append(",");
            sb.append(uuid);
        }
        tag.putString("tamed", sb.toString());
        return tag;
    }
}