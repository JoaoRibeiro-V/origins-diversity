package com.origins_diversity.Entities;

import com.origins_diversity.Extra.OriginsUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

public class SculkZombieEntity extends ZombieEntity {

    private UUID summonerUUID;

    public void setSummoner(PlayerEntity player) {
        this.summonerUUID = player.getUuid();
    }

    public UUID getSummonerUUID() {
        return summonerUUID;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);

        if (summonerUUID != null) {
            tag.putUuid("SummonerUUID", summonerUUID);
        }
    }


    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);

        if (tag.containsUuid("SummonerUUID")) {
            summonerUUID = tag.getUuid("SummonerUUID");
        }
    }

    public static DefaultAttributeContainer.Builder createSculkZombieAttributes() {
        return ZombieEntity.createZombieAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 6.0D);
    }
    public SculkZombieEntity(EntityType<? extends ZombieEntity> entityType, World level) {
        super(entityType, level);

    }

    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D));

        this.targetSelector.add(1,
                (new RevengeGoal(this)).setGroupRevenge(ZombifiedPiglinEntity.class));

        // Your custom player target goal
        this.targetSelector.add(2, new ZombiePlayerTargetGoal(this));

        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, TurtleEntity.class, 10, true, false,
                TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    @Override
    public int getXpToDrop() {
        return super.getXpToDrop() * 3;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean result = super.tryAttack(target);
        if (result && target instanceof LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 80, 0)); // 3 seconds
        }
        return result;
    }

    @Override
    protected boolean burnsInDaylight() {
        return false;
    }

    public class ZombiePlayerTargetGoal extends ActiveTargetGoal<PlayerEntity> {

        public ZombiePlayerTargetGoal(MobEntity mob) {
            super(mob, PlayerEntity.class, true);
        }

        @Override
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            }

            if (this.targetEntity instanceof PlayerEntity player &&
                    OriginsUtil.hasOrigin(player, "origins-diversity", "sculk_cultist")) {
                return false;
            }

            return true;
        }
    }
}
