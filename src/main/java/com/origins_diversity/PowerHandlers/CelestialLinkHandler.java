package com.origins_diversity.PowerHandlers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerTypeReference;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.*;

public class CelestialLinkHandler {

    private static final Identifier CELESTIAL_LINK =
            new Identifier("origins-diversity", "starborn/celestial_link");

    private static final PowerTypeReference<?> CELESTIAL_LINK_POWER = new PowerTypeReference<>(CELESTIAL_LINK);

    private static final float SHARED_RATIO = 0.6f;
    private static final int LINK_COOLDOWN_TICKS = 40;

    private static final Map<UUID, Set<UUID>> linkedAllies = new HashMap<>();
    private static final Map<UUID, UUID> reverseLinkedAllies = new HashMap<>();
    private static final Map<UUID, LivingEntity> entityCache = new HashMap<>();

    private static final Map<UUID, Long> lastLinkTick = new HashMap<>();
    private static final Map<UUID, UUID> lastLinkTarget = new HashMap<>();
    private static final Map<UUID, Long> linkCooldownTick = new HashMap<>();

    public static ServerPlayerEntity getStarbornLink(LivingEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return null;

        UUID starbornId = reverseLinkedAllies.get(entity.getUuid());
        if (starbornId == null) return null;

        return serverWorld.getServer()
                .getPlayerManager()
                .getPlayer(starbornId);
    }

    public static void tryInteract(ServerPlayerEntity starborn, LivingEntity target) {
        if (isOnLinkCooldown(starborn)) return;
        if (isLinkedTo(starborn, target)) {
            unlink(starborn, target);
            starborn.sendMessage(
                    Text.literal("✦ Unlinked with " + target.getName().getString() + " ✦").styled(style -> style.withColor(Formatting.RED).withBold(true)),true
            );
            putOnCooldown(starborn);
            return;
        }

        if (tryLink(starborn, target)) {
            starborn.sendMessage(
                    Text.literal("✦ Linked with " + target.getName().getString() + " ✦").styled(style -> style.withColor(Formatting.YELLOW).withBold(true)),true
            );
        }
        putOnCooldown(starborn);
    }

