package com.origins_diversity.Events;

import com.origins_diversity.Block.ModBlocks;
import com.origins_diversity.Data.SculkServantInventoryData;
import com.origins_diversity.Data.SculkServantTameData;
import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.Entities.SculkZombieEntity;
import com.origins_diversity.Extra.OriginsUtil;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.UUID;


public class ModEntityEvents {

    public static void register() {

        KitsuneEvents.register();
        StarbornEvents.register();
        ParasiteEvents.register();
        HellhoundEvents.register();


        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {

            if (!(world instanceof ServerWorld level)) {
                return ActionResult.PASS;
            }


            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }


            if (player.isSneaking()) {
                return ActionResult.PASS;
            }


            if (!OriginsUtil.hasOrigin(
                    serverPlayer,
                    "origins-diversity",
                    "sculk_cultist"
            )) {
                return ActionResult.PASS;
            }


            if (serverPlayer.getCommandTags().contains("sculk_just_converted")) {

                serverPlayer.removeScoreboardTag("sculk_just_converted");

                return ActionResult.PASS;
            }


            BlockState blockState =
                    world.getBlockState(blockHitResult.getBlockPos());


            if (!(blockState.isOf(Blocks.SCULK)
                    || blockState.isOf(ModBlocks.SCULK_BLOCK))) {

                return ActionResult.PASS;
            }


            SculkServantInventoryData data =
                    SculkServantInventoryData.get(
                            serverPlayer.getServer()
                    );


            SimpleInventory inventory =
                    data.getInventory(
                            serverPlayer.getUuid()
                    );


            serverPlayer.openHandledScreen(
                    new SimpleNamedScreenHandlerFactory(
                            (syncId, playerInventory, playerEntity) ->
                                    GenericContainerScreenHandler.createGeneric9x3(
                                            syncId,
                                            playerInventory,
                                            inventory
                                    ),
                            Text.literal("Sculk Storage")
                    )
            );


            return ActionResult.SUCCESS;
        });



        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {

            if (!(entity instanceof SculkServantEntity servant)) {
                return;
            }


            if (!(entity.getWorld() instanceof ServerWorld serverWorld)) {
                return;
            }


            if (!(source.getAttacker() instanceof ServerPlayerEntity killer)) {
                return;
            }


            String summonerTag =
                    "summoner_" + killer.getUuidAsString();


            if (servant.getCommandTags().contains(summonerTag)) {

                SculkServantTameData.get(
                        serverWorld.getServer()
                ).markTamed(
                        killer.getUuid(),
                        serverWorld.getServer()
                );
            }
        });
    }
}