package net.doubledoordev.sunburn;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
    public static final String VERSION = "2.0.1";

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
}
