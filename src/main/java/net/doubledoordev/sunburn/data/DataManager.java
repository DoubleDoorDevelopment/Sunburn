package net.doubledoordev.sunburn.data;

import com.google.common.collect.HashBasedTable;
import com.google.gson.*;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DataManager extends JsonReloadListener {
    public static final HashMap<ResourceLocation, JsonObject> LOADED_JSONS = new HashMap<>();
    public static final HashMap<ResourceLocation, BurnRulesData> BURN_DATA = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static HashBasedTable<ResourceLocation, String, BurnRulesData> rulesTable = HashBasedTable.create();


    public DataManager(String directory) {
        super(GSON, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, IResourceManager resourceManager, IProfiler profiler) {

        LOADED_JSONS.clear();

        try {
            jsonElementMap.forEach((resourceLocation, jsonElement) ->
            {
                JsonObject json = jsonElement.getAsJsonObject();
                LOADED_JSONS.put(resourceLocation, json);

            });
        } catch (JsonParseException error) {
            LOGGER.error("Failed to load JSON");
        }

        if (!LOADED_JSONS.isEmpty()) {
            LOADED_JSONS.forEach((resourceLocation, jsonObject) -> BURN_DATA.put(resourceLocation, BurnRulesValidator.fromJSON(resourceLocation, jsonObject)));

            rulesTable = BurnRulesValidator.mergeRules(BURN_DATA);
        }
        LOGGER.info("Loaded {} Sunburn rules for {} dimensions!", LOADED_JSONS.size(), rulesTable.size());
    }
}
