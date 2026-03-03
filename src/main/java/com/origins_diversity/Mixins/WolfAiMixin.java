package com.origins_diversity.Mixins;

import com.origins_diversity.PowerHandlers.AttackKitsuneGoal;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
public abstract class WolfAiMixin {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addKitsuneAttack(CallbackInfo ci) {
        Wolf self = (Wolf)(Object)this;
        self.goalSelector.addGoal(1, new AttackKitsuneGoal(self,10));
    }
}
