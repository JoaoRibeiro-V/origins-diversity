package com.origins_diversity.Mixins;

import com.origins_diversity.PowerHandlers.GoldTrimHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onEquipItem", at = @At("TAIL"))
    private void originsDiversity$onEquipItem(
            EquipmentSlot slot,
            ItemStack oldStack,
            ItemStack newStack,
            CallbackInfo ci
    ) {
        GoldTrimHandler.updateGoldTrim(oldStack);
        GoldTrimHandler.updateGoldTrim(newStack);
    }
}
