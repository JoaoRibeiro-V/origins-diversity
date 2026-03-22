package com.origins_diversity.Mixins;

import com.origins_diversity.PowerHandlers.AttackSculkCultistGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolem.class)
public abstract class IronGolemAiMixin {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addSculkCultistAttack(CallbackInfo ci){
        IronGolem self = (IronGolem) (Object)this;
        self.goalSelector.addGoal(1, new AttackSculkCultistGoal(self, 10));
    }
}
