package net.doubledoordev.sunburn.data;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BurnRulesData {

    private static final Logger LOGGER = LogManager.getLogger();
    ResourceLocation dimension;
    ArrayList<ResourceLocation> biomeList;
    String burnDays;
    int startTime;
    int endTime;
    int loadingSafeTime;
    int lengthOfBurn;
    boolean ignoreMagic;
    boolean ignoreArmor;
    boolean scalesWithDifficulty;
    float burnDamage;
    int skyLightBurnLevel;
    int blockLightBurnLevel;
    int alwaysSafeBelowYLevel;
    int alwaysBurnAboveYLevel;
    boolean damageEquippedGear;
    boolean wetStopsBurn;
    boolean fullArmorToBlockBurn;
    String deathMessage;

    public BurnRulesData(ResourceLocation dimension, ArrayList<ResourceLocation> biomeList, String burnDays, int startTime,
                         int endTime, int loadingSafeTime, int lengthOfBurn, boolean ignoreMagic, boolean ignoreArmor,
                         boolean scalesWithDifficulty, float burnDamage, int skyLightBurnLevel, int blockLightBurnLevel,
                         int alwaysSafeBelowYLevel, int alwaysBurnAboveYLevel, boolean damageEquippedGear, boolean wetStopsBurn,
                         boolean fullArmorToBlockBurn, String deathMessage) {
        this.dimension = dimension;
        this.biomeList = biomeList;
        this.burnDays = burnDays;
        this.startTime = startTime;
        this.endTime = endTime;
        this.loadingSafeTime = loadingSafeTime;
        this.lengthOfBurn = lengthOfBurn;
        this.ignoreMagic = ignoreMagic;
        this.ignoreArmor = ignoreArmor;
        this.scalesWithDifficulty = scalesWithDifficulty;
        this.burnDamage = burnDamage;
        this.skyLightBurnLevel = skyLightBurnLevel;
        this.blockLightBurnLevel = blockLightBurnLevel;
        this.alwaysSafeBelowYLevel = alwaysSafeBelowYLevel;
        this.alwaysBurnAboveYLevel = alwaysBurnAboveYLevel;
        this.damageEquippedGear = damageEquippedGear;
        this.wetStopsBurn = wetStopsBurn;
        this.fullArmorToBlockBurn = fullArmorToBlockBurn;
        this.deathMessage = deathMessage;

        if (startTime < 0)
            LOGGER.error("SunBurn start time can not be smaller than 0!");

        if (startTime > endTime)
            LOGGER.error("SunBurn start time can not be larger than end time!");

        if (alwaysSafeBelowYLevel > alwaysBurnAboveYLevel)
            LOGGER.error("SunBurn safe below Y level can not be larger than the always burn above Y level!");
    }

    public ResourceLocation dimension() {
        return dimension;
    }

    public ArrayList<ResourceLocation> biomeList() {
        return biomeList;
    }

    public String burnDays() {
        return burnDays;
    }

    public int startTime() {
        return startTime;
    }

    public int endTime() {
        return endTime;
    }

    public int loadingSafeTime() {
        return loadingSafeTime;
    }

    public int lengthOfBurn() {
        return lengthOfBurn;
    }

    public boolean ignoreMagic() {
        return ignoreMagic;
    }

    public boolean ignoreArmor() {
        return ignoreArmor;
    }

    public boolean scalesWithDifficulty() {
        return scalesWithDifficulty;
    }

    public float burnDamage() {
        return burnDamage;
    }

    public int skyLightBurnLevel() {
        return skyLightBurnLevel;
    }

    public int blockLightBurnLevel() {
        return blockLightBurnLevel;
    }

    public int alwaysSafeBelowYLevel() {
        return alwaysSafeBelowYLevel;
    }

    public int alwaysBurnAboveYLevel() {
        return alwaysBurnAboveYLevel;
    }

    public boolean damageEquippedGear() {
        return damageEquippedGear;
    }

    public boolean wetStopsBurn() {
        return wetStopsBurn;
    }

    public boolean fullArmorToBlockBurn() {
        return fullArmorToBlockBurn;
    }

    public String deathMessage() {
        return deathMessage;
    }
}