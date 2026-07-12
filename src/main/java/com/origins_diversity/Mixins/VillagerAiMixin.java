package com.origins_diversity.Mixins;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.origins_diversity.PowerHandlers.AvoidSculkCultistBehavior;

import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.passive.VillagerEntity;

import net.minecraft.entity.ai.brain.Activity;

import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(VillagerTaskListProvider.class)
public class VillagerAiMixin {

    @Inject(
            method = "createCoreTasks",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void addSculkAvoidTask(
            VillagerProfession profession, float speed, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir
    ) {
        List<Pair<Integer, ? extends Task<? super VillagerEntity>>> tasks =
                new java.util.ArrayList<>(cir.getReturnValue());

        tasks.add(
                Pair.of(
                        0,
                        new AvoidSculkCultistBehavior()
                )
        );

        cir.setReturnValue(ImmutableList.copyOf(tasks));
    }
}