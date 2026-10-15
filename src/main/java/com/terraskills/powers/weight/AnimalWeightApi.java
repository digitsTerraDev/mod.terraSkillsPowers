package com.terraskills.powers.weight;

import com.terraskills.powers.config.PowersConfig;

/**
 * Public, rank-agnostic animal hauling hooks for modpack integrations.
 * Callers supply their own power rank, so this API is not tied to Pufferfish node IDs.
 */
public final class AnimalWeightApi {
    private AnimalWeightApi() {}

    public static double horseLightHarnessSpeedStart(double baseLoadRatio, int rank) {
        return baseLoadRatio + PowersConfig.HORSE_LIGHT_HARNESS_SPEED_START_PER_RANK.get() * Math.max(0, rank);
    }

    public static double horseLightHarnessJumpLimit(double baseLoadRatio, int rank) {
        return baseLoadRatio + PowersConfig.HORSE_LIGHT_HARNESS_JUMP_LIMIT_PER_RANK.get() * Math.max(0, rank);
    }

    public static double horseLightHarnessStepLimit(double baseLoadRatio, int rank) {
        return baseLoadRatio + PowersConfig.HORSE_LIGHT_HARNESS_STEP_LIMIT_PER_RANK.get() * Math.max(0, rank);
    }

    /** Applies Draft Harness as a pulling-capacity increase for a donkey. */
    public static double donkeyDraftHarnessPullCapacity(double baseCapacityKg, int rank) {
        return baseCapacityKg * (1.0D + PowersConfig.DONKEY_DRAFT_HARNESS_PULL_PERCENT_PER_RANK.get() * Math.max(0, rank));
    }

    /** Applies Expedition Balance to rider, armor, or an animal-cart passenger weight. */
    public static double muleExpeditionBalanceWeight(double weightKg, int rank) {
        double reduction = PowersConfig.MULE_EXPEDITION_BALANCE_REDUCTION_PER_RANK.get() * Math.max(0, rank);
        return weightKg * Math.max(0.0D, 1.0D - reduction);
    }

    public static double horseArmorEffectiveWeight(double armorWeightKg, int rank) {
        double reduction = PowersConfig.HORSE_ARMOR_WEIGHT_REDUCTION_PER_RANK.get() * Math.max(0, rank);
        return armorWeightKg * Math.max(0.0D, 1.0D - reduction);
    }

    public static double horseArmorEffectivenessMultiplier(int rank) {
        return 1.0D + PowersConfig.HORSE_ARMOR_EFFECTIVENESS_PER_RANK.get() * Math.max(0, rank);
    }

    public static double mountSpeedMultiplier(int rank) { return 1.0D + PowersConfig.MOUNT_SPEED_PERCENT_PER_RANK.get() * Math.max(0, rank); }
    public static double horseSpeedMultiplier(int rank) { return 1.0D + PowersConfig.HORSE_SPEED_PERCENT_PER_RANK.get() * Math.max(0, rank); }
    public static double muleSpeedMultiplier(int rank) { return 1.0D + PowersConfig.MULE_SPEED_PERCENT_PER_RANK.get() * Math.max(0, rank); }
    public static double mountArmorBonus(int rank) { return PowersConfig.MOUNT_ARMOR_PER_RANK.get() * Math.max(0, rank); }
    public static double horseArmorBonus(int rank) { return PowersConfig.HORSE_ARMOR_PER_RANK.get() * Math.max(0, rank); }
    public static double muleArmorBonus(int rank) { return PowersConfig.MULE_ARMOR_PER_RANK.get() * Math.max(0, rank); }
    public static double mountToughnessBonus(int rank) { return PowersConfig.MOUNT_TOUGHNESS_PER_RANK.get() * Math.max(0, rank); }
    public static double horseToughnessBonus(int rank) { return PowersConfig.HORSE_TOUGHNESS_PER_RANK.get() * Math.max(0, rank); }
    public static double muleToughnessBonus(int rank) { return PowersConfig.MULE_TOUGHNESS_PER_RANK.get() * Math.max(0, rank); }
}
