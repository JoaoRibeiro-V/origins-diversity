package com.origins_diversity.Mixins;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.origins_diversity.Extra.OriginsUtil;
import com.origins_diversity.PowerHandlers.AvoidSculkCultistBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.VillagerCalmDown;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Villager.class)
public class VillagerAiMixin {
    @Inject(method = "registerBrainGoals", at = @At("TAIL"))
    private void injectAvoidBehavior(Brain<Villager> brain, CallbackInfo ci) {

        List<Pair<Integer, ? extends BehaviorControl<? super Villager>>> core = new ArrayList<>();

        core.add(Pair.of(0, new AvoidSculkCultistBehavior()));

        core.add(Pair.of(
                0,
                (BehaviorControl<? super Villager>) (Object)
                        SetWalkTargetAwayFrom.entity(
                                MemoryModuleType.AVOID_TARGET,
                                1.75F,
                                10,
                                false
                        )
        ));

        brain.addActivity(Activity.CORE, ImmutableList.copyOf(core));
    }
}
