package com.origins_diversity.PowerHandlers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterials;

import javax.xml.crypto.Data;

public class GoldTrimHandler {
    private static final String ROOT_KEY = "origins_diversity";

    public static void updateGoldTrim(ItemStack stack) {
        if (stack.isEmpty()) return;
        ArmorTrim trim = stack.get(DataComponents.TRIM);

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbt = data != null ? data.copyTag() : new CompoundTag();

        CompoundTag myTags = nbt.contains(ROOT_KEY) ? nbt.getCompound(ROOT_KEY) : new CompoundTag();

        boolean hasGoldTrim =  trim != null && trim.material().is(TrimMaterials.GOLD);
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

        if(nbt.isEmpty()){
            stack.remove(DataComponents.CUSTOM_DATA);
        } else{
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        }
    }
}

