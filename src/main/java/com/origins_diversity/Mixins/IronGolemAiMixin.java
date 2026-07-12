package com.origins_diversity.Mixins;

import com.origins_diversity.PowerHandlers.AttackSculkCultistGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.passive.IronGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemAiMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addSculkCultistAttack(CallbackInfo ci){
        IronGolemEntity self = (IronGolemEntity) (Object)this;
        GoalSelector goalSelector =
                ((MobEntityAccessor) self).getGoalSelector();
       goalSelector.add(1, new AttackSculkCultistGoal(self, 10));
    }
}
