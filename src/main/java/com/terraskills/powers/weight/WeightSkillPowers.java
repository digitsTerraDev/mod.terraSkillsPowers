package com.terraskills.powers.weight;

import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.player.Player;

/** Applies the configurable Pufferfish skill bonuses used by Ultimate Weight and horse hauling. */
public final class WeightSkillPowers {
    private WeightSkillPowers() {}

    public static double carryCapacityKg(Player player, double baseCapacityKg) {
        if (!(player instanceof ServerPlayer server)) return baseCapacityKg;
        double percent = PowersConfig.CARRY_CAPACITY_PERCENT_PER_RANK.get() * SkillRanks.carryCapacityPercent(server);
        double flatKg = PowersConfig.CARRY_CAPACITY_FLAT_KG_PER_RANK.get() * SkillRanks.carryCapacityFlat(server);
        return Math.max(0.0D, baseCapacityKg * (1.0D + percent) + flatKg);
    }

    public static double horsePullCapacityKg(AbstractHorse horse, Player rider, double baseCapacityKg) {
        double capacityKg = baseCapacityKg * pullMultiplier(horse);
        if (!(rider instanceof ServerPlayer server)) return Math.max(0.0D, capacityKg);

        double ridingPercent = PowersConfig.RIDING_PULL_PERCENT_PER_RANK.get() * SkillRanks.ridingPullPercent(server);
        double ridingFlatKg = PowersConfig.RIDING_PULL_FLAT_KG_PER_RANK.get() * SkillRanks.ridingPullFlat(server);
        capacityKg = capacityKg * (1.0D + ridingPercent) + ridingFlatKg;
        if (horse instanceof Donkey) {
            capacityKg = AnimalWeightApi.donkeyDraftHarnessPullCapacity(capacityKg, SkillRanks.donkeyDraftHarness(server));
        }
        if (isPackAnimal(horse)) {
            double packPercent = PowersConfig.PACK_ANIMAL_PULL_PERCENT_PER_RANK.get() * SkillRanks.packAnimalPullPercent(server);
            double packFlatKg = PowersConfig.PACK_ANIMAL_PULL_FLAT_KG_PER_RANK.get() * SkillRanks.packAnimalPullFlat(server);
            capacityKg = capacityKg * (1.0D + packPercent) + packFlatKg;
        }
        return Math.max(0.0D, capacityKg);
    }

    public static double horseBackCapacityKg(AbstractHorse horse, Player rider, double baseCapacityKg) {
        double capacityKg = carryCapacityKg(rider, baseCapacityKg);
        if (!isPackAnimal(horse)) return capacityKg;

        capacityKg *= backMultiplier(horse);
        if (rider instanceof ServerPlayer server) {
            double percent = PowersConfig.PACK_ANIMAL_BACK_PERCENT_PER_RANK.get() * SkillRanks.packAnimalBackPercent(server);
            double flatKg = PowersConfig.PACK_ANIMAL_BACK_FLAT_KG_PER_RANK.get() * SkillRanks.packAnimalBackFlat(server);
            capacityKg = capacityKg * (1.0D + percent) + flatKg;
        }
        return Math.max(0.0D, capacityKg);
    }

    public static double jumpLoadLimit(AbstractHorse horse, Player rider, double baseLimit) {
        if (!(rider instanceof ServerPlayer server)) return baseLimit;
        if (isPackAnimal(horse)) {
            return baseLimit * (1.0D + PowersConfig.PACK_ANIMAL_JUMP_LIMIT_PERCENT_PER_RANK.get() * SkillRanks.packAnimalJumpLimit(server));
        }
        return AnimalWeightApi.horseLightHarnessJumpLimit(baseLimit, SkillRanks.horseLightHarness(server));
    }

    public static double stepLoadLimit(AbstractHorse horse, Player rider, double baseLimit) {
        if (!(rider instanceof ServerPlayer server)) return baseLimit;
        if (isPackAnimal(horse)) {
            return baseLimit * (1.0D + PowersConfig.PACK_ANIMAL_STEP_LIMIT_PERCENT_PER_RANK.get() * SkillRanks.packAnimalStepLimit(server));
        }
        return AnimalWeightApi.horseLightHarnessStepLimit(baseLimit, SkillRanks.horseLightHarness(server));
    }

    public static double speedMultiplier(AbstractHorse horse, Player rider, double loadRatio) {
        double curveStart = horse instanceof Donkey ? 0.60D : horse instanceof Mule ? 0.45D : 0.30D;
        if (!(horse instanceof Donkey) && !(horse instanceof Mule) && rider instanceof ServerPlayer server) {
            curveStart = AnimalWeightApi.horseLightHarnessSpeedStart(curveStart, SkillRanks.horseLightHarness(server));
        }
        if (loadRatio <= curveStart) return 1.0D;
        double maxPenalty = horse instanceof Mule ? 0.40D : 0.50D;
        if (rider instanceof ServerPlayer server) {
            maxPenalty = Math.max(0.0D, maxPenalty - PowersConfig.RIDING_SPEED_PENALTY_REDUCTION_PER_RANK.get()
                    * SkillRanks.ridingSpeedPenalty(server));
        }
        double progress = Math.clamp((Math.min(loadRatio, 1.0D) - curveStart) / (1.0D - curveStart), 0.0D, 1.0D);
        return Math.max(0.0D, 1.0D - maxPenalty * progress);
    }

    public static double effectiveMuleWeightKg(AbstractHorse horse, Player rider, double weightKg) {
        if (horse instanceof Mule && rider instanceof ServerPlayer server) {
            return AnimalWeightApi.muleExpeditionBalanceWeight(weightKg, SkillRanks.muleExpeditionBalance(server));
        }
        return weightKg;
    }

    private static boolean isPackAnimal(AbstractHorse horse) {
        return horse instanceof Donkey || horse instanceof Mule;
    }

    private static double pullMultiplier(AbstractHorse horse) {
        if (horse instanceof Donkey) return PowersConfig.DONKEY_BASE_PULL_MULTIPLIER.get();
        if (horse instanceof Mule) return PowersConfig.MULE_BASE_PULL_MULTIPLIER.get();
        return 1.0D;
    }

    private static double backMultiplier(AbstractHorse horse) {
        if (horse instanceof Donkey) return PowersConfig.DONKEY_BACK_CAPACITY_MULTIPLIER.get();
        if (horse instanceof Mule) return PowersConfig.MULE_BACK_CAPACITY_MULTIPLIER.get();
        return 1.0D;
    }
}
