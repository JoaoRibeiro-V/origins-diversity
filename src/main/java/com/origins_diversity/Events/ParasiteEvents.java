package com.origins_diversity.Events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.Entity;

import static com.origins_diversity.GameRules.ModGameRules.PREVENT_MOUNT_DAMAGE;

public class ParasiteEvents {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.getAttacker() == null) return true;
            if (!entity.getWorld().getGameRules().get(PREVENT_MOUNT_DAMAGE).get()) return true;

            if (source.getAttacker() == entity.getVehicle()) return false;
            if (entity == source.getAttacker().getVehicle()) return false;
            return true;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.getGameRules().get(PREVENT_MOUNT_DAMAGE).get()) {
                return ActionResult.PASS;
            }

            if (isRidingEachOther(player, entity)) {
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.getGameRules().get(PREVENT_MOUNT_DAMAGE).get()) {
                return ActionResult.PASS;
            }

            if (isRidingEachOther(player, entity)) {
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }

    private static boolean isRidingEachOther(Entity entity1, Entity entity2) {
        return entity1.getVehicle() == entity2 || entity2.getVehicle() == entity1;
    }
}
