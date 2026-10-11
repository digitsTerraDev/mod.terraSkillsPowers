package com.terraskills.powers;

import com.mojang.logging.LogUtils;
import com.terraskills.powers.network.PowersNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TerraSkillsPowers.MOD_ID)
public final class TerraSkillsPowers {
    public static final String MOD_ID = "terraskillspowers";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TerraSkillsPowers(IEventBus modBus, ModContainer container) {
        modBus.addListener(PowersNetwork::registerPayloads);
        LOGGER.info("TerraSkills Powers loaded");
    }
}
