package com.terraskills.powers.exploration;

import com.nyonyix.thermia.data.attachment.EntityTemperature;
import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/** Reduces only Thermia's proposed movement farther beyond the player's own healthy interval. */
public final class AcclimatizationPowers {
    private AcclimatizationPowers() {}

    public static float reduceHarmfulExposure(Entity entity, EntityTemperature data, float proposed) {
        if (!(entity instanceof ServerPlayer player)) return proposed;
        double reduction = Math.clamp(PowersConfig.ACCLIMATIZATION_HARMFUL_EXPOSURE_REDUCTION_PER_RANK.get()
                * Math.clamp(SkillRanks.acclimatization(player), 0, 5), 0.0D, 1.0D);
        if (reduction <= 0.0D) return proposed;
        float current = data.internalTemperature();
        float low = data.minInternalTemperature();
        float high = data.maxInternalTemperature();
        if (current > high && proposed > current) return (float) (current + (proposed - current) * (1.0D - reduction));
        if (current < low && proposed < current) return (float) (current + (proposed - current) * (1.0D - reduction));
        if (current >= low && current <= high && proposed > high) return (float) (high + (proposed - high) * (1.0D - reduction));
        if (current >= low && current <= high && proposed < low) return (float) (low + (proposed - low) * (1.0D - reduction));
        return proposed;
    }
}
