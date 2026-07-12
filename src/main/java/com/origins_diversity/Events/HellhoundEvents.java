package com.origins_diversity.Events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.World;

public class HellhoundEvents {
    public static void register(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            if (!player.getCommandTags().contains("hellhound_nether_spawn")) return;

            player.removeScoreboardTag("hellhound_nether_spawn");
            teleportToSafeNether(player, server);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!player.getCommandTags().contains("hellhound_nether_spawn")) continue;
                player.removeScoreboardTag("hellhound_nether_spawn");
                teleportToSafeNether(player, server);
            }
        });
    }

    private static void teleportToSafeNether(ServerPlayerEntity player, MinecraftServer server) {
        ServerWorld nether = server.getWorld(World.NETHER);
        if (nether == null) return;

        BlockPos safePos = findSafeNetherSpawn(nether, new BlockPos(0, 64, 0));
        if (safePos == null) return;

        player.teleport(nether, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5,
                player.getYaw(), player.getPitch());
    }

    private static BlockPos findSafeNetherSpawn(ServerWorld nether, BlockPos near) {
        int searchRadius = 16;

        for (int attempts = 0; attempts < 32; attempts++) {
            int x = near.getX() + nether.random.nextBetween(-searchRadius, searchRadius);
            int z = near.getZ() + nether.random.nextBetween(-searchRadius, searchRadius);

            for (int y = 100; y > 32; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockPos below = pos.down();

                boolean floorSolid = !nether.getBlockState(below).isAir();
                boolean spaceEmpty = nether.isAir(pos) && nether.isAir(pos.up());
                boolean noLava = !nether.getFluidState(pos).isIn(FluidTags.LAVA)
                        && !nether.getFluidState(pos.up()).isIn(FluidTags.LAVA)
                        && !nether.getFluidState(below).isIn(FluidTags.LAVA);

                if (floorSolid && spaceEmpty && noLava) return pos;
            }
        }

        return null;
    }
}
