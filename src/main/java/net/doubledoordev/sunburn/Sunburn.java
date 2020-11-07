package net.doubledoordev.sunburn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("sunburn")
public class Sunburn
{
    DamageSource damageSource = new DamageSource("sunburn").setDamageBypassesArmor().setDifficultyScaled();
    private static final Logger LOGGER = LogManager.getLogger();
    int tickCounterToStopLogSpam = 100;

    public Sunburn()
    {
        MinecraftForge.EVENT_BUS.register(Sunburn.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SunBurnConfig.spec);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
    {
        tickCounterToStopLogSpam++;

        PlayerEntity player = event.player;
        String dimResourceLocation = player.world.func_234923_W_().func_240901_a_().toString();
        long time = player.getEntityWorld().getDayTime();

        if (event.side.isClient() | event.phase == TickEvent.Phase.END) // Need to filter these out cause they cause issues.
            return;

        if (SunBurnConfig.GENERAL.wetStopsBurn.get() && player.isWet())
            return;

        if (player.isCreative() || player.isSpectator() || // If the player isn't in creative or spectator.
                player.ticksExisted <= SunBurnConfig.GENERAL.waitToBurnTime.get() ||     // If the player entity is younger than the safe wait time.
                time <= SunBurnConfig.GENERAL.burnTimeStart.get() || time >= SunBurnConfig.GENERAL.burnTimeStop.get()) // If the current time is below the burn start or the time is after the burn stop.
            return;

        // if hats block burn and the player is wearing one, ignore the burn.
        if (SunBurnConfig.GENERAL.hatsBlockBurn.get() && player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof ArmorItem)
            return;

        // if full armor blocks burn and the player is wearing something in each slot that is armor, ignore the burn.
        if (SunBurnConfig.GENERAL.fullArmorBlocksBurn.get() && player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof ArmorItem &&
                player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ArmorItem &&
                player.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ArmorItem &&
                player.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ArmorItem)
            return;

        // This only exists to keep the log spam down.
        if (SunBurnConfig.GENERAL.debug.get() && tickCounterToStopLogSpam > 100)
        {
            LOGGER.info(player.getDisplayName() + " is in Dim: [" + dimResourceLocation + "] To disable set debug to false in the config. Copy paste the text inside the [] to the dimlist to effect/block this dim.");
            tickCounterToStopLogSpam = 0;
        }

        if (SunBurnConfig.GENERAL.whitelistOrBlacklist.get()) // Check whitelist/blacklist state. True = Whitelist
        {
            // Whitelist methods. (Damage only in)
            if (SunBurnConfig.GENERAL.dimList.get().contains(dimResourceLocation)) // is the player in this dim?
                damageConditionCheck(player);
        }
        else
        {
            // Blacklist method. (Damage everything but)
            if (!SunBurnConfig.GENERAL.dimList.get().contains(dimResourceLocation)) // is the player not in this dim?
                damageConditionCheck(player);
        }
    }

    private void damageConditionCheck(PlayerEntity player)
    {
        // Does the player need to see they sky or do we always burn over a Y level?
        if (SunBurnConfig.GENERAL.playerMustSeeSky.get() && player.world.canBlockSeeSky(player.func_233580_cy_()) | SunBurnConfig.GENERAL.alwaysBurnOverYLevel.get() &&
                player.func_233580_cy_().getY() >= SunBurnConfig.GENERAL.burnOverYLevel.get())
        {
            damagePlayer(player); // apply damage accordingly.
        }
    }

    private void damagePlayer(PlayerEntity player)
    {
        if (SunBurnConfig.GENERAL.bypassFireResist.get()) // Should we bypass the fire resist effect?
        {
            // if so damage them with our damage.
            player.attackEntityFrom(damageSource, SunBurnConfig.GENERAL.bypassDamge.get());
            player.setFire(1);
        }
        else player.setFire(SunBurnConfig.GENERAL.lengthOfBurn.get()); //otherwise use a regular burn.
    }
}
