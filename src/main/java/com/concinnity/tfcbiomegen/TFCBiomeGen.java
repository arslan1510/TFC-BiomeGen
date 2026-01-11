package com.concinnity.tfcbiomegen;

import com.concinnity.tfcbiomegen.api.BiomeAPI;
import com.concinnity.tfcbiomegen.data.BiomeExtensionConfigLoader;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TFCBiomeGen.MODID)
public class TFCBiomeGen {
    public static final String MODID = "tfcbiomegen";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TFCBiomeGen(IEventBus modEventBus, ModContainer modContainer) {
        BiomeAPI.init(modEventBus, MODID);
        LOGGER.info("TFC-BiomeGen initialized - BiomeAPI ready");
        BiomeExtensionConfigLoader.load();
    }
}
