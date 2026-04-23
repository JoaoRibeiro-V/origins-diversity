package com.origins_diversity.Events;

import com.origins_diversity.Block.ModBlocks;
import com.origins_diversity.Data.SculkServantInventoryData;
import com.origins_diversity.Entities.SculkServantEntity;
import com.origins_diversity.Data.SculkServantTameData;
import com.origins_diversity.Entities.SculkZombieEntity;
import com.origins_diversity.Extra.OriginsUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class ModEntityEvents {
    public static void register() {
        KitsuneEvents.register();
        StarbornEvents.register();
        ParasiteEvents.register();
        HellhoundEvents.register();
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (!(world instanceof ServerLevel level)) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
            if (player.isCrouching()) return InteractionResult.PASS;
            if (!OriginsUtil.hasOrigin(serverPlayer, "origins-diversity", "sculk_cultist")) return InteractionResult.PASS;

            // If a conversion just happened on this click, skip storage and clear the tag
            if (serverPlayer.getTags().contains("sculk_just_converted")) {
                serverPlayer.removeTag("sculk_just_converted");
                return InteractionResult.PASS;
            }
            BlockState blockState = world.getBlockState(blockHitResult.getBlockPos());
            if (!(blockState.is(Blocks.SCULK) || blockState.is(ModBlocks.SCULK_BLOCK))) return InteractionResult.PASS;

            SculkServantInventoryData data = SculkServantInventoryData.get(serverPlayer.server);
            SimpleContainer container = data.getInventory(serverPlayer.getUUID());

            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> ChestMenu.threeRows(id, inv, container),
                    Component.literal("Sculk Storage")
            ));

            return InteractionResult.SUCCESS;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(entity instanceof SculkServantEntity servant)) return;
            if (!(entity.level() instanceof ServerLevel serverLevel)) return;
            if (!(source.getEntity() instanceof ServerPlayer killer)) return;
            String summonerTag = "summoner_" + killer.getStringUUID();
            if (servant.getTags().contains(summonerTag)) {
                SculkServantTameData.get(serverLevel.getServer()).markTamed(killer.getUUID(), serverLevel.getServer());
            }
        });
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damage, absorbed) -> {
            if (!(entity.level() instanceof ServerLevel serverLevel)) return;
            if (source.getEntity() == null) return;
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            // find nearby sculk zombies whose summoner is this entity
            serverLevel.getEntitiesOfClass(SculkZombieEntity.class,
                    entity.getBoundingBox().inflate(32),
                    zombie -> {
                        UUID sid = zombie.getSummonerUUID();
                        return sid != null && sid.equals(entity.getUUID());
                    }
            ).forEach(zombie -> zombie.setTarget(attacker));
        });
    }
}