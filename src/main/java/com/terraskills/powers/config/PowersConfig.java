package com.terraskills.powers.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/** Server-owned balance knobs; datapacks remain responsible for skill definitions and prerequisites. */
public final class PowersConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CAREFUL_EYE_GEMS;
    public static final ModConfigSpec.DoubleValue CAREFUL_EYE_DISCOVERY_CHANCE_PER_RANK;
    public static final ModConfigSpec.IntValue HAZARD_SHORE_RADIUS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> HAZARD_SHORES;
    public static final ModConfigSpec.DoubleValue HAZARD_CHANCE_PER_RANK;
    public static final ModConfigSpec.BooleanValue HAZARD_ALLOW_AUTOMATED_PLACEMENT;
    public static final ModConfigSpec.DoubleValue TOOL_CONSERVATION_CHANCE_PER_RANK;
    public static final ModConfigSpec.DoubleValue CAREFUL_MINING_COLLAPSE_REDUCTION_PER_RANK;
    public static final ModConfigSpec.DoubleValue MINING_SPEED_BONUS_PER_RANK;
    public static final ModConfigSpec.DoubleValue DEEP_MINING_SPEED_BONUS_PER_RANK;
    public static final ModConfigSpec.DoubleValue STONE_MINING_SPEED_BONUS_PER_RANK;
    public static final ModConfigSpec.DoubleValue EXCAVATING_SPEED_BONUS_PER_RANK;
    public static final ModConfigSpec.IntValue BRUTISH_MINING_REQUIRED_MINING_RANK;
    public static final ModConfigSpec.DoubleValue BRUTISH_MINING_SPEED_BONUS_PER_RANK;
    public static final ModConfigSpec.DoubleValue BRUTISH_MINING_WEAR_BASE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue BRUTISH_MINING_WEAR_MULTIPLIER_PER_RANK;
    public static final ModConfigSpec.DoubleValue BRUTISH_MINING_TOOL_CONSERVATION_CHANCE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue HEIGHTS_TRAINING_SAFE_FALL_BLOCKS_PER_RANK;
    public static final ModConfigSpec.DoubleValue CAREFUL_EXCAVATION_REACH_PER_RANK;
    public static final ModConfigSpec.IntValue SUPPORT_ASSESSMENT_RADIUS_AT_RANK_ONE;
    public static final ModConfigSpec.IntValue SUPPORT_ASSESSMENT_ADDITIONAL_RADIUS_PER_RANK;
    public static final ModConfigSpec.IntValue SUPPORT_ASSESSMENT_MAX_RADIUS;
    public static final ModConfigSpec.IntValue SUPPORT_ASSESSMENT_COOLDOWN_TICKS;
    public static final ModConfigSpec.DoubleValue CARRY_CAPACITY_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue CARRY_CAPACITY_FLAT_KG_PER_RANK;
    public static final ModConfigSpec.DoubleValue RIDING_PULL_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue RIDING_PULL_FLAT_KG_PER_RANK;
    public static final ModConfigSpec.DoubleValue RIDING_SPEED_PENALTY_REDUCTION_PER_RANK;
    public static final ModConfigSpec.DoubleValue HORSE_MIN_PULL_CAPACITY_KG;
    public static final ModConfigSpec.DoubleValue HORSE_MAX_PULL_CAPACITY_KG;
    public static final ModConfigSpec.DoubleValue DONKEY_MIN_PULL_CAPACITY_KG;
    public static final ModConfigSpec.DoubleValue DONKEY_MAX_PULL_CAPACITY_KG;
    public static final ModConfigSpec.DoubleValue MULE_MIN_PULL_CAPACITY_KG;
    public static final ModConfigSpec.DoubleValue MULE_MAX_PULL_CAPACITY_KG;
    public static final ModConfigSpec.DoubleValue HORSE_BACK_CAPACITY_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue DONKEY_BACK_CAPACITY_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue MULE_BACK_CAPACITY_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue HORSE_SPEED_PENALTY_START;
    public static final ModConfigSpec.DoubleValue DONKEY_SPEED_PENALTY_START;
    public static final ModConfigSpec.DoubleValue MULE_SPEED_PENALTY_START;
    public static final ModConfigSpec.DoubleValue HORSE_MIN_SPEED_AT_CAPACITY;
    public static final ModConfigSpec.DoubleValue DONKEY_MIN_SPEED_AT_CAPACITY;
    public static final ModConfigSpec.DoubleValue MULE_MIN_SPEED_AT_CAPACITY;
    public static final ModConfigSpec.DoubleValue HORSE_JUMP_LOAD_LIMIT;
    public static final ModConfigSpec.DoubleValue DONKEY_JUMP_LOAD_LIMIT;
    public static final ModConfigSpec.DoubleValue MULE_JUMP_LOAD_LIMIT;
    public static final ModConfigSpec.DoubleValue HORSE_STEP_LOAD_LIMIT;
    public static final ModConfigSpec.DoubleValue DONKEY_STEP_LOAD_LIMIT;
    public static final ModConfigSpec.DoubleValue MULE_STEP_LOAD_LIMIT;
    public static final ModConfigSpec.DoubleValue PULL_CAPACITY_MIN_JUMP_STRENGTH;
    public static final ModConfigSpec.DoubleValue PULL_CAPACITY_MAX_JUMP_STRENGTH;
    public static final ModConfigSpec.DoubleValue REDUCED_STEP_HEIGHT;
    public static final ModConfigSpec.DoubleValue HORSE_LIGHT_HARNESS_SPEED_START_PER_RANK;
    public static final ModConfigSpec.DoubleValue HORSE_LIGHT_HARNESS_JUMP_LIMIT_PER_RANK;
    public static final ModConfigSpec.DoubleValue HORSE_LIGHT_HARNESS_STEP_LIMIT_PER_RANK;
    public static final ModConfigSpec.DoubleValue DONKEY_DRAFT_HARNESS_PULL_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue MULE_EXPEDITION_BALANCE_REDUCTION_PER_RANK;
    public static final ModConfigSpec.DoubleValue HORSE_ARMOR_WEIGHT_REDUCTION_PER_RANK;
    public static final ModConfigSpec.DoubleValue HORSE_ARMOR_EFFECTIVENESS_PER_RANK;
    public static final ModConfigSpec.DoubleValue MOUNT_SPEED_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue HORSE_SPEED_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue MULE_SPEED_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue MOUNT_ARMOR_PER_RANK;
    public static final ModConfigSpec.DoubleValue HORSE_ARMOR_PER_RANK;
    public static final ModConfigSpec.DoubleValue MULE_ARMOR_PER_RANK;
    public static final ModConfigSpec.DoubleValue MOUNT_TOUGHNESS_PER_RANK;
    public static final ModConfigSpec.DoubleValue HORSE_TOUGHNESS_PER_RANK;
    public static final ModConfigSpec.DoubleValue MULE_TOUGHNESS_PER_RANK;
    public static final ModConfigSpec.DoubleValue PACK_ANIMAL_PULL_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue PACK_ANIMAL_PULL_FLAT_KG_PER_RANK;
    public static final ModConfigSpec.DoubleValue PACK_ANIMAL_BACK_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue PACK_ANIMAL_BACK_FLAT_KG_PER_RANK;
    public static final ModConfigSpec.DoubleValue PACK_ANIMAL_JUMP_LIMIT_PERCENT_PER_RANK;
    public static final ModConfigSpec.DoubleValue PACK_ANIMAL_STEP_LIMIT_PERCENT_PER_RANK;
    public static final ModConfigSpec.IntValue TRAIL_ACTIVITY_DECAY_TICKS;
    public static final ModConfigSpec.DoubleValue TRAIL_ACTIVITY_NORMALIZATION_LOW;
    public static final ModConfigSpec.DoubleValue TRAIL_ACTIVITY_NORMALIZATION_HIGH;
    public static final ModConfigSpec.IntValue TRAIL_ACTIVITY_SAMPLE_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue TRAIL_ACTIVITY_CONTRIBUTION_WINDOW_TICKS;
    public static final ModConfigSpec.DoubleValue TRAIL_ACTIVITY_CONTRIBUTION_CAP_BLOCKS;
    public static final ModConfigSpec.DoubleValue TRAIL_ACTIVITY_MAX_SAMPLE_DISTANCE;
    public static final ModConfigSpec.IntValue TRAIL_ACTIVITY_HEATMAP_MAX_RADIUS_CHUNKS;
    public static final ModConfigSpec.IntValue TRAIL_ACTIVITY_HEATMAP_MAX_ENTRIES;
    public static final ModConfigSpec.IntValue TRAIL_ACTIVITY_HEATMAP_REQUEST_COOLDOWN_TICKS;
    public static final ModConfigSpec.DoubleValue WATER_CONSERVATION_TRAVEL_THIRST_REDUCTION_PER_RANK;
    public static final ModConfigSpec.DoubleValue SCRAMBLING_UPWARD_SPEED_BONUS_PER_RANK;
    public static final ModConfigSpec.DoubleValue EXPEDITION_PACKING_WEIGHT_REDUCTION_PER_RANK;
    public static final ModConfigSpec.DoubleValue TRAIL_ACTIVITY_SPEED_BONUS_PER_RANK;
    public static final ModConfigSpec.DoubleValue ENDURANCE_SPRINT_EXHAUSTION_REDUCTION_PER_RANK;
    public static final ModConfigSpec.DoubleValue ACCLIMATIZATION_HARMFUL_EXPOSURE_REDUCTION_PER_RANK;
    public static final ModConfigSpec.DoubleValue QUIET_PASSAGE_DETECTION_REDUCTION_PER_RANK;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("carefulEye");
        CAREFUL_EYE_GEMS = builder.comment("Weighted TFC raw-rock gem associations: rock=gem@weight,gem@weight.",
                "Defaults mirror TFC 4.2.10 vein replacement rocks; invalid entries are ignored.")
                .defineListAllowEmpty("gemWeights", List.of(
                        "granite=emerald@1", "diorite=emerald@1", "gabbro=diamond@2,emerald@1",
                        "shale=amethyst@1,opal@1", "claystone=amethyst@1,opal@1", "limestone=amethyst@1,opal@1,lapis_lazuli@1",
                        "conglomerate=amethyst@1,opal@1", "dolomite=amethyst@1,opal@1", "chert=amethyst@1,opal@1",
                        "chalk=amethyst@1,opal@1", "tuff=amethyst@1,opal@1", "rhyolite=opal@1", "basalt=opal@1",
                        "andesite=opal@1", "dacite=opal@1", "quartzite=amethyst@1", "slate=amethyst@1",
                        "phyllite=amethyst@1", "schist=amethyst@1", "gneiss=amethyst@1", "marble=amethyst@1,lapis_lazuli@1,ruby@1"),
                        value -> value instanceof String text && text.contains("="));
        CAREFUL_EYE_DISCOVERY_CHANCE_PER_RANK = builder.comment("Careful Eye: chance per rank to discover one gem from an eligible raw rock (0.002 = 0.2%).")
                .defineInRange("discoveryChancePerRank", 0.002D, 0.0D, 1.0D);
        builder.pop();
        builder.push("hazardManagement");
        HAZARD_SHORE_RADIUS = builder.comment("Maximum block distance from a falling collapse block at which a shore can arrest its front.")
                .defineInRange("shoreRadius", 4, 1, 16);
        HAZARD_SHORES = builder.comment("Configured shore blocks: block_id=base_arrest_chance,required_hazard_management_rank.",
                "The blocks/items themselves are registered by the modpack, for example through KubeJS.",
                "The defaults expect KubeJS blocks named kubejs:copper_braced_shore and kubejs:black_steel_shore.")
                .defineListAllowEmpty("shores", List.of("kubejs:copper_braced_shore=0.10,1", "kubejs:black_steel_shore=0.45,4"),
                        value -> value instanceof String text && text.contains("=") && text.contains(","));
        HAZARD_CHANCE_PER_RANK = builder.comment("Arrest chance added per triggering miner's Hazard Management rank.")
                .defineInRange("chancePerRank", 0.10D, 0.0D, 1.0D);
        HAZARD_ALLOW_AUTOMATED_PLACEMENT = builder.comment("Allow shore placement with no player entity, for example dispensers or mod automation.")
                .define("allowAutomatedPlacement", false);
        builder.pop();
        builder.push("miningPowers");
        TOOL_CONSERVATION_CHANCE_PER_RANK = builder.comment("Tool Conservation: chance per rank to skip one eligible mining durability charge (0.05 = 5%).",
                        "Brutish Mining applies its separately configurable chance multiplier; the final value is capped at 100%.")
                .defineInRange("toolConservationChancePerRank", 0.05D, 0.0D, 1.0D);
        CAREFUL_MINING_COLLAPSE_REDUCTION_PER_RANK = builder.comment("Careful Mining: reduction per rank of the triggering miner's real TFC collapse roll (0.10 = 10%).")
                .defineInRange("carefulMiningCollapseReductionPerRank", 0.10D, 0.0D, 1.0D);
        MINING_SPEED_BONUS_PER_RANK = builder.comment("Mining: pickaxe mining-speed bonus per rank (0.05 = 5%).")
                .defineInRange("miningSpeedBonusPerRank", 0.05D, 0.0D, 10.0D);
        DEEP_MINING_SPEED_BONUS_PER_RANK = builder.comment("Deep Mining: below-Y-0 pickaxe mining-speed bonus per rank (0.05 = 5%).")
                .defineInRange("deepMiningSpeedBonusPerRank", 0.05D, 0.0D, 10.0D);
        STONE_MINING_SPEED_BONUS_PER_RANK = builder.comment("Stone Mining: raw-stone pickaxe mining-speed bonus per rank (0.04 = 4%).")
                .defineInRange("stoneMiningSpeedBonusPerRank", 0.04D, 0.0D, 10.0D);
        EXCAVATING_SPEED_BONUS_PER_RANK = builder.comment("Excavating: shovel mining-speed bonus per rank (0.03 = 3%).")
                .defineInRange("excavatingSpeedBonusPerRank", 0.03D, 0.0D, 10.0D);
        BRUTISH_MINING_REQUIRED_MINING_RANK = builder.comment("Minimum Mining rank required before Brutish Mining can be toggled.")
                .defineInRange("brutishMiningRequiredMiningRank", 4, 0, 100);
        BRUTISH_MINING_SPEED_BONUS_PER_RANK = builder.comment("Brutish Mining: mining-speed bonus per active rank (0.50 = 50%).")
                .defineInRange("brutishMiningSpeedBonusPerRank", 0.50D, 0.0D, 10.0D);
        BRUTISH_MINING_WEAR_BASE_MULTIPLIER = builder.comment("Brutish Mining: mining-durability multiplier before its active rank is added.",
                        "With the default 2.5 base and 0.5 per rank, ranks I-V cost 3x-5x durability.")
                .defineInRange("brutishMiningWearBaseMultiplier", 2.5D, 0.0D, 100.0D);
        BRUTISH_MINING_WEAR_MULTIPLIER_PER_RANK = builder.comment("Brutish Mining: additional mining-durability multiplier per active rank.")
                .defineInRange("brutishMiningWearMultiplierPerRank", 0.5D, 0.0D, 100.0D);
        BRUTISH_MINING_TOOL_CONSERVATION_CHANCE_MULTIPLIER = builder.comment("Brutish Mining: multiplier applied to Tool Conservation's chance while Brutish Mining is active.")
                .defineInRange("brutishMiningToolConservationChanceMultiplier", 2.0D, 0.0D, 100.0D);
        HEIGHTS_TRAINING_SAFE_FALL_BLOCKS_PER_RANK = builder.comment("Heights Training: safe-fall distance added per rank, in blocks.")
                .defineInRange("heightsTrainingSafeFallBlocksPerRank", 0.5D, 0.0D, 128.0D);
        CAREFUL_EXCAVATION_REACH_PER_RANK = builder.comment("Careful Excavation: block interaction-range bonus per rank while holding a pickaxe or shovel.")
                .defineInRange("carefulExcavationReachPerRank", 0.5D, 0.0D, 128.0D);
        builder.push("supportAssessment");
        SUPPORT_ASSESSMENT_RADIUS_AT_RANK_ONE = builder.comment("Support Assessment scan radius at rank I, in blocks.")
                .defineInRange("radiusAtRankOne", 0, 0, 32);
        SUPPORT_ASSESSMENT_ADDITIONAL_RADIUS_PER_RANK = builder.comment("Support Assessment scan radius added for every rank after I, in blocks.")
                .defineInRange("additionalRadiusPerRank", 1, 0, 32);
        SUPPORT_ASSESSMENT_MAX_RADIUS = builder.comment("Safety cap for Support Assessment's calculated scan radius, in blocks.")
                .defineInRange("maxRadius", 16, 0, 32);
        SUPPORT_ASSESSMENT_COOLDOWN_TICKS = builder.comment("Support Assessment cooldown, in game ticks (20 ticks = 1 second).")
                .defineInRange("cooldownTicks", 100, 0, 72_000);
        builder.pop();
        builder.pop();
        builder.push("weightPowers");
        CARRY_CAPACITY_PERCENT_PER_RANK = builder.comment("Carry Capacity % skill: percent capacity increase per unlocked rank (0.10 = 10%).")
                .defineInRange("carryCapacityPercentPerRank", 0.10D, 0.0D, 10.0D);
        CARRY_CAPACITY_FLAT_KG_PER_RANK = builder.comment("Carry Capacity Flat skill: kilograms added per unlocked rank.")
                .defineInRange("carryCapacityFlatKgPerRank", 0.0D, 0.0D, 10000.0D);
        RIDING_PULL_PERCENT_PER_RANK = builder.comment("Riding Pull % skill: horse pull-capacity increase per unlocked rank (0.10 = 10%).")
                .defineInRange("ridingPullPercentPerRank", 0.10D, 0.0D, 10.0D);
        RIDING_PULL_FLAT_KG_PER_RANK = builder.comment("Riding Pull Flat skill: kilograms added to horse pull capacity per unlocked rank.")
                .defineInRange("ridingPullFlatKgPerRank", 0.0D, 0.0D, 10000.0D);
        RIDING_SPEED_PENALTY_REDUCTION_PER_RANK = builder.comment("Riding Speed skill: reduction of the maximum horse speed penalty per rank (0.10 = 10 percentage points).")
                .defineInRange("ridingSpeedPenaltyReductionPerRank", 0.10D, 0.0D, 1.0D);
        builder.push("baseMountStats");
        HORSE_MIN_PULL_CAPACITY_KG = builder.comment("Horse pull capacity at minimum jump strength.").defineInRange("horseMinPullCapacityKg", 500.0D, 0.0D, 100000.0D);
        HORSE_MAX_PULL_CAPACITY_KG = builder.comment("Horse pull capacity at maximum jump strength.").defineInRange("horseMaxPullCapacityKg", 1000.0D, 0.0D, 100000.0D);
        DONKEY_MIN_PULL_CAPACITY_KG = builder.comment("Donkey pull capacity at minimum jump strength.").defineInRange("donkeyMinPullCapacityKg", 900.0D, 0.0D, 100000.0D);
        DONKEY_MAX_PULL_CAPACITY_KG = builder.comment("Donkey pull capacity at maximum jump strength.").defineInRange("donkeyMaxPullCapacityKg", 1800.0D, 0.0D, 100000.0D);
        MULE_MIN_PULL_CAPACITY_KG = builder.comment("Mule pull capacity at minimum jump strength.").defineInRange("muleMinPullCapacityKg", 725.0D, 0.0D, 100000.0D);
        MULE_MAX_PULL_CAPACITY_KG = builder.comment("Mule pull capacity at maximum jump strength.").defineInRange("muleMaxPullCapacityKg", 1450.0D, 0.0D, 100000.0D);
        HORSE_BACK_CAPACITY_MULTIPLIER = builder.comment("Horse rider/back capacity multiplier.").defineInRange("horseBackCapacityMultiplier", 1.0D, 0.0D, 10.0D);
        DONKEY_BACK_CAPACITY_MULTIPLIER = builder.comment("Back-weight capacity multiplier for donkeys.")
                .defineInRange("donkeyBackCapacityMultiplier", 1.50D, 0.0D, 10.0D);
        MULE_BACK_CAPACITY_MULTIPLIER = builder.comment("Back-weight capacity multiplier for mules; their expedition specialty.")
                .defineInRange("muleBackCapacityMultiplier", 1.75D, 0.0D, 10.0D);
        HORSE_SPEED_PENALTY_START = builder.comment("Horse load ratio where speed loss begins.").defineInRange("horseSpeedPenaltyStart", 0.30D, 0.0D, 1.0D);
        DONKEY_SPEED_PENALTY_START = builder.comment("Donkey load ratio where speed loss begins.").defineInRange("donkeySpeedPenaltyStart", 0.60D, 0.0D, 1.0D);
        MULE_SPEED_PENALTY_START = builder.comment("Mule load ratio where speed loss begins.").defineInRange("muleSpeedPenaltyStart", 0.45D, 0.0D, 1.0D);
        HORSE_MIN_SPEED_AT_CAPACITY = builder.comment("Horse speed multiplier at full pull capacity.").defineInRange("horseMinSpeedAtCapacity", 0.50D, 0.0D, 1.0D);
        DONKEY_MIN_SPEED_AT_CAPACITY = builder.comment("Donkey speed multiplier at full pull capacity.").defineInRange("donkeyMinSpeedAtCapacity", 0.50D, 0.0D, 1.0D);
        MULE_MIN_SPEED_AT_CAPACITY = builder.comment("Mule speed multiplier at full pull capacity.").defineInRange("muleMinSpeedAtCapacity", 0.60D, 0.0D, 1.0D);
        HORSE_JUMP_LOAD_LIMIT = builder.comment("Horse load ratio where jumping is blocked.").defineInRange("horseJumpLoadLimit", 0.45D, 0.0D, 10.0D);
        DONKEY_JUMP_LOAD_LIMIT = builder.comment("Donkey load ratio where jumping is blocked.").defineInRange("donkeyJumpLoadLimit", 0.25D, 0.0D, 10.0D);
        MULE_JUMP_LOAD_LIMIT = builder.comment("Mule load ratio where jumping is blocked.").defineInRange("muleJumpLoadLimit", 0.35D, 0.0D, 10.0D);
        HORSE_STEP_LOAD_LIMIT = builder.comment("Horse load ratio where step height is reduced.").defineInRange("horseStepLoadLimit", 0.75D, 0.0D, 10.0D);
        DONKEY_STEP_LOAD_LIMIT = builder.comment("Donkey load ratio where step height is reduced.").defineInRange("donkeyStepLoadLimit", 0.55D, 0.0D, 10.0D);
        MULE_STEP_LOAD_LIMIT = builder.comment("Mule load ratio where step height is reduced.").defineInRange("muleStepLoadLimit", 0.65D, 0.0D, 10.0D);
        PULL_CAPACITY_MIN_JUMP_STRENGTH = builder.comment("Jump strength mapped to each animal's minimum pull capacity.").defineInRange("pullCapacityMinJumpStrength", 0.40D, 0.0D, 10.0D);
        PULL_CAPACITY_MAX_JUMP_STRENGTH = builder.comment("Jump strength mapped to each animal's maximum pull capacity.").defineInRange("pullCapacityMaxJumpStrength", 1.00D, 0.01D, 10.0D);
        REDUCED_STEP_HEIGHT = builder.comment("Step height used after an animal reaches its configured step-load threshold.").defineInRange("reducedStepHeight", 0.60D, 0.0D, 5.0D);
        builder.pop();
        HORSE_LIGHT_HARNESS_SPEED_START_PER_RANK = builder.comment("Light Harness skill: extra horse load percentage per rank before speed loss begins.")
                .defineInRange("horseLightHarnessSpeedStartPerRank", 0.05D, 0.0D, 1.0D);
        HORSE_LIGHT_HARNESS_JUMP_LIMIT_PER_RANK = builder.comment("Light Harness skill: extra horse load percentage per rank before jumping is blocked.")
                .defineInRange("horseLightHarnessJumpLimitPerRank", 0.03D, 0.0D, 1.0D);
        HORSE_LIGHT_HARNESS_STEP_LIMIT_PER_RANK = builder.comment("Light Harness skill: extra horse load percentage per rank before step height is reduced.")
                .defineInRange("horseLightHarnessStepLimitPerRank", 0.03D, 0.0D, 1.0D);
        DONKEY_DRAFT_HARNESS_PULL_PERCENT_PER_RANK = builder.comment("Draft Harness skill: donkey pull-capacity increase per rank (0.10 = 10%).")
                .defineInRange("donkeyDraftHarnessPullPercentPerRank", 0.10D, 0.0D, 10.0D);
        MULE_EXPEDITION_BALANCE_REDUCTION_PER_RANK = builder.comment("Expedition Balance skill: rider, armor, and animal-cart passenger weight reduction per rank for mules.")
                .defineInRange("muleExpeditionBalanceReductionPerRank", 0.05D, 0.0D, 1.0D);
        HORSE_ARMOR_WEIGHT_REDUCTION_PER_RANK = builder.comment("Horse Armor Weight skill: equipped body-armor weight reduction per rank.")
                .defineInRange("horseArmorWeightReductionPerRank", 0.05D, 0.0D, 1.0D);
        HORSE_ARMOR_EFFECTIVENESS_PER_RANK = builder.comment("Horse Armor Effectiveness skill: percent more armor and toughness from equipped body armor per rank.")
                .defineInRange("horseArmorEffectivenessPerRank", 0.10D, 0.0D, 10.0D);
        MOUNT_SPEED_PERCENT_PER_RANK = builder.comment("Mount Speed skill: movement-speed increase per rank for a ridden horse-like animal.")
                .defineInRange("mountSpeedPercentPerRank", 0.05D, 0.0D, 10.0D);
        HORSE_SPEED_PERCENT_PER_RANK = builder.comment("Horse Speed skill: additional movement-speed increase per rank for horses.")
                .defineInRange("horseSpeedPercentPerRank", 0.05D, 0.0D, 10.0D);
        MULE_SPEED_PERCENT_PER_RANK = builder.comment("Mule Speed skill: additional movement-speed increase per rank for mules.")
                .defineInRange("muleSpeedPercentPerRank", 0.05D, 0.0D, 10.0D);
        MOUNT_ARMOR_PER_RANK = builder.comment("Mount Armor skill: armor points added per rank to a ridden horse-like animal.")
                .defineInRange("mountArmorPerRank", 1.0D, 0.0D, 100.0D);
        HORSE_ARMOR_PER_RANK = builder.comment("Horse Armor skill: additional armor points per rank for horses.")
                .defineInRange("horseArmorPerRank", 1.0D, 0.0D, 100.0D);
        MULE_ARMOR_PER_RANK = builder.comment("Mule Armor skill: additional armor points per rank for mules.")
                .defineInRange("muleArmorPerRank", 1.0D, 0.0D, 100.0D);
        MOUNT_TOUGHNESS_PER_RANK = builder.comment("Mount Toughness skill: armor toughness added per rank to a ridden horse-like animal.")
                .defineInRange("mountToughnessPerRank", 0.5D, 0.0D, 100.0D);
        HORSE_TOUGHNESS_PER_RANK = builder.comment("Horse Toughness skill: additional armor toughness per rank for horses.")
                .defineInRange("horseToughnessPerRank", 0.5D, 0.0D, 100.0D);
        MULE_TOUGHNESS_PER_RANK = builder.comment("Mule Toughness skill: additional armor toughness per rank for mules.")
                .defineInRange("muleToughnessPerRank", 0.5D, 0.0D, 100.0D);
        PACK_ANIMAL_PULL_PERCENT_PER_RANK = builder.comment("Pack Animal Pull % skill: pull-capacity increase per rank for donkeys and mules.")
                .defineInRange("packAnimalPullPercentPerRank", 0.10D, 0.0D, 10.0D);
        PACK_ANIMAL_PULL_FLAT_KG_PER_RANK = builder.comment("Pack Animal Pull Flat skill: kilograms added per rank for donkeys and mules.")
                .defineInRange("packAnimalPullFlatKgPerRank", 0.0D, 0.0D, 10000.0D);
        PACK_ANIMAL_BACK_PERCENT_PER_RANK = builder.comment("Pack Animal Back % skill: back-weight capacity increase per rank for donkeys and mules.")
                .defineInRange("packAnimalBackPercentPerRank", 0.10D, 0.0D, 10.0D);
        PACK_ANIMAL_BACK_FLAT_KG_PER_RANK = builder.comment("Pack Animal Back Flat skill: kilograms added per rank for donkeys and mules.")
                .defineInRange("packAnimalBackFlatKgPerRank", 0.0D, 0.0D, 10000.0D);
        PACK_ANIMAL_JUMP_LIMIT_PERCENT_PER_RANK = builder.comment("Pack Animal Jump Limit skill: load-limit increase per rank before donkey/mule jumping is blocked.")
                .defineInRange("packAnimalJumpLimitPercentPerRank", 0.10D, 0.0D, 10.0D);
        PACK_ANIMAL_STEP_LIMIT_PERCENT_PER_RANK = builder.comment("Pack Animal Step Limit skill: load-limit increase per rank before donkey/mule step height is reduced.")
                .defineInRange("packAnimalStepLimitPercentPerRank", 0.10D, 0.0D, 10.0D);
        builder.pop();
        builder.push("explorationPowers");
        WATER_CONSERVATION_TRAVEL_THIRST_REDUCTION_PER_RANK = builder.comment("Water Conservation: reduction per rank of thirst caused by travel only (0.08 = 8%).",
                        "Temperature/heat thirst remains unchanged.")
                .defineInRange("waterConservationTravelThirstReductionPerRank", 0.08D, 0.0D, 1.0D);
        SCRAMBLING_UPWARD_SPEED_BONUS_PER_RANK = builder.comment("Scrambling: upward ladder/vine speed bonus per rank (0.10 = 10%).")
                .defineInRange("scramblingUpwardSpeedBonusPerRank", 0.10D, 0.0D, 1.0D);
        EXPEDITION_PACKING_WEIGHT_REDUCTION_PER_RANK = builder.comment("Expedition Packing: effective-weight reduction per rank for tagged supply shells (0.06 = 6%).",
                        "Container contents are never discounted.")
                .defineInRange("expeditionPackingWeightReductionPerRank", 0.06D, 0.0D, 1.0D);
        TRAIL_ACTIVITY_SPEED_BONUS_PER_RANK = builder.comment("Trailblazing and Well Trodden: common movement-speed bonus per rank in their activity blend (0.03 = 3%).")
                .defineInRange("trailActivitySpeedBonusPerRank", 0.03D, 0.0D, 10.0D);
        ENDURANCE_SPRINT_EXHAUSTION_REDUCTION_PER_RANK = builder.comment("Endurance: sprinting-exhaustion reduction per rank (0.05 = 5%).")
                .defineInRange("enduranceSprintExhaustionReductionPerRank", 0.05D, 0.0D, 1.0D);
        ACCLIMATIZATION_HARMFUL_EXPOSURE_REDUCTION_PER_RANK = builder.comment("Acclimatization: harmful Thermia heat/cold exposure reduction per rank (0.05 = 5%).")
                .defineInRange("acclimatizationHarmfulExposureReductionPerRank", 0.05D, 0.0D, 1.0D);
        QUIET_PASSAGE_DETECTION_REDUCTION_PER_RANK = builder.comment("Quiet Passage: TFC prey/predator detection-radius reduction per rank while sneaking (0.05 = 5%).")
                .defineInRange("quietPassageDetectionReductionPerRank", 0.05D, 0.0D, 1.0D);
        builder.push("trailActivity");
        TRAIL_ACTIVITY_DECAY_TICKS = builder.comment("Game ticks for an untouched area's activity to decay to zero. Tune after gameplay testing.")
                .defineInRange("decayTicks", 72000, 20, 2_400_000);
        TRAIL_ACTIVITY_NORMALIZATION_LOW = builder.comment("Raw shared activity at which normalized activity A starts at zero.")
                .defineInRange("normalizationLow", 0.0D, 0.0D, 1_000_000.0D);
        TRAIL_ACTIVITY_NORMALIZATION_HIGH = builder.comment("Raw shared activity at which normalized activity A reaches one. Tune after gameplay testing.")
                .defineInRange("normalizationHigh", 320.0D, 0.0001D, 1_000_000.0D);
        TRAIL_ACTIVITY_SAMPLE_INTERVAL_TICKS = builder.comment("How often eligible player displacement is sampled.")
                .defineInRange("sampleIntervalTicks", 10, 1, 1200);
        TRAIL_ACTIVITY_CONTRIBUTION_WINDOW_TICKS = builder.comment("Length of the per-player contribution cap window.")
                .defineInRange("contributionWindowTicks", 1200, 20, 72_000);
        TRAIL_ACTIVITY_CONTRIBUTION_CAP_BLOCKS = builder.comment("Maximum eligible displacement one player can add to shared activity in each cap window.")
                .defineInRange("contributionCapBlocks", 128.0D, 0.0D, 100_000.0D);
        TRAIL_ACTIVITY_MAX_SAMPLE_DISTANCE = builder.comment("Displacements above this between samples are ignored as teleports or discontinuities.")
                .defineInRange("maxSampleDistance", 12.0D, 0.1D, 1024.0D);
        TRAIL_ACTIVITY_HEATMAP_MAX_RADIUS_CHUNKS = builder.comment("Maximum chunk radius returned to a JourneyMap activity-heatmap request.",
                        "Map views wider than this are cropped around their centre; this bounds server work and packet size.")
                .defineInRange("heatmapMaxRadiusChunks", 48, 1, 256);
        TRAIL_ACTIVITY_HEATMAP_MAX_ENTRIES = builder.comment("Maximum non-zero activity chunks returned in one JourneyMap heatmap response.",
                        "When a view has more entries, the most active chunks are returned.")
                .defineInRange("heatmapMaxEntries", 1024, 1, 4096);
        TRAIL_ACTIVITY_HEATMAP_REQUEST_COOLDOWN_TICKS = builder.comment("Minimum ticks between one player's JourneyMap heatmap requests.")
                .defineInRange("heatmapRequestCooldownTicks", 20, 1, 1200);
        builder.pop();
        builder.pop();
        SPEC = builder.build();
    }

    private PowersConfig() {}
}
