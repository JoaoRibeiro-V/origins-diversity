package com.origins_diversity.Events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;

import static com.origins_diversity.GameRules.ModGameRules.PREVENT_MOUNT_DAMAGE;

public class ParasiteEvents {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.getEntity() == null) return true;
            if (!entity.level().getGameRules().getRule(PREVENT_MOUNT_DAMAGE).get()) return true;

            if (source.getEntity() == entity.getVehicle()) return false;
            if (entity == source.getEntity().getVehicle()) return false;
            return true;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.getGameRules().getRule(PREVENT_MOUNT_DAMAGE).get()) {
                return InteractionResult.PASS;
            }

            if (isRidingEachOther(player, entity)) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.getGameRules().getRule(PREVENT_MOUNT_DAMAGE).get()) {
                return InteractionResult.PASS;
            }

            if (isRidingEachOther(player, entity)) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });
    }

    private static boolean isRidingEachOther(Entity entity1, Entity entity2) {
        return entity1.getVehicle() == entity2 || entity2.getVehicle() == entity1;
    }
}