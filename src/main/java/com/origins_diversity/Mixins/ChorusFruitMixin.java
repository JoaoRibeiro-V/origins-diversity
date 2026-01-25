package com.origins_diversity.Mixins;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusFruitItem.class)
public abstract class ChorusFruitMixin {
    @Inject(
            method = "finishUsingItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void originsDiversity$chorusLeechEat(
            ItemStack stack,
            Level world,
            LivingEntity entity,
            CallbackInfoReturnable<ItemStack> cir
    ){
        if(!(entity instanceof Player player)) return;
        if(!OriginsUtil.hasOrigin(player, "origins-diversity","chorus_leech")) return;
        if(!player.getFoodData().needsFood()){
            cir.setReturnValue(stack);
            return;
        }
        FoodProperties food = stack.get(DataComponents.FOOD);
        if(food != null){
            player.getFoodData().eat(food.nutrition(), food.saturation());
        }
        if(!player.getAbilities().instabuild) stack.shrink(1);
        cir.setReturnValue(stack);
    }
}
