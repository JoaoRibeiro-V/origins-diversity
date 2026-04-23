package com.origins_diversity.Events;

import io.github.apace100.origins.integration.OriginDataLoadedCallback;
import io.github.apace100.origins.power.type.OriginsActionOnCallbackPowerType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;

public class HellhoundEvents {
    public static void register(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            if (!player.getTags().contains("hellhound_nether_spawn")) return;

            player.removeTag("hellhound_nether_spawn");
            teleportToSafeNether(player, server);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (!player.getTags().contains("hellhound_nether_spawn")) continue;
                player.removeTag("hellhound_nether_spawn");
                teleportToSafeNether(player, server);
            }
        });
    }

    private static void teleportToSafeNether(ServerPlayer player, MinecraftServer server) {
        ServerLevel nether = server.getLevel(Level.NETHER);
        if (nether == null) return;

        BlockPos safePos = findSafeNetherSpawn(nether, new BlockPos(0, 64, 0));
        if (safePos == null) return;

        player.teleportTo(nether, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5,
                player.getYRot(), player.getXRot());
    }

    private static BlockPos findSafeNetherSpawn(ServerLevel nether, BlockPos near) {
        int searchRadius = 16;

        for (int attempts = 0; attempts < 32; attempts++) {
            int x = near.getX() + nether.random.nextIntBetweenInclusive(-searchRadius, searchRadius);
            int z = near.getZ() + nether.random.nextIntBetweenInclusive(-searchRadius, searchRadius);

            for (int y = 100; y > 32; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockPos below = pos.below();

                boolean floorSolid = nether.getBlockState(below).isSolid();
                boolean spaceEmpty = nether.isEmptyBlock(pos) && nether.isEmptyBlock(pos.above());
                boolean noLava = !nether.getFluidState(pos).is(FluidTags.LAVA)
                        && !nether.getFluidState(pos.above()).is(FluidTags.LAVA)
                        && !nether.getFluidState(below).is(FluidTags.LAVA);

                if (floorSolid && spaceEmpty && noLava) return pos;
            }
        }

        return null;
    }
}
