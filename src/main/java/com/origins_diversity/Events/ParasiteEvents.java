    package com.origins_diversity.Events;

    import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
    import net.fabricmc.fabric.api.gamerule.v1.FabricGameRuleVisitor;
    import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
    import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
    import net.minecraft.world.level.GameRules;

    import static com.origins_diversity.GameRules.ModGameRules.PREVENT_MOUNT_DAMAGE;

    public class ParasiteEvents {
        public static void register() {
            ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
                if (source.getEntity() == null) return true;
                if (!entity.level().getGameRules().getRule(PREVENT_MOUNT_DAMAGE).get()) return true;

                if (source.getEntity() == entity.getVehicle()) return false;
                if (entity == source.getEntity().getVehicle()) return false;

                return true;
            });
        }
    }
