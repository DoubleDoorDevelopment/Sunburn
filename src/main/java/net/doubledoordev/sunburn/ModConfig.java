package net.doubledoordev.sunburn;

import net.minecraftforge.common.config.Config;

@Config(modid = Sunburn.MOD_ID)
@Config.LangKey("sunburn.config.title")
public class ModConfig
{
    @Config.LangKey("sunburn.config.burntime.start")
    @Config.Comment("Time of day burning starts in ticks. Normal day length is 0 to 23999.")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int burnTimeStart = 1000;

    @Config.LangKey("sunburn.config.burntime.stop")
    @Config.Comment("Time of day burning stops in ticks. Normal day length is 0 to 23999.")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int burnTimeStop = 15000;

    @Config.LangKey("sunburn.config.daylength")
    @Config.Comment("How long is one day. Normal day length 24000. DO NOT CHANGE UNLESS YOU CHANGE THE DAY LENGTH!")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int lengthOfDay = 24000;

    @Config.LangKey("sunburn.config.burnlength")
    @Config.Comment("How long a player is set to burn for. This is how long the player will burn AFTER they are safe.")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int lengthOfBurn = 1;

    @Config.LangKey("sunburn.config.playermustseesky")
    @Config.Comment("The player only burns if they see the sky.")
    public static boolean playerMustSeeSky = true;

    @Config.LangKey("sunburn.config.wetStopsBurn")
    @Config.Comment("The player being wet stops the burn. (Rain and water stops burning)")
    public static boolean wetStopsBurn = true;

    @Config.LangKey("sunburn.config.burnylevel")
    @Config.Comment("If the player is below this Y level they will not burn, If they are above it they burn. This setting only works if playerMustSeeSky is false!")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int burnOverYLevel = 64;

    @Config.LangKey("sunburn.config.burnovery")
    @Config.Comment("Should players always burn over a certain Y level? This setting only works if playerMustSeeSky is false!")
    public static boolean alwaysBurnOverYLevel = true;

    @Config.LangKey("sunburn.config.armorblocksburn")
    @Config.Comment("Should players always burn over a certain Y level? This setting only works if playerMustSeeSky is false!")
    public static boolean fullArmorBlocksBurn = false;

    @Config.LangKey("sunburn.config.hatblocksburn")
    @Config.Comment("Should players always burn over a certain Y level? This setting only works if playerMustSeeSky is false!")
    public static boolean hatsBlockBurn = true;

    @Config.LangKey("sunburn.config.safetime")
    @Config.Comment("How long to wait in ticks before players can burn after spawning into the world, This includes logging in. 20 ticks = 1 second, 1200 ticks = 1 minute, 72000 ticks = 1 hour")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int waitToBurnTime = 600;

    @Config.LangKey("sunburn.config.bypassfireresist")
    @Config.Comment("Changes the damage type to bypass fire resist potions/enchants.")
    public static boolean bypassFireResist = false;

    @Config.LangKey("sunburn.config.bypassdamage")
    @Config.Comment("How much damage the player takes from bypass damage.")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
    public static int bypassDamge = 1;

    @Config.LangKey("sunburn.config.dimlist")
    @Config.Comment("Dimension list, whitelistOrBlacklist changes how this works!")
    public static int[] dimList = new int[1];

    static
    {
        dimList[0] = -1;
    }

    @Config.LangKey("sunburn.config.whitelistorblacklist")
    @Config.Comment("Changes how dimList is treated, True = Whitelist, False = Blacklist.")
    public static boolean whitelistOrBlacklist = false;
}
