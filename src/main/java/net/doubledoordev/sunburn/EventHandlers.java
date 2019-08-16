package net.doubledoordev.sunburn;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static net.doubledoordev.sunburn.ModConfig.*;

public class EventHandlers
{
    DamageSource damageSource = new DamageSource("sunburn").setDamageBypassesArmor().setDifficultyScaled();

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        long time = player.getEntityWorld().getWorldTime() % lengthOfDay;

        if (event.side.isClient() | event.phase == TickEvent.Phase.END) // Need to filter these out cause they cause issues.
            return;

        if (wetStopsBurn && player.isWet())
            return;

        if (player.isCreative() || player.isSpectator() || // If the player isn't in creative or spectator.
                player.ticksExisted <= waitToBurnTime ||     // If the player entity is younger than the safe wait time.
                time <= burnTimeStart || time >= burnTimeStop) // If the current time is below the burn start or the time is after the burn stop.
            return;

        // if hats block burn and the player is wearing one, ignore the burn.
        if (hatsBlockBurn && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemArmor)
            return;

        // if full armor blocks burn and the player is wearing something in each slot that is armor, ignore the burn.
        if (fullArmorBlocksBurn && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemArmor &&
                player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemArmor &&
                player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemArmor &&
                player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemArmor)
            return;

        for (int dimList : ModConfig.dimList) // for each dim in the list.
        {
            if (whitelistOrBlacklist) // Check whitelist/blacklist state. True = Whitelist
            {
                // Whitelist methods. (Damage only in)
                if (player.dimension == dimList) // is the player in this dim?
                    damageConditionCheck(player);
            }
            else
            {
                // Blacklist method. (Damage everything but)
                if (player.dimension != dimList) // is the player not in this dim?
                    damageConditionCheck(player);
            }
        }
    }

    private void damageConditionCheck(EntityPlayer player)
    {
        // Does the player need to see they sky or do we always burn over a Y level?
        if (playerMustSeeSky && player.world.canSeeSky(player.getPosition()) | alwaysBurnOverYLevel && player.getPosition().getY() >= burnOverYLevel)
        {
            damagePlayer(player); // apply damage accordingly.
        }
    }

    private void damagePlayer(EntityPlayer player)
    {
        if (bypassFireResist) // Should we bypass the fire resist effect?
        {
            // if so damage them with our damage.
            player.attackEntityFrom(damageSource, bypassDamge);
            player.setFire(1);
        }
        else player.setFire(lengthOfBurn); //otherwise use a regular burn.
    }
}
