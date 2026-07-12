package com.origins_diversity.PowerHandlers;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterials;

import java.util.Optional;

public class GoldTrimHandler {
    private static final String ROOT_KEY = "origins_diversity";

    public static void updateGoldTrim(ItemStack stack, DynamicRegistryManager registryAccess) {
        if (stack.isEmpty()) return;

        Optional<ArmorTrim> trim = ArmorTrim.getTrim(registryAccess, stack);

        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound myTags = nbt.contains(ROOT_KEY) ? nbt.getCompound(ROOT_KEY) : new NbtCompound();

        boolean hasGoldTrim = trim.isPresent() && trim.get().getMaterial().matchesKey(ArmorTrimMaterials.GOLD);
        if (hasGoldTrim) {
            myTags.putBoolean("has_gold_trim", true);
        } else {
            myTags.remove("has_gold_trim");
        }

        if (myTags.isEmpty()) {
            nbt.remove(ROOT_KEY);
        } else {
            nbt.put(ROOT_KEY, myTags);
        }
    }
}
