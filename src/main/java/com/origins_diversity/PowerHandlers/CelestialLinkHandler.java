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

    private static final float SHARED_RATIO = 0.6f;
    private static final int LINK_COOLDOWN_TICKS = 40;

    private static final Map<UUID, Set<UUID>> linkedAllies = new HashMap<>();
    private static final Map<UUID, UUID> reverseLinkedAllies = new HashMap<>();
    private static final Map<UUID, LivingEntity> entityCache = new HashMap<>();

    private static final Map<UUID, Long> lastLinkTick = new HashMap<>();
    private static final Map<UUID, UUID> lastLinkTarget = new HashMap<>();
    private static final Map<UUID, Long> linkCooldownTick = new HashMap<>();

    public static ServerPlayer getStarbornLink(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return null;

        UUID starbornId = reverseLinkedAllies.get(entity.getUUID());
        if (starbornId == null) return null;

        return serverLevel.getServer()
                .getPlayerList()
                .getPlayer(starbornId);
    }

    public static void tryInteract(ServerPlayer starborn, LivingEntity target) {
        if (isOnLinkCooldown(starborn)) return;
        if (isLinkedTo(starborn, target)) {
            unlink(starborn, target);
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
        UUID starbornId = starborn.getUUID();
        UUID targetId = target.getUUID();
        long currentTick = starborn.serverLevel().getServer().getTickCount();
        Long lastTick = lastLinkTick.get(starborn.getUUID());
        UUID lastTarget = lastLinkTarget.get(starborn.getUUID());

        if (lastTick != null && lastTick == currentTick
                && target.getUUID().equals(lastTarget)) {
            return false;
        }

        UUID existingOwner = reverseLinkedAllies.get(targetId);
        if (existingOwner != null && !existingOwner.equals(starbornId)) {
            // remove from previous starborn
            Set<UUID> oldSet = linkedAllies.get(existingOwner);
            if (oldSet != null) {
                oldSet.remove(targetId);
                if (oldSet.isEmpty()) {
                    linkedAllies.remove(existingOwner);
                }
            }
            reverseLinkedAllies.remove(targetId);
        }

        entityCache.put(targetId, target);
        lastLinkTick.put(starborn.getUUID(), currentTick);
        lastLinkTarget.put(starborn.getUUID(), target.getUUID());
        linkCooldownTick.put(starborn.getUUID(), currentTick);
        linkedAllies
                .computeIfAbsent(starborn.getUUID(), k -> new HashSet<>())
                .add(target.getUUID());
        reverseLinkedAllies.put(targetId, starbornId);
        return true;
    }

    public static void unlink(ServerPlayer starborn, LivingEntity target) {
        UUID starbornId = starborn.getUUID();
        UUID targetId = target.getUUID();
        Set<UUID> links = linkedAllies.get(starbornId);
        if (links != null) {
            links.remove(targetId);

            if (links.isEmpty()) {
                linkedAllies.remove(starbornId);
            }
        }

        reverseLinkedAllies.remove(targetId);
    }

    public static void unlinkAll(ServerPlayer starborn) {
        UUID starbornId = starborn.getUUID();

        Set<UUID> links = linkedAllies.remove(starbornId);
        if (links != null) {
            for (UUID targetId : links) {
                reverseLinkedAllies.remove(targetId);
            }
        }
    }

    public static void syncEffectsToLinkedTargets(ServerPlayer starborn) {
        for (LivingEntity target : getLinkedTargets(starborn)) {

            for (var effect : starborn.getActiveEffects()) {
                if (!effect.getEffect().value().isBeneficial()) continue;

                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        effect.getEffect(),
                        effect.getDuration(),
                        effect.getAmplifier(),
                        true,
                        false
                ));
            }
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
        return starborn.getUUID().equals(
                reverseLinkedAllies.get(target.getUUID())
        );
    }

    public static boolean hasCelestialLink(LivingEntity entity) {
        var result = new boolean[]{false};
        PowerHolderComponent.KEY.maybeGet(entity).ifPresent(holder ->
                result[0] = holder.getPowerType(PowerReference.resource(CELESTIAL_LINK).getPower()) != null
        );
        return result[0];
    }

    private static LivingEntity getEntityByUUID(ServerLevel level, UUID uuid) {
        LivingEntity cached = entityCache.get(uuid);
        if (cached != null && !cached.isRemoved()) {
            return cached;
        }

        return level.getServer().getPlayerList().getPlayer(uuid);
    }

    public static Set<LivingEntity> getLinkedTargets(ServerPlayer starborn) {
        if (!(starborn.level() instanceof ServerLevel serverLevel)) return Collections.emptySet();

        Set<UUID> ids = linkedAllies.get(starborn.getUUID());
        if (ids == null || ids.isEmpty()) return Collections.emptySet();

        Set<LivingEntity> result = new HashSet<>();

        for (UUID id : ids) {
            LivingEntity entity = getEntityByUUID(serverLevel, id);
            if (entity != null && starborn.distanceTo(entity) <= 16) {
                result.add(entity);
            }
        }

        return result;
    }

    private static final Set<UUID> processingDamage = new HashSet<>();

    public static void onEntityHurt(LivingEntity victim, DamageSource source, float amount) {
        if (victim.level().isClientSide()) return;
        if (!(victim.level() instanceof ServerLevel serverLevel)) return;

        UUID victimId = victim.getUUID();
        if (processingDamage.contains(victimId)) return;

        boolean isFatal = (victim.getHealth() - amount) <= 0.0f;

        // determine proper damage source
        DamageSource sharedSource = (source.getEntity() instanceof Mob mob)
                ? serverLevel.damageSources().mobAttack(mob)
                : serverLevel.damageSources().generic();

        ServerPlayer owner = getStarbornLink(victim);
        if (owner != null && hasCelestialLink(owner)) {
            if (isFatal) {
                unlink(owner, victim);
                return;
            }
            UUID ownerId = owner.getUUID();
            if (!processingDamage.contains(ownerId)) {
                processingDamage.add(ownerId);
                owner.hurt(sharedSource, amount * SHARED_RATIO);
                processingDamage.remove(ownerId);
            }
            return;
        }
        if (victim instanceof ServerPlayer starborn) {
            Set<UUID> links = linkedAllies.get(starborn.getUUID());
            if (links == null || links.isEmpty()) return;

            if (isFatal) {
                unlinkAll(starborn);
                return;
            }

            for (UUID targetId : links) {
                LivingEntity target = getEntityByUUID(serverLevel, targetId);
                if (target == null) continue;

                UUID targetUUID = target.getUUID();
                if (processingDamage.contains(targetUUID)) continue;

                processingDamage.add(targetUUID);
                target.hurt(sharedSource, amount * SHARED_RATIO);
                processingDamage.remove(targetUUID);
            }
        }
    }
}