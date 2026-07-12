package com.origins_diversity.PowerHandlers;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.BlockPos;

import java.util.Random;


public class AbysswyrmShiftListener {

    private static final Random RANDOM = new Random();


    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for(ServerWorld world : server.getWorlds()) {

                for(ServerPlayerEntity player : world.getPlayers()) {


                    if(player.getCommandTags().contains("abysswyrm_shadow_shift")) {

                        player.removeScoreboardTag(
                                "abysswyrm_shadow_shift"
                        );

                        shadowShift(player, world);
                    }
                }
            }

        });
    }



    private static void shadowShift(
            ServerPlayerEntity player,
            ServerWorld world
    ) {


        double oldX = player.getX();
        double oldY = player.getY();
        double oldZ = player.getZ();



        /*
         * area_width 2
         * Means roughly +/- 1 block
         */
        int x =
                player.getBlockX()
                        + RANDOM.nextInt(3)
                        - 1;


        int z =
                player.getBlockZ()
                        + RANDOM.nextInt(3)
                        - 1;



        int y = world.getTopY(
                Heightmap.Type.WORLD_SURFACE,
                x,
                z
        );



        BlockPos pos = new BlockPos(x,y,z);



        // find safe air
        while(!world.getBlockState(pos).isAir()) {
            pos = pos.up();
        }



        /*
         * disappear effect
         */
        world.spawnParticles(
                ParticleTypes.POOF,
                oldX,
                oldY + 0.5,
                oldZ,
                8,
                0.3,
                0.5,
                0.3,
                0.05
        );



        player.teleport(
                world,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
        );



        /*
         * reappear effect
         */
        world.spawnParticles(
                ParticleTypes.POOF,
                player.getX(),
                player.getY() + 0.5,
                player.getZ(),
                8,
                0.3,
                0.5,
                0.3,
                0.05
        );


        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS,
                1,
                0.8f
        );



        player.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.NAUSEA,
                        200,
                        0,
                        false,
                        false,
                        false
                )
        );
    }
}