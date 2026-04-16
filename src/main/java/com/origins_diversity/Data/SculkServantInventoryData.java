package com.origins_diversity.Data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SculkServantInventoryData extends SavedData {
    private static final String KEY = "sculk_servant_inventory";
    private final Map<UUID, SimpleContainer> inventories = new HashMap<>();

    public static SculkServantInventoryData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(
                        new SavedData.Factory<>(
                                SculkServantInventoryData::new,
                                SculkServantInventoryData::load, // now matches (tag, provider)
                                null
                        ),
                        KEY
                );
    }

    private static SculkServantInventoryData load(CompoundTag tag, HolderLookup.Provider provider) {
        SculkServantInventoryData data = new SculkServantInventoryData();

        CompoundTag inventoriesTag = tag.getCompound("inventories");

        for (String key : inventoriesTag.getAllKeys()) {
            UUID uuid = UUID.fromString(key);

            SimpleContainer container = new SimpleContainer(27);

            ListTag list = inventoriesTag.getList(key, 10); // 10 = CompoundTag
            container.fromTag(list, provider);

            data.inventories.put(uuid, container);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag inventoriesTag = new CompoundTag();

        for (Map.Entry<UUID, SimpleContainer> entry : inventories.entrySet()) {
            ListTag list = entry.getValue().createTag(provider);

            inventoriesTag.put(entry.getKey().toString(), list);
        }

        tag.put("inventories", inventoriesTag);

        return tag;
    }

    public SimpleContainer getInventory(UUID uuid) {
        return inventories.computeIfAbsent(uuid, u -> {
            setDirty();

            return new SimpleContainer(27) {
                @Override
                public void setChanged() {
                    super.setChanged();
                    setDirty();
                }
            };
        });
    }
}