    public static boolean tryLink(ServerPlayerEntity starborn, LivingEntity target) {
        UUID starbornId = starborn.getUuid();
        UUID targetId = target.getUuid();
        long currentTick = ((ServerWorld) starborn.getWorld()).getServer().getTicks();
        Long lastTick = lastLinkTick.get(starborn.getUuid());
        UUID lastTarget = lastLinkTarget.get(starborn.getUuid());

        if (lastTick != null && lastTick == currentTick
                && target.getUuid().equals(lastTarget)) {
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
        lastLinkTick.put(starborn.getUuid(), currentTick);
        lastLinkTarget.put(starborn.getUuid(), target.getUuid());
        linkCooldownTick.put(starborn.getUuid(), currentTick);
        linkedAllies
                .computeIfAbsent(starborn.getUuid(), k -> new HashSet<>())
                .add(target.getUuid());
        reverseLinkedAllies.put(targetId, starbornId);
        return true;
    }

    public static void unlink(ServerPlayerEntity starborn, LivingEntity target) {
        UUID starbornId = starborn.getUuid();
        UUID targetId = target.getUuid();
        Set<UUID> links = linkedAllies.get(starbornId);
        if (links != null) {
            links.remove(targetId);

            if (links.isEmpty()) {
                linkedAllies.remove(starbornId);
            }
        }

        reverseLinkedAllies.remove(targetId);
    }

    public static void unlinkAll(ServerPlayerEntity starborn) {
        UUID starbornId = starborn.getUuid();

        Set<UUID> links = linkedAllies.remove(starbornId);
        if (links != null) {
            for (UUID targetId : links) {
                reverseLinkedAllies.remove(targetId);
            }
        }
    }

    public static void syncEffectsToLinkedTargets(ServerPlayerEntity starborn) {
        for (LivingEntity target : getLinkedTargets(starborn)) {

            for (var effect : starborn.getStatusEffects()) {
                if (!effect.getEffectType().isBeneficial()) continue;

                target.addStatusEffect(new StatusEffectInstance(
                        effect.getEffectType(),
                        effect.getDuration(),
                        effect.getAmplifier(),
                        true,
                        false
                ));
            }
        }
    }

    public static boolean isOnLinkCooldown(ServerPlayerEntity starborn) {
        Long lastTick = linkCooldownTick.get(starborn.getUuid());
        if (lastTick == null) return false;
        long currentTick = ((ServerWorld) starborn.getWorld()).getServer().getTicks();
        return currentTick - lastTick < LINK_COOLDOWN_TICKS;
    }

    public static void putOnCooldown(ServerPlayerEntity starborn) {
        linkCooldownTick.put(starborn.getUuid(), (long) ((ServerWorld) starborn.getWorld()).getServer().getTicks());
    }

    public static boolean isLinkedTo(ServerPlayerEntity starborn, LivingEntity target) {
        return starborn.getUuid().equals(
                reverseLinkedAllies.get(target.getUuid())
        );
    }

    public static boolean hasCelestialLink(LivingEntity entity) {
        var result = new boolean[]{false};
        PowerHolderComponent.KEY.maybeGet(entity).ifPresent(holder ->
                result[0] = holder.hasPower(CELESTIAL_LINK_POWER)
        );
        return result[0];
    }

    private static LivingEntity getEntityByUUID(ServerWorld level, UUID uuid) {
        LivingEntity cached = entityCache.get(uuid);
        if (cached != null && !cached.isRemoved()) {
            return cached;
        }

        return level.getServer().getPlayerManager().getPlayer(uuid);
    }

    public static Set<LivingEntity> getLinkedTargets(ServerPlayerEntity starborn) {
        if (!(starborn.getWorld() instanceof ServerWorld serverWorld)) return Collections.emptySet();

        Set<UUID> ids = linkedAllies.get(starborn.getUuid());
        if (ids == null || ids.isEmpty()) return Collections.emptySet();

        Set<LivingEntity> result = new HashSet<>();

        for (UUID id : ids) {
            LivingEntity entity = getEntityByUUID(serverWorld, id);
            if (entity != null && starborn.distanceTo(entity) <= 16) {
                result.add(entity);
            }
        }

        return result;
    }

    private static final Set<UUID> processingDamage = new HashSet<>();

    public static void onEntityHurt(LivingEntity victim, DamageSource source, float amount) {
        if (victim.getWorld().isClient()) return;
        if (!(victim.getWorld() instanceof ServerWorld serverWorld)) return;

        UUID victimId = victim.getUuid();
        if (processingDamage.contains(victimId)) return;

        boolean isFatal = (victim.getHealth() - amount) <= 0.0f;

        // determine proper damage source
        DamageSource sharedSource = (source.getAttacker() instanceof MobEntity mob)
                ? serverWorld.getDamageSources().mobAttack(mob)
                : serverWorld.getDamageSources().generic();

        ServerPlayerEntity owner = getStarbornLink(victim);
        if (owner != null && hasCelestialLink(owner)) {
            if (isFatal) {
                unlink(owner, victim);
                return;
            }
            UUID ownerId = owner.getUuid();
            if (!processingDamage.contains(ownerId)) {
                processingDamage.add(ownerId);
                owner.damage(sharedSource, amount * SHARED_RATIO);
                processingDamage.remove(ownerId);
            }
            return;
        }
        if (victim instanceof ServerPlayerEntity starborn) {
            Set<UUID> links = linkedAllies.get(starborn.getUuid());
            if (links == null || links.isEmpty()) return;

            if (isFatal) {
                unlinkAll(starborn);
                return;
            }

            for (UUID targetId : links) {
                LivingEntity target = getEntityByUUID(serverWorld, targetId);
                if (target == null) continue;

                UUID targetUUID = target.getUuid();
                if (processingDamage.contains(targetUUID)) continue;

                processingDamage.add(targetUUID);
                target.damage(sharedSource, amount * SHARED_RATIO);
                processingDamage.remove(targetUUID);
            }
        }
    }
}
