package com.origins_diversity.Mixins;

import com.origins_diversity.PowerHandlers.AttackKitsuneGoal;
import com.origins_diversity.PowerHandlers.AttackSculkCultistGoal;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.passive.WolfEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class WolfAiMixin {

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addCustomGoals(CallbackInfo ci) {
        WolfEntity self = (WolfEntity) (Object) this;

        GoalSelector goalSelector =
                ((MobEntityAccessor) self).getGoalSelector();

        goalSelector.add(
                1,
                new AttackKitsuneGoal(self, 10)
        );

        goalSelector.add(
                2,
                new AttackSculkCultistGoal(self, 10)
        );
    }
}