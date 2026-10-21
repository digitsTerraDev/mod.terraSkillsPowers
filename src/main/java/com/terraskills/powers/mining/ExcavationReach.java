package com.terraskills.powers.mining;

import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Maintains the transient interaction-range bonus granted by Careful Excavation. */
public final class ExcavationReach {
    private static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("terraskillspowers", "excavation_reach");
    private static final ResourceLocation LEGACY_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("terra", "excavation_reach");
    private static final Map<UUID, State> STATES = new HashMap<>();

    private ExcavationReach() {}

    public static void reconcile(ServerPlayer player, boolean refreshRank) {
        boolean qualifyingTool = player.getMainHandItem().is(ItemTags.PICKAXES) || player.getMainHandItem().is(ItemTags.SHOVELS);
        State previous = STATES.get(player.getUUID());
        if (previous != null && previous.qualifyingTool() == qualifyingTool && !refreshRank) return;

        AttributeInstance range = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (range == null) return;

        // Remove the KubeJS modifier as part of the native migration, even if this player has no rank yet.
        range.removeModifier(LEGACY_MODIFIER_ID);
        if (!qualifyingTool) {
            range.removeModifier(MODIFIER_ID);
            STATES.put(player.getUUID(), new State(false));
            return;
        }

        int rank = SkillRanks.rank(player, "careful_excavation");
        if (rank == 0) {
            range.removeModifier(MODIFIER_ID);
            STATES.put(player.getUUID(), new State(true));
            return;
        }
        range.addOrUpdateTransientModifier(new AttributeModifier(MODIFIER_ID,
                PowersConfig.CAREFUL_EXCAVATION_REACH_PER_RANK.get() * rank,
                AttributeModifier.Operation.ADD_VALUE));
        STATES.put(player.getUUID(), new State(true));
    }

    public static void clear(ServerPlayer player) {
        AttributeInstance range = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (range != null) range.removeModifier(MODIFIER_ID);
        STATES.remove(player.getUUID());
    }

    public static void forget(UUID playerId) {
        STATES.remove(playerId);
    }

    public static void clearAll() {
        STATES.clear();
    }

    private record State(boolean qualifyingTool) {}
}
