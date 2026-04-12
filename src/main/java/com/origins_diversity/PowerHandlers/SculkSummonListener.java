package com.origins_diversity.PowerHandlers;

import com.origins_diversity.Entities.ModEntities;
import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.Entities.SculkZombieEntity;
import com.origins_diversity.Extra.EmissiveParticleOptions;
import com.origins_diversity.Extra.ModParticles;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SculkSummonListener {

    private static final double SPAWN_BEHIND_DISTANCE = -3.5;

    private static final List<long[]> taskTicks = new ArrayList<>();
    private static final List<Runnable> taskRunnables = new ArrayList<>();

    // Dust particle colors
    private static final EmissiveParticleOptions SCULK_1 = ModParticles.emissive(ModParticles.BUBBLE_POP, 0.6f, 0.0f, 1.0f, 0.182f, 4);
    private static final EmissiveParticleOptions SCULK_2 = ModParticles.emissive(ModParticles.BUBBLE_POP, 0.15f, 0.0f, 0.25f, 0.25f, 12);
    private static final DustParticleOptions DUST_DARK_PURPLE = new DustParticleOptions(new Vector3f(0.25f, 0.0f, 0.35f), 1.4f);
    private static final DustParticleOptions DUST_DEEP_VIOLET = new DustParticleOptions(new Vector3f(0.15f, 0.0f, 0.25f), 1.0f);
    private static final DustParticleOptions DUST_BLACK_PURPLE = new DustParticleOptions(new Vector3f(0.08f, 0.0f, 0.12f), 1.8f);
    private static final DustParticleOptions DUST_PILLAR = new DustParticleOptions(new Vector3f(0.20f, 0.0f, 0.30f), 1.2f);
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                for (ServerPlayer player : level.players()) {

                    if (player.getTags().contains("sculk_cultist_ritual_pending")) {
                        player.removeTag("sculk_cultist_ritual_pending");
                        spawnAndStartRitual(player, level);
                    }

                    if (player.getTags().contains("sculk_zombie_summon")) {
                        player.removeTag("sculk_zombie_summon");
                        double x = player.getX() + 1;
                        double y = player.getY();
                        double z = player.getZ() + 1;
                        triggerZombieSummon(level, player, x, y, z);
                    }
                }
            }

            // Tick scheduled tasks
            if (!taskTicks.isEmpty()) {
                Iterator<long[]>   tickIter = taskTicks.iterator();
                Iterator<Runnable> runIter  = taskRunnables.iterator();
                List<Runnable>     toRun    = new ArrayList<>();

                while (tickIter.hasNext()) {
                    long[]   remaining = tickIter.next();
                    Runnable task      = runIter.next();
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
        if (delayTicks <= 0) { task.run(); return; }
        taskTicks.add(new long[]{ delayTicks });
        taskRunnables.add(task);
    }

    /*
     *Sculk Zombie summon
    */

    private static void triggerZombieSummon(ServerLevel level, ServerPlayer player, double x, double y, double z) {
        // Expanding then contracting dark circle over 60 ticks, zombie spawns at the end
        scheduleTask(0, new Runnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick > 60) {
                    SculkZombieEntity zombie = new SculkZombieEntity(ModEntities.SCULK_ZOMBIE, level);
                    zombie.setSummoner(player);
                    zombie.moveTo(x, y, z, 0, 0);
                    level.addFreshEntity(zombie);

                    for (int i = 0; i < 30; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                                x + Math.cos(angle) * 0.5, y + 0.5, z + Math.sin(angle) * 0.5,
                                1, 0, 0.1, 0, 0.05);
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
                        level.sendParticles(ModParticles.emissive(ModParticles.SCULK_CHARGE_POP, 0.6f, 0.0f, 1.0f, 0.12f, new Random().nextInt(4,12)),
                                px, y + 0.05, pz, 1, 0, 0, 0, 0);
                    }
                }

                // Rising smoke after initial buildup
                if (tick > 20) {
                    for (int i = 0; i < 3; i++) {
                        level.sendParticles(ParticleTypes.LARGE_SMOKE,
                                x + (Math.random() - 0.5) * radius,
                                y + (tick - 20) / 40.0 * 2,
                                z + (Math.random() - 0.5) * radius,
                                1, 0, 0.02, 0, 0.01);
                    }
                }

                tick++;
                scheduleTask(1, this);
            }
        });
    }

    /*
     *Sculk servant summon
     */
    private static void setScale(Warden warden, float scale) {
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(warden);
        scaleData.setScale(scale);
    }

    private static void spawnAndStartRitual(ServerPlayer player, ServerLevel level) {
        float  yaw     = player.getYRot();
        double radians = Math.toRadians(yaw);

        double x = player.getX() - Math.sin(radians) * SPAWN_BEHIND_DISTANCE;
        double z = player.getZ() + Math.cos(radians) * SPAWN_BEHIND_DISTANCE;
        double y = player.getY();

        SculkServantEntity warden = new SculkServantEntity(ModEntities.SCULK_SERVANT, level);
        warden.moveTo(x, y, z, yaw, 0f);
        warden.setYRot(yaw);
        warden.setYBodyRot(yaw);
        warden.setXRot(player.getXRot());
        warden.setYHeadRot(yaw);
        warden.setInvisible(true);
        warden.setInvulnerable(true);
        warden.setNoAi(true);
        warden.setPersistenceRequired();

        level.addFreshEntity(warden);
        warden.addTag("sculk_ritual_active");
        warden.addTag("summoner_" + player.getStringUUID());
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 3600L);
        setScale(warden, 0.1f);

        scheduleTask(1, () -> {
            if (warden.isRemoved()) return;
            warden.setInvisible(false);
            level.playSound(null, x, y, z, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.MASTER, 2f, 0.5f);
            triggerRitualCutscene(warden, new Vec3(x, y, z), level, player);
        });
    }

    private static void triggerRitualCutscene(Warden warden, Vec3 pos, ServerLevel level, ServerPlayer player) {

        // Darkness pulse
        level.getPlayers(p -> p.distanceToSqr(pos.x, pos.y, pos.z) < 400)
                .forEach(p -> p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 160, 1, false, false)));

        // Ambient swirling particles pulled toward the warden
        scheduleTask(0, new Runnable() {
            int life = 120;

            @Override
            public void run() {
                if (life-- <= 0 || warden.isRemoved()) return;

                for (int i = 0; i < 3; i++) {
                    double radius  = 6.0;
                    double offsetX = (Math.random() - 0.5) * radius * 2;
                    double offsetZ = (Math.random() - 0.5) * radius * 2;
                    double offsetY = Math.random() * 3;

                    double px = pos.x + offsetX;
                    double py = pos.y + offsetY;
                    double pz = pos.z + offsetZ;

                    double dx = (pos.x - px) * 0.03;
                    double dz = (pos.z - pz) * 0.03;

                    EmissiveParticleOptions dust = (life % 2 == 0) ? SCULK_1 : SCULK_2;
                    level.sendParticles(dust, px, py, pz, 1, dx, 0.01, dz, 0);

                    if (i == 0) {
                        level.sendParticles(ParticleTypes.SMOKE, px, py, pz, 1, dx * 0.5, 0.01, dz * 0.5, 0.005);
                    }
                }

                scheduleTask(1, this);
            }
        });

        // Rise and grow loop
        for (int i = 0; i <= 80; i++) {
            final int    tick  = i;
            final double riseY = pos.y + (tick / 80.0);
            final float  scale = 0.1f + (tick / 80.0f) * 1.499f;

            scheduleTask(tick, () -> {
                if (warden.isRemoved()) return;

                warden.setPos(warden.getX(), riseY, warden.getZ());
                warden.setYRot(player.getYRot());
                warden.setYBodyRot(player.getYRot());
                warden.setXRot(player.getXRot());
                warden.setYHeadRot(player.getYRot());
                setScale(warden, scale);

                if (tick % 3 == 0) {
                    level.sendParticles(DUST_DARK_PURPLE, warden.getX(), warden.getY() + 0.5, warden.getZ(), 3, 0.3, 0.15, 0.3, 0.02);
                    level.sendParticles(DUST_DEEP_VIOLET, warden.getX(), warden.getY(),        warden.getZ(), 2, 0.4, 0.1,  0.4, 0.03);
                    level.sendParticles(ParticleTypes.LARGE_SMOKE, warden.getX(), warden.getY(), warden.getZ(), 1, 0.3, 0.0, 0.3, 0.01);
                }

                if (tick % 5 == 0) {
                    level.sendParticles(ParticleTypes.REVERSE_PORTAL, warden.getX(), warden.getY() + 0.2, warden.getZ(), 4, 0.5, 0.3, 0.5, 0.06);
                }
            });
        }

        // Shadow pillars
        for (int p = 0; p < 4; p++) {
            double angle = p * (Math.PI / 2.0);
            double px    = pos.x + Math.cos(angle) * 4.5;
            double pz    = pos.z + Math.sin(angle) * 4.5;

            for (int h = 0; h < 20; h++) {
                final double height = h;
                scheduleTask(30 + (h * 2), () -> {
                    level.sendParticles(DUST_PILLAR, px, pos.y + height * 0.45, pz, 2, 0.06, 0.0, 0.06, 0.01);
                    level.sendParticles(ParticleTypes.SMOKE, px, pos.y + height * 0.45, pz, 1, 0.03, 0.02, 0.03, 0.005);
                });
            }
        }

        // Witch sparks at pillar bases
        scheduleTask(30, () -> {
            for (int p = 0; p < 4; p++) {
                double angle = p * (Math.PI / 2.0);
                level.sendParticles(ParticleTypes.WITCH,
                        pos.x + Math.cos(angle) * 4.5, pos.y + 0.5, pos.z + Math.sin(angle) * 4.5,
                        12, 0.2, 0.3, 0.2, 0.05);
            }
        });

        scheduleTask(40, () -> level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2f, 0.5f));
        scheduleTask(70, () -> level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WARDEN_ROAR,      SoundSource.HOSTILE, 2.5f, 0.8f));

        // Awaken
        scheduleTask(105, () -> {
            if (warden.isRemoved()) return;

            setScale(warden, 1.5f);
            warden.setHealth(600.0f);
            warden.setInvulnerable(false);
            warden.setNoAi(false);
            warden.removeTag("sculk_ritual_active");

            for (int i = 0; i < 3; i++) {
                level.sendParticles(DUST_BLACK_PURPLE, warden.getX(), warden.getY() + 1.0, warden.getZ(), 20, 1.0, 0.5, 1.0, 0.15);
                level.sendParticles(DUST_DARK_PURPLE,  warden.getX(), warden.getY() + 1.0, warden.getZ(), 15, 0.8, 0.4, 0.8, 0.2);
            }

            level.sendParticles(ParticleTypes.LARGE_SMOKE,     warden.getX(), warden.getY() + 1, warden.getZ(), 20, 0.8, 0.5, 0.8, 0.05);
            level.sendParticles(ParticleTypes.WITCH,           warden.getX(), warden.getY() + 1, warden.getZ(), 30, 1.0, 0.5, 1.0, 0.1);
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, warden.getX(), warden.getY() + 1, warden.getZ(), 1, 0, 0, 0, 0);

            level.playSound(null, warden.getX(), warden.getY(), warden.getZ(), SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 3f, 1f);

            level.getPlayers(p -> p.distanceToSqr(warden.getX(), warden.getY(), warden.getZ()) < 400)
                    .forEach(p -> p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 1, false, false)));
        });
    }
}