package net.doubledoordev.sunburn;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import org.apache.commons.lang3.tuple.Pair;

public class SunBurnConfig {

    public static final SunBurnConfig.Server SERVER;
    static final ForgeConfigSpec spec;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new Builder().configure(SunBurnConfig.Server::new);
        spec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Server {
        public ForgeConfigSpec.LongValue lengthOfDay;

        Server(Builder builder) {
            builder.push("Sunburn Settings");

            builder.comment("Most settings are done via datapacks! The following settings are special global settings that apply to all rules.");

            lengthOfDay = builder
                    .comment("How long a day is in ticks. DO NOT CHANGE THIS UNLESS YOU USE A MOD TO CHANGE THE LENGTH OF DAY!")
                    .translation("sunburn.config.lengthOfDay")
                    .defineInRange("lengthOfDay", 24000, 0, Long.MAX_VALUE);


            builder.pop();

        }
    }
}
