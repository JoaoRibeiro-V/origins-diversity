package com.origins_diversity.PowerHandlers;

import com.origins_diversity.Entities.ModEntities;
import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.Entities.SculkZombieEntity;
import com.origins_diversity.Extra.EmissiveParticleOptions;
import com.origins_diversity.Extra.ModParticles;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.util.Unit;
import net.minecraft.util.math.Vec3d;

import org.joml.Vector3f;

import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public class SculkSummonListener {

    private static final double SPAWN_BEHIND_DISTANCE = -3.5;

    private static final List<long[]> taskTicks = new ArrayList<>();
    private static final List<Runnable> taskRunnables = new ArrayList<>();


    private static final EmissiveParticleOptions SCULK_1 =
            ModParticles.emissive(ModParticles.BUBBLE_POP, 0.6f, 0.0f, 1.0f, 0.182f, 4);

    private static final EmissiveParticleOptions SCULK_2 =
            ModParticles.emissive(ModParticles.BUBBLE_POP, 0.15f, 0.0f, 0.25f, 0.25f, 12);


    private static final DustParticleEffect DUST_DARK_PURPLE =
            new DustParticleEffect(new Vector3f(0.25f, 0.0f, 0.35f), 1.4f);

    private static final DustParticleEffect DUST_DEEP_VIOLET =
            new DustParticleEffect(new Vector3f(0.15f, 0.0f, 0.25f), 1.0f);

    private static final DustParticleEffect DUST_BLACK_PURPLE =
            new DustParticleEffect(new Vector3f(0.08f, 0.0f, 0.12f), 1.8f);

    private static final DustParticleEffect DUST_PILLAR =
            new DustParticleEffect(new Vector3f(0.20f, 0.0f, 0.30f), 1.2f);


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerWorld level : server.getWorlds()) {

                for (ServerPlayerEntity player : level.getPlayers()) {

                    if (player.getCommandTags().contains("sculk_cultist_ritual_pending")) {

                        player.removeScoreboardTag("sculk_cultist_ritual_pending");

                        spawnAndStartRitual(player, level);
                    }


                    if (player.getCommandTags().contains("sculk_zombie_summon")) {

                        player.removeScoreboardTag("sculk_zombie_summon");

                        double x = player.getX() + 1;
                        double y = player.getY();
                        double z = player.getZ() + 1;

                        triggerZombieSummon(level, player, x, y, z);
                    }
                }
            }


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


    private static void scheduleTask(long delayTicks, Runnable task) {

        if (delayTicks <= 0) {
            task.run();
            return;
        }

        taskTicks.add(new long[]{delayTicks});
        taskRunnables.add(task);
    }    /*
     * Sculk Zombie summon
     */

    private static void triggerZombieSummon(
            ServerWorld level,
            ServerPlayerEntity player,
            double x,
            double y,
            double z
    ) {

        scheduleTask(0, new Runnable() {

            int tick = 0;

            @Override
            public void run() {

                if (tick > 60) {

                    SculkZombieEntity zombie =
                            new SculkZombieEntity(ModEntities.SCULK_ZOMBIE, level);

                    zombie.setSummoner(player);

                    zombie.refreshPositionAndAngles(x, y, z, 0, 0);

                    level.spawnEntity(zombie);


                    for (int i = 0; i < 30; i++) {

                        double angle = Math.random() * Math.PI * 2;

                        level.spawnParticles(
                                ParticleTypes.REVERSE_PORTAL,
                                x + Math.cos(angle) * 0.5,
                                y + 0.5,
                                z + Math.sin(angle) * 0.5,
                                1,
                                0,
                                0.1,
                                0,
                                0.05
                        );
                    }

                    return;
                }


                double radius = tick < 30
                        ? (tick / 30.0) * 1.5
                        : ((60 - tick) / 30.0) * 1.5;


                // Dark ring
                if (tick % 2 == 0) {

                    for (int i = 0; i < 20; i++) {

                        double angle = (2 * Math.PI / 20) * i;

                        double px = x + Math.cos(angle) * radius;
                        double pz = z + Math.sin(angle) * radius;


                        level.spawnParticles(
                                ModParticles.emissive(
                                        ModParticles.SCULK_CHARGE_POP,
                                        0.6f,
                                        0.0f,
                                        1.0f,
                                        0.12f,
                                        new Random().nextInt(4, 12)
                                ),
                                px,
                                y + 0.05,
                                pz,
                                1,
                                0,
                                0,
                                0,
                                0
                        );
                    }
                }


                // Rising smoke
                if (tick > 20) {

                    for (int i = 0; i < 3; i++) {

                        level.spawnParticles(
                                ParticleTypes.LARGE_SMOKE,
                                x + (Math.random() - 0.5) * radius,
                                y + (tick - 20) / 40.0 * 2,
                                z + (Math.random() - 0.5) * radius,
                                1,
                                0,
                                0.02,
                                0,
                                0.01
                        );
                    }
                }


                tick++;

                scheduleTask(1, this);
            }
        });
    }


    /*
     * Sculk servant summon
     */


    private static void setScale(WardenEntity warden, float scale) {

        ScaleData scaleData = ScaleTypes.BASE.getScaleData(warden);

        scaleData.setScale(scale);
    }


    private static void spawnAndStartRitual(
            ServerPlayerEntity player,
            ServerWorld level
    ) {

        float yaw = player.getYaw();

        double radians = Math.toRadians(yaw);

        double x = player.getX() - Math.sin(radians) * SPAWN_BEHIND_DISTANCE;
        double z = player.getZ() + Math.cos(radians) * SPAWN_BEHIND_DISTANCE;
        double y = player.getY();


        SculkServantEntity warden =
                new SculkServantEntity(ModEntities.SCULK_SERVANT, level);


        warden.refreshPositionAndAngles(x, y, z, yaw, 0);

        warden.setBodyYaw(yaw);
        warden.setHeadYaw(player.getHeadYaw());

        warden.setInvisible(true);
        warden.setInvulnerable(true);

        warden.setAiDisabled(true);
        warden.setPersistent();


        level.spawnEntity(warden);


        warden.addCommandTag("sculk_ritual_active");

        warden.addCommandTag(
                "summoner_" + player.getUuid().toString()
        );


        warden.getBrain().remember(
                MemoryModuleType.DIG_COOLDOWN,
                Unit.INSTANCE,
                3600L
        );


        setScale(warden, 0.1f);


        scheduleTask(1, () -> {

            if (warden.isRemoved()) {
                return;
            }


            warden.setInvisible(false);


            level.playSound(
                    null,
                    x,
                    y,
                    z,
                    SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                    SoundCategory.MASTER,
                    2f,
                    0.5f
            );


            triggerRitualCutscene(
                    warden,
                    new Vec3d(x, y, z),
                    level,
                    player
            );
        });
    }    private static void triggerRitualCutscene(
            SculkServantEntity warden,
            Vec3d pos,
            ServerWorld level,
            ServerPlayerEntity player
    ) {

        // Darkness pulse
        level.getPlayers(
                p -> p.squaredDistanceTo(pos.x, pos.y, pos.z) < 400
        ).forEach(
                p -> p.addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.DARKNESS,
                                160,
                                1,
                                false,
                                false
                        )
                )
        );


        /*
         * Ambient swirling particles
         */

        scheduleTask(0, new Runnable() {

            int life = 120;

            @Override
            public void run() {

                if (life-- <= 0 || warden.isRemoved()) {
                    return;
                }


                for (int i = 0; i < 3; i++) {

                    double radius = 6.0;

                    double offsetX = (Math.random() - 0.5) * radius * 2;
                    double offsetZ = (Math.random() - 0.5) * radius * 2;
                    double offsetY = Math.random() * 3;


                    double px = pos.x + offsetX;
                    double py = pos.y + offsetY;
                    double pz = pos.z + offsetZ;


                    double dx = (pos.x - px) * 0.03;
                    double dz = (pos.z - pz) * 0.03;


                    ParticleEffect dust =
                            life % 2 == 0 ? SCULK_1 : SCULK_2;


                    level.spawnParticles(
                            dust,
                            px,
                            py,
                            pz,
                            1,
                            dx,
                            0.01,
                            dz,
                            0
                    );


                    if (i == 0) {

                        level.spawnParticles(
                                ParticleTypes.SMOKE,
                                px,
                                py,
                                pz,
                                1,
                                dx * 0.5,
                                0.01,
                                dz * 0.5,
                                0.005
                        );
                    }
                }


                scheduleTask(1, this);
            }
        });



        /*
         * Rise + grow animation
         */

        for (int i = 0; i <= 80; i++) {

            final int tick = i;

            final double riseY = pos.y + (tick / 80.0);

            final float scale =
                    0.1f + (tick / 80.0f) * 1.499f;


            scheduleTask(tick, () -> {

                if (warden.isRemoved()) {
                    return;
                }


                warden.setPos(
                        warden.getX(),
                        riseY,
                        warden.getZ()
                );


                warden.setYaw(player.getYaw());
                warden.setBodyYaw(player.getBodyYaw());
                warden.setHeadYaw(player.getHeadYaw());


                setScale(warden, scale);



                if (tick % 3 == 0) {

                    level.spawnParticles(
                            DUST_DARK_PURPLE,
                            warden.getX(),
                            warden.getY() + 0.5,
                            warden.getZ(),
                            3,
                            0.3,
                            0.15,
                            0.3,
                            0.02
                    );


                    level.spawnParticles(
                            DUST_DEEP_VIOLET,
                            warden.getX(),
                            warden.getY(),
                            warden.getZ(),
                            2,
                            0.4,
                            0.1,
                            0.4,
                            0.03
                    );


                    level.spawnParticles(
                            ParticleTypes.LARGE_SMOKE,
                            warden.getX(),
                            warden.getY(),
                            warden.getZ(),
                            1,
                            0.3,
                            0,
                            0.3,
                            0.01
                    );
                }



                if (tick % 5 == 0) {

                    level.spawnParticles(
                            ParticleTypes.REVERSE_PORTAL,
                            warden.getX(),
                            warden.getY() + 0.2,
                            warden.getZ(),
                            4,
                            0.5,
                            0.3,
                            0.5,
                            0.06
                    );
                }
            });
        }        /*
         * Shadow pillars
         */

        for (int p = 0; p < 4; p++) {

            double angle = p * (Math.PI / 2.0);

            double px = pos.x + Math.cos(angle) * 4.5;
            double pz = pos.z + Math.sin(angle) * 4.5;


            for (int h = 0; h < 20; h++) {

                final double height = h;


                scheduleTask(30 + (h * 2), () -> {

                    level.spawnParticles(
                            DUST_PILLAR,
                            px,
                            pos.y + height * 0.45,
                            pz,
                            2,
                            0.06,
                            0,
                            0.06,
                            0.01
                    );


                    level.spawnParticles(
                            ParticleTypes.SMOKE,
                            px,
                            pos.y + height * 0.45,
                            pz,
                            1,
                            0.03,
                            0.02,
                            0.03,
                            0.005
                    );
                });
            }
        }



        /*
         * Witch sparks
         */

        scheduleTask(30, () -> {

            for (int p = 0; p < 4; p++) {

                double angle = p * (Math.PI / 2.0);


                level.spawnParticles(
                        ParticleTypes.WITCH,
                        pos.x + Math.cos(angle) * 4.5,
                        pos.y + 0.5,
                        pos.z + Math.sin(angle) * 4.5,
                        12,
                        0.2,
                        0.3,
                        0.2,
                        0.05
                );
            }
        });



        scheduleTask(
                40,
                () -> level.playSound(
                        null,
                        pos.x,
                        pos.y,
                        pos.z,
                        SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
                        SoundCategory.HOSTILE,
                        2f,
                        0.5f
                )
        );


        scheduleTask(
                70,
                () -> level.playSound(
                        null,
                        pos.x,
                        pos.y,
                        pos.z,
                        SoundEvents.ENTITY_WARDEN_ROAR,
                        SoundCategory.HOSTILE,
                        2.5f,
                        0.8f
                )
        );



        /*
         * Awakening
         */

        scheduleTask(105, () -> {

            if (warden.isRemoved()) {
                return;
            }


            setScale(warden, 1.5f);


            warden.setHealth(600.0f);

            warden.setInvulnerable(false);

            warden.setAiDisabled(false);


            warden.removeScoreboardTag(
                    "sculk_ritual_active"
            );



            for (int i = 0; i < 3; i++) {

                level.spawnParticles(
                        DUST_BLACK_PURPLE,
                        warden.getX(),
                        warden.getY() + 1,
                        warden.getZ(),
                        20,
                        1,
                        0.5,
                        1,
                        0.15
                );


                level.spawnParticles(
                        DUST_DARK_PURPLE,
                        warden.getX(),
                        warden.getY() + 1,
                        warden.getZ(),
                        15,
                        0.8,
                        0.4,
                        0.8,
                        0.2
                );
            }



            level.spawnParticles(
                    ParticleTypes.LARGE_SMOKE,
                    warden.getX(),
                    warden.getY() + 1,
                    warden.getZ(),
                    20,
                    0.8,
                    0.5,
                    0.8,
                    0.05
            );


            level.spawnParticles(
                    ParticleTypes.WITCH,
                    warden.getX(),
                    warden.getY() + 1,
                    warden.getZ(),
                    30,
                    1,
                    0.5,
                    1,
                    0.1
            );


            level.spawnParticles(
                    ParticleTypes.EXPLOSION_EMITTER,
                    warden.getX(),
                    warden.getY() + 1,
                    warden.getZ(),
                    1,
                    0,
                    0,
                    0,
                    0
            );



            level.playSound(
                    null,
                    warden.getX(),
                    warden.getY(),
                    warden.getZ(),
                    SoundEvents.ENTITY_WARDEN_EMERGE,
                    SoundCategory.HOSTILE,
                    3f,
                    1f
            );



            level.getPlayers(
                    p -> p.squaredDistanceTo(
                            warden.getX(),
                            warden.getY(),
                            warden.getZ()
                    ) < 400
            ).forEach(
                    p -> p.addStatusEffect(
                            new StatusEffectInstance(
                                    StatusEffects.DARKNESS,
                                    60,
                                    1,
                                    false,
                                    false
                            )
                    )
            );
        });
    }
}