package com.origins_diversity.Mixins;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusFruitItem.class)
public abstract class ChorusFruitMixin {
    @Inject(
            method = "finishUsing",
            at = @At("HEAD"),
            cancellable = true
    )
    private void originsDiversity$chorusLeechEat(
            ItemStack stack,
            World world,
            LivingEntity entity,
            CallbackInfoReturnable<ItemStack> cir
    ){
        if(!(entity instanceof PlayerEntity player)) return;
        if(!OriginsUtil.hasOrigin(player, "origins-diversity","chorus_leech")) return;
        if(!player.getHungerManager().isNotFull()){
            cir.setReturnValue(stack);
            return;
        }
        FoodComponent food = stack.getItem().getFoodComponent();
        if(food != null){
            player.getHungerManager().add(food.getHunger(), food.getSaturationModifier());
        }
        if(!player.getAbilities().creativeMode) stack.decrement(1);
        cir.setReturnValue(stack);
    }
}
