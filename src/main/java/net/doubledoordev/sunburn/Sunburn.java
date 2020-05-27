package net.doubledoordev.sunburn;

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

    public Sunburn()
    {
        MinecraftForge.EVENT_BUS.register(Sunburn.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SunBurnConfig.spec);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
    {
        PlayerEntity player = event.player;
        long time = player.getEntityWorld().getGameTime() % SunBurnConfig.GENERAL.lengthOfDay.get();

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

        for (int dim : SunBurnConfig.GENERAL.dimList.get()) // for each dim in the list.
        {
            if (SunBurnConfig.GENERAL.whitelistOrBlacklist.get()) // Check whitelist/blacklist state. True = Whitelist
            {
                // Whitelist methods. (Damage only in)
                if (player.dimension.getId() == dim) // is the player in this dim?
                    damageConditionCheck(player);
            }
            else
            {
                // Blacklist method. (Damage everything but)
                if (player.dimension.getId() != dim) // is the player not in this dim?
                    damageConditionCheck(player);
            }
        }
    }

    private void damageConditionCheck(PlayerEntity player)
    {
        // Does the player need to see they sky or do we always burn over a Y level?
        if (SunBurnConfig.GENERAL.playerMustSeeSky.get() && player.world.canBlockSeeSky(player.getPosition()) | SunBurnConfig.GENERAL.alwaysBurnOverYLevel.get() &&
                player.getPosition().getY() >= SunBurnConfig.GENERAL.burnOverYLevel.get())
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
