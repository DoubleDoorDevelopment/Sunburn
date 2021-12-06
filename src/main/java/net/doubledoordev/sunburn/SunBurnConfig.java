package net.doubledoordev.sunburn;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class SunBurnConfig
{

    public static final SunBurnConfig.General GENERAL;
    static final ForgeConfigSpec spec;

    static
    {
        final Pair<General, ForgeConfigSpec> specPair = new Builder().configure(SunBurnConfig.General::new);
        spec = specPair.getRight();
        GENERAL = specPair.getLeft();
    }

    public static class General
    {
        public static List<? extends String> dimList()
        {
            ArrayList<String> list = new ArrayList<>();
            list.add("minecraft:the_nether");

            return list;
        }

        public IntValue burnTimeStart;
        public IntValue burnTimeStop;
        public IntValue lengthOfBurn;
        public IntValue burnOverYLevel;
        public IntValue waitToBurnTime;
        public IntValue bypassDamage;
        public IntValue burnLightLevel;

        public BooleanValue playerMustSeeSky;
        public BooleanValue wetStopsBurn;
        public BooleanValue alwaysBurnOverYLevel;
        public BooleanValue fullArmorBlocksBurn;
        public BooleanValue hatsBlockBurn;
        public BooleanValue bypassFireResist;
        public BooleanValue whitelistOrBlacklist;
        public BooleanValue burnInLight;
        public BooleanValue damageEquippedGear;
        public BooleanValue debug;

        public ConfigValue<List<? extends String>> dimList;

        General(Builder builder)
        {
            builder.comment("General configuration settings")
                    .push("General");

            builder.push("Burn Time");

            burnTimeStart = builder
                    .comment("Time of day burning starts in ticks. Normal day length is 0 to 23999.")
                    .translation("sunburn.config.burntime.start")
                    .defineInRange("burnTimeStart", 1000, 0, Integer.MAX_VALUE);

            burnTimeStop = builder
                    .comment("Time of day burning stops in ticks. Normal day length is 0 to 23999.")
                    .translation("sunburn.config.burntime.stop")
                    .defineInRange("burnTimeStop", 15000, 0, Integer.MAX_VALUE);

            builder.pop();
            builder.push("Burn Damage Options");

            lengthOfBurn = builder
                    .comment("How long a player is set to burn for. This is how long the player will burn AFTER they are safe.")
                    .translation("sunburn.config.burnlength")
                    .defineInRange("lengthOfBurn", 1, 0, Integer.MAX_VALUE);

            bypassDamage = builder
                    .comment("How much damage the player takes from bypass damage.")
                    .translation("sunburn.config.bypassdamage")
                    .defineInRange("bypassDamage", 1, 0, Integer.MAX_VALUE);

            bypassFireResist = builder
                    .comment("Changes the damage type to bypass fire resist potions/enchants.")
                    .translation("sunburn.config.bypassfireresist")
                    .define("bypassFireResist", false);

            playerMustSeeSky = builder
                    .comment("The player only burns if they see the sky.")
                    .translation("sunburn.config.playermustseesky")
                    .define("playerMustSeeSky", true);

            alwaysBurnOverYLevel = builder
                    .comment("Should players always burn over a certain Y level? This setting only works if playerMustSeeSky is false!")
                    .translation("sunburn.config.burnovery")
                    .define("alwaysBurnOverYLevel", true);

            burnInLight = builder
                    .comment("If light levels will be used to burn people.")
                    .translation("sunburn.config.burnInLight")
                    .define("burnInLight", false);

            damageEquippedGear = builder
                    .comment("Should the armor that's blocking the sun take damage from the sun if it can.")
                    .translation("sunburn.config.damageEquippedGear")
                    .define("damageEquippedGear", false);

            builder.pop();
            builder.push("Safety Options");

            burnOverYLevel = builder
                    .comment("If the player is below this Y level they will not burn, If they are above it they burn. This setting only works if playerMustSeeSky is false!")
                    .translation("sunburn.config.burnylevel")
                    .defineInRange("burnOverYLevel", 64, Integer.MAX_VALUE, Integer.MAX_VALUE);

            dimList = builder
                    .comment("Dimension list, whitelistOrBlacklist changes how this works!")
                    .translation("sunburn.config.dimlist")
                    .defineList("dimBlacklist", SunBurnConfig.General.dimList(), p -> p instanceof String);

            waitToBurnTime = builder
                    .comment("How long to wait in ticks before players can burn after spawning into the world, This includes logging in. 20 ticks = 1 second, 1200 ticks = 1 minute, 72000 ticks = 1 hour")
                    .translation("sunburn.config.safetime")
                    .defineInRange("waitToBurnTime", 600, 0, Integer.MAX_VALUE);

            wetStopsBurn = builder
                    .comment("The player being wet stops the burn. (Rain and water stops burning)")
                    .translation("sunburn.config.wetStopsBurn")
                    .define("wetStopsBurn", true);

            fullArmorBlocksBurn = builder
                    .comment("Should the player be required to wear a full set of armor to block the burning? (Head, Chest, Legs, Feet)")
                    .translation("sunburn.config.armorblocksburn")
                    .define("fullArmorBlocksBurn", false);

            hatsBlockBurn = builder
                    .comment("Do just head items block the burning?")
                    .translation("sunburn.config.hatblocksburn")
                    .define("hatsBlockBurn", true);

            burnLightLevel = builder
                    .comment("Lowest block light level that players will start to burn in at any Y level. Must enable via burnInLight. IGNORES SKY LIGHT!")
                    .translation("sunburn.config.burnLightLevel")
                    .defineInRange("burnLightLevel", 15, 0, 15);

            whitelistOrBlacklist = builder
                    .comment("Changes how dimList is treated, True = Whitelist, False = Blacklist.")
                    .translation("sunburn.config.whitelistorblacklist")
                    .define("whitelistOrBlacklist", false);

            builder.pop();
            builder.push("Debug");

            debug = builder
                    .comment("Enable Dim to console output for getting ID's.")
                    .translation("sunburn.config.debug")
                    .define("debug", false);

            builder.pop();
        }
    }
}