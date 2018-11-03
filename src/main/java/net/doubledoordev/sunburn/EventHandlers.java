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
    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        long time = player.getEntityWorld().getWorldTime() % lengthOfDay;
        DamageSource damageSource = new DamageSource("sunburn").setDamageBypassesArmor().setDifficultyScaled();

        if (!player.isCreative() && !player.isSpectator() &&
                player.ticksExisted >= waitToBurnTime &&
                time >= burnTimeStart && time <= burnTimeStop)
        {
            if (whitelistOrBlacklist)
            {
                for (int dimList : ModConfig.dimList)
                {
                    if (player.dimension == dimList)
                    {
                        if (hatsBlockBurn && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemArmor)
                            return;

                        if (fullArmorBlocksBurn && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemArmor &&
                                player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemArmor &&
                                player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemArmor &&
                                player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemArmor)
                            return;

                        if (playerMustSeeSky && player.world.canSeeSky(player.getPosition()))
                        {
                            if (bypassFireResist)
                            {
                                player.attackEntityFrom(damageSource, bypassDamge);
                                player.setFire(1);
                            }
                            else player.setFire(lengthOfBurn);
                        }
                        else
                        {
                            if (alwaysBurnOverYLevel && player.getPosition().getY() >= burnOverYLevel)
                            {
                                if (bypassFireResist)
                                {
                                    player.attackEntityFrom(damageSource, bypassDamge);
                                    player.setFire(1);
                                }
                                else player.setFire(lengthOfBurn);
                            }
                        }
                    }
                }
            }
            else
            {
                for (int dimList1 : ModConfig.dimList)
                {
                    if (player.dimension != dimList1)
                    {
                        if (hatsBlockBurn && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemArmor)
                            return;

                        if (fullArmorBlocksBurn && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemArmor &&
                                player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemArmor &&
                                player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemArmor &&
                                player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemArmor)
                            return;

                        if (playerMustSeeSky && player.world.canSeeSky(player.getPosition()))
                        {
                            if (bypassFireResist)
                            {
                                player.attackEntityFrom(damageSource, bypassDamge);
                                player.setFire(1);
                            }
                            else player.setFire(lengthOfBurn);
                        }
                        else
                        {
                            if (alwaysBurnOverYLevel && player.getPosition().getY() >= burnOverYLevel)
                            {
                                if (bypassFireResist)
                                {
                                    player.attackEntityFrom(damageSource, bypassDamge);
                                    player.setFire(1);
                                }
                                else player.setFire(lengthOfBurn);
                            }
                        }
                    }
                }
            }
        }
    }
}
