package com.origins_diversity.PowerHandlers;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SculkSummonListener {

    // Distance behind the player where the Warden spawns
    private static final double SPAWN_BEHIND_DISTANCE = -3.5;

    // Simple tick-based scheduler (runs tasks after X ticks)
    private static final List<long[]> taskTicks = new ArrayList<>();
    private static final List<Runnable> taskRunnables = new ArrayList<>();

    /**
     * Main loop:
     * - Detects players who triggered the ritual
     * - Updates scheduled tasks every tick
     */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            // Trigger ritual if player has the tag
            for (ServerLevel level : server.getAllLevels()) {
                for (ServerPlayer player : level.players()) {
                    if (player.getTags().contains("sculk_cultist_ritual_pending")) {
                        player.removeTag("sculk_cultist_ritual_pending");
                        spawnAndStartRitual(player, level);
                    }
                }
            }

            // Run scheduled tasks
            if (!taskTicks.isEmpty()) {
                Iterator<long[]> tickIter = taskTicks.iterator();
                Iterator<Runnable> runIter = taskRunnables.iterator();
                List<Runnable> toRun = new ArrayList<>();

                while (tickIter.hasNext()) {
                    long[] remaining = tickIter.next();
                    Runnable task = runIter.next();

                    if (--remaining[0] <= 0) {
                        toRun.add(task);
                        tickIter.remove();
                        runIter.remove();
                    }
                }

                toRun.forEach(Runnable::run);
            }
        });
    }

    /**
     * Schedules a task to run after a delay (in ticks)
     */
    private static void scheduleTask(long delayTicks, Runnable task) {
        if (delayTicks <= 0) {
            task.run();
            return;
        }
        taskTicks.add(new long[]{delayTicks});
        taskRunnables.add(task);
    }

    /**
     * Sets Warden scale using Pehkui API
     */
    private static void setScale(Warden warden, ServerLevel level, float scale) {
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(warden);
        scaleData.setScale(scale);
    }

    /**
     * Spawns the Warden behind the player and initializes it
     */
    private static void spawnAndStartRitual(ServerPlayer player, ServerLevel level) {

        // Calculate position behind player
        float yaw = player.getYRot();
        double radians = Math.toRadians(yaw);

        double x = player.getX() - Math.sin(radians) * SPAWN_BEHIND_DISTANCE;
        double z = player.getZ() + Math.cos(radians) * SPAWN_BEHIND_DISTANCE;
        double y = player.getY();

        // Create Warden
        Warden warden = new Warden(net.minecraft.world.entity.EntityType.WARDEN, level);
        warden.moveTo(x, y, z, yaw, 0f);

        // Match player rotation
        warden.setYRot(yaw);
        warden.setYBodyRot(yaw);
        warden.setXRot(player.getXRot());
        warden.setYHeadRot(yaw);

        // Initial state (hidden + frozen)
        warden.setInvisible(true);
        warden.setInvulnerable(true);
        warden.setNoAi(true);
        warden.setPersistenceRequired();

        // Spawn in world
        level.addFreshEntity(warden);

        // Buff health
        Objects.requireNonNull(warden.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(750.0);
        warden.setHealth(750.0f);

        // Prevent digging/despawning
        warden.getBrain().setMemoryWithExpiry(
                MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 3600L);

        // Start small + glowing
        setScale(warden, level, 0.1f);
        warden.setGlowingTag(true);

        // Start ritual next tick
        scheduleTask(1, () -> {
            if (warden.isRemoved()) return;

            warden.setInvisible(false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.MASTER, 2f, 0.5f);
            triggerRitualCutscene(warden, new Vec3(x, y, z), level, player);
        });
    }

    /**
     * Handles the full ritual animation sequence
     */
    private static void triggerRitualCutscene(Warden warden, Vec3 pos, ServerLevel level, ServerPlayer player) {

        // Apply darkness to nearby players
        level.getPlayers(p -> p.distanceToSqr(pos.x, pos.y, pos.z) < 400)
                .forEach(p -> p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 160, 1, false, false)));

       // particles during ritual
        scheduleTask(0, new Runnable() {
            int life = 120; // lasts entire ritual (~6 seconds)

            @Override
            public void run() {
                if (life-- <= 0) return;
                if (warden.isRemoved()) return;

                for (int i = 0; i < 2; i++) {
                    double radius = 6.0;

                    double offsetX = (Math.random() - 0.5) * radius * 2;
                    double offsetZ = (Math.random() - 0.5) * radius * 2;
                    double offsetY = Math.random() * 3;

                    double x = pos.x + offsetX;
                    double y = pos.y + offsetY;
                    double z = pos.z + offsetZ;

                    // slight pull toward center
                    double dx = pos.x - x;
                    double dz = pos.z - z;

                    level.sendParticles(
                            ParticleTypes.BUBBLE_POP,
                            x, y, z,
                            1,
                            dx * 0.02, 0.02, dz * 0.02,
                            0
                    );
                }

                // loop
                scheduleTask(1, this);
            }
        });
        // Main animation: rise + grow
        for (int i = 0; i <= 80; i++) {
            final int tick = i;
            final double y = pos.y + (tick / 80.0);
            final float scale = 0.1f + (tick / 80.0f) * 1.499f;

            scheduleTask(tick, () -> {
                if (warden.isRemoved()) return;

                // Move + face player
                warden.setPos(warden.getX(), y, warden.getZ());
                warden.setYRot(player.getYRot());
                warden.setYBodyRot(player.getYRot());
                warden.setXRot(player.getXRot());
                warden.setYHeadRot(player.getYRot());

                // Grow over time
                setScale(warden, level, scale);

                // Particles
                if (tick % 3 == 0) {
                    level.sendParticles(ParticleTypes.SCULK_SOUL,
                            warden.getX(), warden.getY() + 0.5, warden.getZ(),
                            2, 0.3, 0.1, 0.3, 0.04);

                    level.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                            warden.getX(), warden.getY(), warden.getZ(),
                            3, 0.5, 0.3, 0.5, 0.1);
                }
            });
        }

        // VFX soul pillars
        for (int p = 0; p < 4; p++) {
            double angle = p * (Math.PI / 2);
            double px = pos.x + Math.cos(angle) * 4.5;
            double pz = pos.z + Math.sin(angle) * 4.5;

            for (int h = 0; h < 20; h++) {
                final double height = h;
                scheduleTask(30 + (h * 2), () ->
                        level.sendParticles(ParticleTypes.SOUL,
                                px, pos.y + height * 0.45, pz,
                                2, 0.08, 0, 0.08, 0.02)
                );
            }
        }

        // Sound effects
        scheduleTask(40, () ->
                level.playSound(null, pos.x, pos.y, pos.z,
                        SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2f, 0.5f));

        scheduleTask(70, () ->
                level.playSound(null, pos.x, pos.y, pos.z,
                        SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.5f, 0.8f));

        // Final phase: awaken
        scheduleTask(105, () -> {
            if (warden.isRemoved()) return;

            setScale(warden, level, 1.5f);
            warden.setGlowingTag(false);

            warden.setInvulnerable(false);
            warden.setNoAi(false);

            level.playSound(null, warden.getX(), warden.getY(), warden.getZ(),
                    SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 3f, 1f);

            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    warden.getX(), warden.getY() + 1, warden.getZ(),
                    1, 0, 0, 0, 0);
        });
    }
}