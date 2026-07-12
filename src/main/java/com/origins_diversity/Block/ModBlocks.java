package com.origins_diversity.Block;

import com.origins_diversity.OriginsDiversity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.block.AbstractBlock;

public class ModBlocks {

    public static final Block SCULK_BLOCK = registerBlock(
            "purple_sculk_block",
            new Block(
                    AbstractBlock.Settings.copy(Blocks.SCULK)
            )
    );


    private static Block registerBlock(String name, Block block) {

        registerBlockItem(name, block);

        return Registry.register(
                Registries.BLOCK,
                new Identifier(OriginsDiversity.MOD_ID, name),
                block
        );
    }


    private static void registerBlockItem(String name, Block block) {

        Registry.register(
                Registries.ITEM,
                new Identifier(OriginsDiversity.MOD_ID, name),
                new BlockItem(block, new Item.Settings())
        );
    }


    public static void registerModBlocks() {

        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.NATURAL
        ).register(entries -> {
            entries.add(SCULK_BLOCK);
        });
    }
}