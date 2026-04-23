package com.origins_diversity.PowerHandlers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerReference;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.*;

public class CelestialLinkHandler {

    private static final ResourceLocation CELESTIAL_LINK =
            ResourceLocation.fromNamespaceAndPath("origins-diversity", "starborn/celestial_link");

    private static final float SHARED_RATIO = 0.4f;
    private static final int LINK_COOLDOWN_TICKS = 40;

    private static final Map<UUID, UUID> linkedAllies = new HashMap<>();
    private static final Map<UUID, Long> lastLinkTick = new HashMap<>();
    private static final Map<UUID, UUID> lastLinkTarget = new HashMap<>();
    private static final Map<UUID, Long> linkCooldownTick = new HashMap<>();

    public static void tryInteract(ServerPlayer starborn, LivingEntity target) {
        if (isOnLinkCooldown(starborn)) return;
        if (isLinkedTo(starborn, target)) {
            unlink(starborn);
            starborn.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("✦ Unlinked with " + target.getName().getString() + " ✦").withStyle(style -> style.withColor(ChatFormatting.RED).withBold(true)),true
            );
            putOnCooldown(starborn);
            return;
        }

        if (tryLink(starborn, target)) {
            starborn.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("✦ Linked with " + target.getName().getString() + " ✦").withStyle(style -> style.withColor(ChatFormatting.YELLOW).withBold(true)),true
            );
        }
        putOnCooldown(starborn);
    }

    public static boolean tryLink(ServerPlayer starborn, LivingEntity target) {
        long currentTick = starborn.serverLevel().getServer().getTickCount();
        Long lastTick = lastLinkTick.get(starborn.getUUID());
        UUID lastTarget = lastLinkTarget.get(starborn.getUUID());

        if (lastTick != null && lastTick == currentTick
                && target.getUUID().equals(lastTarget)) {
            return false;
        }

        lastLinkTick.put(starborn.getUUID(), currentTick);
        lastLinkTarget.put(starborn.getUUID(), target.getUUID());
        linkCooldownTick.put(starborn.getUUID(), currentTick);
        linkedAllies.put(starborn.getUUID(), target.getUUID());
        return true;
    }

    public static void unlink(ServerPlayer starborn) {
        linkedAllies.remove(starborn.getUUID());
    }

    public static void syncEffectsToLinkedTarget(ServerPlayer starborn) {
        LivingEntity target = getLinkedTarget(starborn);
        if (target == null) return;

        // copy every active effect from starborn to the linked target
        for (net.minecraft.world.effect.MobEffectInstance effect : starborn.getActiveEffects()) {
            // only sync buff effects, not debuffs
            if (!effect.getEffect().value().isBeneficial()) continue;

            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    effect.getEffect(),
                    effect.getDuration(),
                    effect.getAmplifier(),
                    true,  // ambient
                    false  // no show particles
            ));
        }
    }

    public static boolean isOnLinkCooldown(ServerPlayer starborn) {
        Long lastTick = linkCooldownTick.get(starborn.getUUID());
        if (lastTick == null) return false;
        long currentTick = starborn.serverLevel().getServer().getTickCount();
        return currentTick - lastTick < LINK_COOLDOWN_TICKS;
    }

    public static void putOnCooldown(ServerPlayer starborn) {
        linkCooldownTick.put(starborn.getUUID(), (long) starborn.serverLevel().getServer().getTickCount());
    }

    public static boolean isLinkedTo(ServerPlayer starborn, LivingEntity target) {
        UUID current = linkedAllies.get(starborn.getUUID());
        return current != null && current.equals(target.getUUID());
    }

    public static boolean hasCelestialLink(ServerPlayer player) {
        var result = new boolean[]{false};
        PowerHolderComponent.KEY.maybeGet(player).ifPresent(holder ->
                result[0] = holder.getPowerType(PowerReference.resource(CELESTIAL_LINK).getPower()) != null
        );
        return result[0];
    }

    private static LivingEntity getLinkedTarget(ServerPlayer starborn) {
        UUID targetId = linkedAllies.get(starborn.getUUID());
        if (targetId == null) return null;

        // check players first
        ServerPlayer ally = starborn.serverLevel().getServer().getPlayerList().getPlayer(targetId);
        if (ally != null) return starborn.distanceTo(ally) <= 16 ? ally : null;

        // fall back to nearby mobs
        for (LivingEntity entity : starborn.serverLevel().getEntitiesOfClass(
                LivingEntity.class, starborn.getBoundingBox().inflate(16))) {
            if (entity.getUUID().equals(targetId)) return entity;
        }

        return null;
    }

    private static final Set<UUID> processingDamage = new HashSet<>();

    public static void onEntityHurt(LivingEntity victim, DamageSource source, float amount) {
        if (victim.level().isClientSide()) return;
        if (!(victim.level() instanceof ServerLevel serverLevel)) return;
        if (processingDamage.contains(victim.getUUID())) return;

        DamageSource sharedSource = (source.getEntity() instanceof Mob mob)
                ? serverLevel.damageSources().mobAttack(mob)
                : serverLevel.damageSources().generic();

        for (ServerPlayer starborn : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (!hasCelestialLink(starborn)) continue;

            LivingEntity linkedTarget = getLinkedTarget(starborn);
            if (linkedTarget == null) continue;

            if (linkedTarget.getUUID().equals(victim.getUUID())) {
                processingDamage.add(starborn.getUUID());
                starborn.hurt(sharedSource, amount * SHARED_RATIO);
                processingDamage.remove(starborn.getUUID());
                return;
            }

            if (starborn.getUUID().equals(victim.getUUID())) {
                processingDamage.add(linkedTarget.getUUID());
                linkedTarget.hurt(sharedSource, amount * SHARED_RATIO);
                processingDamage.remove(linkedTarget.getUUID());
            }
        }
    }
}