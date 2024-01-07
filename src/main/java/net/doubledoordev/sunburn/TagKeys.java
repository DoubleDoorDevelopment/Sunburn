package net.doubledoordev.sunburn;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

public class TagKeys {
    public static final TagKey<Item> SUN_BLOCKING_ITEMS = ItemTags.create(new ResourceLocation(SunBurn.MOD_ID, "blocks_sun"));
    public static final TagKey<Biome> ALWAYS_SAFE_BIOMES = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(SunBurn.MOD_ID, "always_safe"));
}
