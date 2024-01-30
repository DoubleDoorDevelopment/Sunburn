package net.doubledoordev.sunburn.data;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public record BurnRulesData(ResourceLocation dimension, ArrayList<ResourceLocation> biomeList, String burnDays,
                            int startTime, int endTime, int loadingSafeTime, int lengthOfBurn,
                            boolean ignoreMagic, boolean ignoreArmor, boolean scalesWithDifficulty, float burnDamage,
                            int skyLightBurnLevel, int blockLightBurnLevel, int alwaysSafeBelowYLevel,
                            int alwaysBurnAboveYLevel, boolean damageEquippedGear, boolean wetStopsBurn,
                            boolean powderSnowStopsBurn, boolean fullArmorToBlockBurn, String deathMessage) {
    private static final Logger LOGGER = LogManager.getLogger();

    public BurnRulesData {

        if (startTime < 0)
            LOGGER.error("SunBurn start time can not be smaller than 0!");

        if (startTime > endTime)
            LOGGER.error("SunBurn start time can not be larger than end time!");

        if (alwaysSafeBelowYLevel > alwaysBurnAboveYLevel)
            LOGGER.error("SunBurn safe below Y level can not be larger than the always burn above Y level!");

    }
}