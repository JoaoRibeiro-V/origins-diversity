package com.origins_diversity.Mixins;

import com.origins_diversity.Entities.SculkZombieEntity;
import com.origins_diversity.Extra.OriginsUtil;
import com.origins_diversity.PowerHandlers.CelestialLinkHandler;
import com.origins_diversity.PowerHandlers.GoldTrimHandler;
import com.origins_diversity.PowerHandlers.SupernovaHandler;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onEquipStack", at = @At("TAIL"))
    private void originsDiversity$onEquipStack(
            EquipmentSlot slot,
            ItemStack oldStack,
            ItemStack newStack,
            CallbackInfo ci
    ) {
        LivingEntity self = (LivingEntity) (Object) this;

        GoldTrimHandler.updateGoldTrim(oldStack, self.getWorld().getRegistryManager());
        GoldTrimHandler.updateGoldTrim(newStack, self.getWorld().getRegistryManager());
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!self.getWorld().isClient()) {
            CelestialLinkHandler.onEntityHurt(self, source, amount);
        }
    }

    @Inject(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (entity.getWorld().isClient()) return;

        if (!(source.getAttacker() instanceof LivingEntity attacker)) return;

        ServerWorld world = (ServerWorld) entity.getWorld();

        world.getEntitiesByClass(
                SculkZombieEntity.class,
                entity.getBoundingBox().expand(32),
                zombie -> {
                    UUID sid = zombie.getSummonerUUID();
                    return sid != null && sid.equals(entity.getUuid());
                }
        ).forEach(zombie -> zombie.setTarget(attacker));
    }
}
