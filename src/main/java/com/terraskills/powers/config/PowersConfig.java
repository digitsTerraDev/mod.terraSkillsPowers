package com.terraskills.powers.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/** Server-owned balance knobs; datapacks remain responsible for skill definitions and prerequisites. */
public final class PowersConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CAREFUL_EYE_GEMS;
    public static final ModConfigSpec.IntValue HAZARD_SHORE_RADIUS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> HAZARD_SHORES;
    public static final ModConfigSpec.DoubleValue HAZARD_CHANCE_PER_RANK;
    public static final ModConfigSpec.BooleanValue HAZARD_ALLOW_AUTOMATED_PLACEMENT;

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
        SPEC = builder.build();
    }

    private PowersConfig() {}
}
