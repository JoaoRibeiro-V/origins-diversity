package com.origins_diversity.Mixins;

import com.origins_diversity.Extra.OriginsUtil;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
    @Inject(
            method = "isWearingGold",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void originsDiversity$ignoreGoldForWitheredPiglins(
            LivingEntity entity,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!(entity instanceof Player player)) return;
        if (getShouldAttack(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "wantsToPickup",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void originsDiversity$rejectGold(
            Piglin piglin,
            ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {

        if (!(piglin.getTarget() instanceof Player player)) return;
        if (getShouldAttack(player)) {
            piglin.setAggressive(true);
            piglin.setTarget(player);
            cir.setReturnValue(false);
            return;
        }
    }

    @Unique
    private static boolean getShouldAttack(Player player) {
        return OriginsUtil.hasOrigin(player, "origins-diversity", "withered_piglin") || OriginsUtil.hasOrigin(player, "origins-diversity", "sculk_cultist");
    }
}
