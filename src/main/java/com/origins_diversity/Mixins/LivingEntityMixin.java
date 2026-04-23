package com.origins_diversity.Mixins;

import com.origins_diversity.Extra.OriginsUtil;
import com.origins_diversity.PowerHandlers.CelestialLinkHandler;
import com.origins_diversity.PowerHandlers.GoldTrimHandler;
import com.origins_diversity.PowerHandlers.SupernovaHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "hurt", at = @At("HEAD"))
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!self.level().isClientSide()) {
            CelestialLinkHandler.onEntityHurt(self, source, amount);
        }
    }
}
