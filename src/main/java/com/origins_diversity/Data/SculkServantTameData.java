package com.origins_diversity.Data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class SculkServantTameData extends PersistentState {

    private static final String KEY = "sculk_servant_tamed";
    private final Set<UUID> tamedPlayers = new HashSet<>();

    public static SculkServantTameData get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager()
                .getOrCreate(
                        SculkServantTameData::load,
                        SculkServantTameData::new,
                        KEY
                );
    }

    private static SculkServantTameData load(NbtCompound tag) {
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
        markDirty();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage(
                    net.minecraft.text.Text.literal("✦ Sculk Servant Tamed ✦").styled(style -> style.withColor(net.minecraft.util.Formatting.DARK_PURPLE).withBold(true)),true
            );
        }
    }

    public boolean isTamed(UUID playerUUID) {
        return tamedPlayers.contains(playerUUID);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        StringBuilder sb = new StringBuilder();
        for (UUID uuid : tamedPlayers) {
            if (sb.length() > 0) sb.append(",");
            sb.append(uuid);
        }
        tag.putString("tamed", sb.toString());
        return tag;
    }
}
