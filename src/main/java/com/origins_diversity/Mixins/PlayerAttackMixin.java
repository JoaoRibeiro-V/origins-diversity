package com.origins_diversity.Mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static com.origins_diversity.GameRules.ModGameRules.PREVENT_MOUNT_DAMAGE;
@Mixin(PlayerEntity.class)
public abstract class PlayerAttackMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void preventMountDamage(Entity target, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity)(Object)this;

        if (!self.getWorld().getGameRules().getBoolean(PREVENT_MOUNT_DAMAGE)) return;
        if (target == self.getVehicle()) { ci.cancel(); return; }
        if (target.getVehicle() == self) { ci.cancel(); }
    }
}
