package com.origins_diversity.Entities;

import com.origins_diversity.Data.SculkServantTameData;
import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.UUID;

public class SculkZombieEntity extends Zombie {

    private UUID summonerUUID;

    public void setSummoner(Player player) {
        this.summonerUUID = player.getUUID();
    }

    public UUID getSummonerUUID() {
        return summonerUUID;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (summonerUUID != null) tag.putUUID("SummonerUUID", summonerUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("SummonerUUID")) summonerUUID = tag.getUUID("SummonerUUID");
    }

    public SculkZombieEntity(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(25.0);
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(6.0);
        this.setHealth(25.0f);

    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof Player player && OriginsUtil.hasOrigin(player, "origins-diversity", "sculk_cultist")) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    @Override
    protected int getBaseExperienceReward() {
        return super.getBaseExperienceReward() * 3;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0)); // 3 seconds
        }
        return result;
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }
}
