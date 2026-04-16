package com.origins_diversity.Block;

import com.origins_diversity.OriginsDiversity;
import io.github.apace100.origins.registry.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final Block SCULK_BLOCK = registerBlock("purple_sculk_block", new Block(
           BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK.defaultBlockState().getBlock())
    ));

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name,block);
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(OriginsDiversity.MOD_ID, name), block);
    }
    private static void registerBlockItem(String name, Block block){
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(OriginsDiversity.MOD_ID, name),
                new BlockItem(block, new Item.Properties()));
    }
    public static void registerModBlocks(){
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.addBefore(ModBlocks.SCULK_BLOCK, ModBlocks.SCULK_BLOCK);
        });
    }
}
