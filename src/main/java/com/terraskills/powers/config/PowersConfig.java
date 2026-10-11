package com.terraskills.powers.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/** Server-owned balance knobs; datapacks remain responsible for skill definitions and prerequisites. */
public final class PowersConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CAREFUL_EYE_GEMS;

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
        SPEC = builder.build();
    }

    private PowersConfig() {}
}
