package net.doubledoordev.sunburn;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class TagKeys {
    public static final ITag.INamedTag<Item> SUN_BLOCKING_ITEMS = ItemTags.createOptional(new ResourceLocation(SunBurn.MOD_ID, "blocks_sun"));
}
