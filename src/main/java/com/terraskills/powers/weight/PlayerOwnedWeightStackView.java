package com.terraskills.powers.weight;

import com.warfactory.ultimateweight.api.WeightDataView;
import com.warfactory.ultimateweight.api.WeightItemView;
import com.warfactory.ultimateweight.api.WeightStackView;
import net.minecraft.world.entity.player.Player;

/** Tags only a top-level player inventory stack with its owner; nested contents remain ordinary views. */
public record PlayerOwnedWeightStackView(Player player, WeightStackView delegate) implements WeightStackView {
    @Override public WeightItemView item() { return delegate.item(); }
    @Override public int count() { return delegate.count(); }
    @Override public int metadata() { return delegate.metadata(); }
    @Override public int resolutionDepth() { return delegate.resolutionDepth(); }
    @Override public String complexCacheKey() { return delegate.complexCacheKey(); }
    @Override public Object unwrap() { return delegate.unwrap(); }
    @Override public WeightDataView data() { return delegate.data(); }
}
