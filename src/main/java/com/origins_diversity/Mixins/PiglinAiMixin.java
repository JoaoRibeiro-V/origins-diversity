package com.origins_diversity.Mixins;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public abstract class PiglinAiMixin {
    @Inject(
            method = "wearsGoldArmor",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void originsDiversity$ignoreGoldForWitheredPiglins(
            LivingEntity entity,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!(entity instanceof PlayerEntity player)) return;
        if (getShouldAttack(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "canGather",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void originsDiversity$rejectGold(
            PiglinEntity piglin,
            ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        World level = piglin.getWorld();

        PlayerEntity player = (piglin.getTarget() instanceof PlayerEntity p) ? p : null;

        if (player == null) {
            player = level.getClosestPlayer(
                    piglin.getX(),
                    piglin.getY(),
                    piglin.getZ(),
                    12.0,
                    foundPlayer -> getShouldAttack((PlayerEntity) foundPlayer)
            );
        }

        if (player == null) return;
        if (getShouldAttack(player)) {
            piglin.setAttacking(true);
            piglin.setTarget(player);
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean getShouldAttack(PlayerEntity player) {
        return OriginsUtil.hasOrigin(player, "origins-diversity", "withered_piglin") || OriginsUtil.hasOrigin(player, "origins-diversity", "sculk_cultist");
    }
}
