package com.origins_diversity.PowerHandlers;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.Random;

public class ChorusTeleportListener {

    private static final Random RANDOM = new Random();


    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for(ServerWorld world : server.getWorlds()) {

                for(ServerPlayerEntity player : world.getPlayers()) {


                    if(player.getCommandTags().contains("chorus_random_teleport")) {

                        player.removeScoreboardTag("chorus_random_teleport");

                        randomTeleport(player, world);
                    }
                }
            }

        });
    }



    private static void randomTeleport(
            ServerPlayerEntity player,
            ServerWorld world
    ) {


        int radius = 12;


        int x = player.getBlockX()
                + RANDOM.nextInt(radius * 2 + 1)
                - radius;


        int z = player.getBlockZ()
                + RANDOM.nextInt(radius * 2 + 1)
                - radius;


        /*
         * Same idea as heightmap="world_surface"
         */
        int y = world.getTopY(
                Heightmap.Type.WORLD_SURFACE,
                x,
                z
        );


        BlockPos pos = new BlockPos(x, y, z);


        // avoid teleporting inside blocks
        while(!world.getBlockState(pos).isAir()
                && y < world.getBottomY() + world.getHeight()) {

            pos = pos.up();
            y++;
        }


        player.teleport(
                world,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
        );
    }
}