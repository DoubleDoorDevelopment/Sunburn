package net.doubledoordev.sunburn;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
        modid = Sunburn.MOD_ID,
        name = Sunburn.MOD_NAME,
        version = Sunburn.VERSION,
        acceptableRemoteVersions = "*"
)
public class Sunburn
{

    public static final String MOD_ID = "sunburn";
    public static final String MOD_NAME = "Sunburn";
    public static final String VERSION = "1.0.0";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static Sunburn INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandlers());
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {

    }

    /**
     * Forge will automatically look up and bind blocks to the fields in this class
     * based on their registry name.
     */
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Blocks
    {
    }

    /**
     * Forge will automatically look up and bind items to the fields in this class
     * based on their registry name.
     */
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Items
    {
    }

    /**
     * This is a special class that listens to registry events, to allow creation of mod blocks and items at the proper time.
     */
    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler
    {
        /**
         * Listen for the register event for creating custom items
         */
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event)
        {
        }

        /**
         * Listen for the register event for creating custom blocks
         */
        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event)
        {
        }
    }
}
