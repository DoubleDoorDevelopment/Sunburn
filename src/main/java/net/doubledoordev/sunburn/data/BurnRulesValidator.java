package net.doubledoordev.sunburn.data;

import com.google.common.collect.HashBasedTable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class BurnRulesValidator {

    private static final Logger LOGGER = LogManager.getLogger();

    public static BurnRulesData fromJSON(ResourceLocation resourceLocation, JsonObject json) {
        if (validateJSONObject(resourceLocation, json))
            return new BurnRulesData(
                    json.has("dimension") ? new ResourceLocation(json.get("dimension").getAsString()) :
                            new ResourceLocation("minecraft:overworld"),
                    json.has("biomes") ? convertJSONArray(json.getAsJsonArray("biomes")) : new ArrayList<>(),
                    json.has("burnDays") ? json.get("burnDays").getAsString() : ">0",
                    json.get("startTime").getAsInt(),
                    json.get("endTime").getAsInt(),
                    json.get("loadingSafeTime").getAsInt(),
                    json.get("lengthOfBurn").getAsInt(),
                    json.has("ignoreMagic") && json.get("ignoreMagic").getAsBoolean(),
                    json.has("ignoreArmor") && json.get("ignoreArmor").getAsBoolean(),
                    json.has("scalesWithDifficulty") && json.get("scalesWithDifficulty").getAsBoolean(),
                    json.get("burnDamage").getAsFloat(),
                    json.get("skyLightBurnLevel").getAsInt(),
                    json.get("blockLightBurnLevel").getAsInt(),
                    json.get("alwaysSafeBelowYLevel").getAsInt(),
                    json.get("alwaysBurnAboveYLevel").getAsInt(),
                    json.has("damageEquippedGear") && json.get("damageEquippedGear").getAsBoolean(),
                    !json.has("wetStopsBurn") || json.get("wetStopsBurn").getAsBoolean(),
                    !json.has("powderSnowStopsBurn") || json.get("powderSnowStopsBurn").getAsBoolean(),
                    json.has("fullArmorToBlockBurn") && json.get("fullArmorToBlockBurn").getAsBoolean(),
                    json.has("deathMessage") ? json.get("deathMessage").getAsString() : "%1$s baked in the sun"
            );
        else
            throw new JsonParseException("Sunburn datapack error(s) preventing loading, refer to your game log for all the problems.");
    }

    public static HashBasedTable<ResourceLocation, String, BurnRulesData> mergeRules(Map<ResourceLocation, BurnRulesData> dataMap) {
        HashBasedTable<ResourceLocation, String, BurnRulesData> rulesTable = HashBasedTable.create();

        dataMap.forEach((resourceLocation, data) -> {
            if (!rulesTable.containsRow(data.dimension()))
                rulesTable.put(data.dimension(), data.burnDays(), data);
            else {
                rulesTable.row(data.dimension()).put(data.burnDays(), data);
            }
        });

        return rulesTable;
    }

    public static boolean isValidDay(ResourceLocation dataLocation, String dayString, int currentDay) {
        // 1 <- Only on first day
        // 1-5 <- On first through 5th day
        // 1,2,6 <- On First, Second and Sixth day
        // <10 <- Before 10th day
        // >20 <- After 20th day
        // 1,5-10,>15 <- On first, 5th to 10th day and every day after 15th

        if (dayString.contains(",")) {
            String[] days = dayString.split(",");

            for (String dayValue : days) {
                return isTodayValid(dataLocation, dayValue, currentDay);
            }
        }
        return isTodayValid(dataLocation, dayString, currentDay);
    }

    private static boolean isTodayValid(ResourceLocation dataLocation, String timeSubString, int currentDay) {
        if (timeSubString.contains("-")) {
            String[] dayRange = timeSubString.split("-");
            if (dayRange.length < 1) {
                LOGGER.error("Date range missing second date! IDK how you got here but you did....");
                return false;
            }
            int rangeStart = Integer.parseInt(dayRange[0]);
            int rangeEnd = Integer.parseInt(dayRange[1]);

            if (rangeStart > rangeEnd) {
                LOGGER.error("Error in {} You can't go back in time.... even if you think dinosaurs are cool.", dataLocation);

            }
            if (Integer.parseInt(dayRange[0]) >= currentDay || Integer.parseInt(dayRange[1]) <= currentDay)
                return true;
        }

        if (timeSubString.startsWith("<")) {
            return currentDay <= Integer.parseInt(timeSubString.substring(1));
        }

        if (timeSubString.startsWith(">")) {
            return currentDay >= Integer.parseInt(timeSubString.substring(1));
        }

        return currentDay == Integer.parseInt(timeSubString);
    }

    private static boolean validDayChar(char dayChar) {
        return dayChar == '>' || dayChar == '<' || dayChar == ',' || dayChar == '-' || dayChar >= '0' && dayChar <= '9';
    }

    private static boolean isInvalidDayString(String dayString) {
        for (int i = 0; i < dayString.length(); ++i) {
            if (!validDayChar(dayString.charAt(i))) {
                LOGGER.error("{} is not a valid time period character, Must only contain [0-9,><-]", dayString.charAt(i));
                return true;
            }
        }

        if (dayString.matches("(?<=\\D)-|-(?=\\D)|^-|-$")) {
            LOGGER.error("{} Invalid burnDays string! A [-] requires a number on both sides. [10-11] is valid. [-10] or [10-] is not.", dayString);
            return true;
        }

        return false;
    }

    private static ArrayList<ResourceLocation> convertJSONArray(JsonArray jsonArray) {
        ArrayList<ResourceLocation> tempBiomeList = new ArrayList<>();
        jsonArray.forEach(jsonElement -> {
            if (ResourceLocation.isValidResourceLocation(jsonElement.getAsString())) {
                tempBiomeList.add(new ResourceLocation(jsonElement.getAsString()));
            }
        });

        return tempBiomeList;
    }

    public static boolean validateJSONObject(ResourceLocation resourceLocation, JsonObject json) {
        String location = resourceLocation.toString();

        AtomicBoolean isComplete = new AtomicBoolean(true);

        if (json.has("dimension") && !json.get("dimension").isJsonNull()) {
            if (!ResourceLocation.isValidResourceLocation(json.get("dimension").getAsString())) {
                LOGGER.error("{} Has an invalid dimension set! Please correct \"{}\" to a valid dimension.", location, json.get("dimension").getAsString());
                isComplete.set(false);
            }
        }

        if (json.has("biomes") && !json.get("biomes").isJsonNull()) {
            json.get("biomes").getAsJsonArray().forEach(element -> {
                if (!ResourceLocation.isValidResourceLocation(element.getAsString())) {
                    LOGGER.error("{} Invalid biome found! Please correct \"{}\" to a valid biome.", location, element.getAsString());
                    isComplete.set(false);
                }
            });
        }

        if (json.has("burnDays") && !json.get("burnDays").isJsonNull()) {
            if (isInvalidDayString(json.get("burnDays").getAsString()))
                isComplete.set(false);
        }

        if (!json.has("startTime")) {
            LOGGER.error("{} Missing start time element. Add \"startTime\":1000 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        } else if (json.get("startTime").getAsInt() < 0) {
            LOGGER.error("{} Start time can not be smaller than 0!", location);
            isComplete.set(false);
        }

        if (!json.has("endTime")) {
            LOGGER.error("{} Missing end time element. Add \"endTime\":15000 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        } else if (json.get("startTime").getAsInt() > json.get("endTime").getAsInt()) {
            LOGGER.error("{} End time can not be smaller than start time!", location);
            isComplete.set(false);
        }

        if (!json.has("loadingSafeTime")) {
            LOGGER.error("{} Missing safe time element. Add \"loadingSafeTime\":0 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        }

        if (!json.has("lengthOfBurn")) {
            LOGGER.error("{} Missing burn length element. Add \"lengthOfBurn\":1 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        }

        if (!json.has("burnDamage")) {
            LOGGER.error("{} Missing burn Damage element. Add \"burnDamage\":1 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        }

        if (!json.has("skyLightBurnLevel")) {
            LOGGER.error("{} Missing burn length element. Add \"skyLightBurnLevel\":0 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        }
        if (json.get("skyLightBurnLevel").getAsInt() > 15)
            LOGGER.warn("{} \"skyLightBurnLevel\" is greater than 15. Burning is disabled for sky light.", location);

        if (!json.has("blockLightBurnLevel")) {
            LOGGER.error("{} Missing burn length element. Add \"blockLightBurnLevel\":-1 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        }
        if (json.get("blockLightBurnLevel").getAsInt() > 15)
            LOGGER.warn("{} \"blockLightBurnLevel\" is greater than 15. Burning is disabled for block light.", location);

        if (!json.has("alwaysSafeBelowYLevel")) {
            LOGGER.error("{} Missing burn length element. Add \"alwaysSafeBelowYLevel\":64 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        }

        if (!json.has("alwaysBurnAboveYLevel")) {
            LOGGER.error("{} Missing burn length element. Add \"alwaysBurnAboveYLevel\":100 to your JSON, change the value if needed.", location);
            isComplete.set(false);
        } else if (json.get("alwaysSafeBelowYLevel").getAsInt() > json.get("alwaysBurnAboveYLevel").getAsInt()) {
            LOGGER.error("{} Always safe level can not be larger than always burn level!", location);
            isComplete.set(false);
        }

        return isComplete.get();
    }
}
