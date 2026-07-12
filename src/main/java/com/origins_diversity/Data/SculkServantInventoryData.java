package com.origins_diversity.Data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SculkServantInventoryData extends PersistentState {

    private static final String KEY = "sculk_servant_inventory";

    private final Map<UUID, SimpleInventory> inventories = new HashMap<>();


    public static SculkServantInventoryData get(MinecraftServer server) {

        return server.getOverworld()
                .getPersistentStateManager()
                .getOrCreate(
                        SculkServantInventoryData::load,
                        SculkServantInventoryData::new,
                        KEY
                );
    }


    private static SculkServantInventoryData load(NbtCompound tag) {

        SculkServantInventoryData data = new SculkServantInventoryData();

        NbtCompound inventoriesTag =
                tag.getCompound("inventories");


        for (String key : inventoriesTag.getKeys()) {

            UUID uuid = UUID.fromString(key);

            SimpleInventory container =
                    new SimpleInventory(27);


            NbtList list =
                    inventoriesTag.getList(key, 10);


            container.readNbtList(list);


            data.inventories.put(uuid, container);
        }


        return data;
    }


    @Override
    public NbtCompound writeNbt(NbtCompound tag) {

        NbtCompound inventoriesTag =
                new NbtCompound();


        for (Map.Entry<UUID, SimpleInventory> entry : inventories.entrySet()) {

            NbtList list =
                    entry.getValue().toNbtList();


            inventoriesTag.put(
                    entry.getKey().toString(),
                    list
            );
        }


        tag.put(
                "inventories",
                inventoriesTag
        );


        return tag;
    }


    public SimpleInventory getInventory(UUID uuid) {

        return inventories.computeIfAbsent(uuid, u -> {

            markDirty();


            return new SimpleInventory(27) {

                @Override
                public void markDirty() {
                    super.markDirty();
                    SculkServantInventoryData.this.markDirty();
                }
            };
        });
    }
}
