package net.doubledoordev.sunburn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.lighting.IWorldLightListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("sunburn")
public class Sunburn
{
    DamageSource damageSource = new DamageSource("sunburn").bypassArmor().setScalesWithDifficulty();
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
        String dimResourceLocation = player.level.dimension().location().toString();
        long time = player.level.getDayTime();

        if (event.side.isClient() | event.phase == TickEvent.Phase.END) // Need to filter these out cause they cause issues.
            return;

        if (player.isCreative() || player.isSpectator() || // If the player isn't in creative or spectator.
                player.tickCount <= SunBurnConfig.GENERAL.waitToBurnTime.get() ||     // If the player entity is younger than the safe wait time.
                time <= SunBurnConfig.GENERAL.burnTimeStart.get() || time >= SunBurnConfig.GENERAL.burnTimeStop.get()) // If the current time is below the burn start or the time is after the burn stop.
            return;

        ItemStack headStack = player.getItemBySlot(EquipmentSlotType.HEAD);
        ItemStack chestStack = player.getItemBySlot(EquipmentSlotType.CHEST);
        ItemStack legStack = player.getItemBySlot(EquipmentSlotType.LEGS);
        ItemStack feetStack = player.getItemBySlot(EquipmentSlotType.FEET);

        // if hats block burn and the player is wearing one, ignore the burn.
        if (SunBurnConfig.GENERAL.hatsBlockBurn.get() && headStack.getItem() instanceof ArmorItem)
        {
            damageGearRandomly(player, headStack, EquipmentSlotType.HEAD);
            return;
        }

        // if full armor blocks burn and the player is wearing something in each slot that is armor, ignore the burn.
        if (SunBurnConfig.GENERAL.fullArmorBlocksBurn.get() && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() instanceof ArmorItem &&
                player.getItemBySlot(EquipmentSlotType.CHEST).getItem() instanceof ArmorItem &&
                player.getItemBySlot(EquipmentSlotType.LEGS).getItem() instanceof ArmorItem &&
                player.getItemBySlot(EquipmentSlotType.FEET).getItem() instanceof ArmorItem)
        {
            damageGearRandomly(player, headStack, EquipmentSlotType.HEAD);
            damageGearRandomly(player, chestStack, EquipmentSlotType.CHEST);
            damageGearRandomly(player, legStack, EquipmentSlotType.LEGS);
            damageGearRandomly(player, feetStack, EquipmentSlotType.FEET);

            return;
        }

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
        BlockPos playerPos = player.blockPosition();
        IWorldLightListener blockLightingLayer = player.level.getLightEngine().getLayerListener(LightType.BLOCK);

        // Will the player burn if they see the sky?
        // Will the player burn if they are over the Y level?
        // Will the player burn if in a specific light level?
        if (SunBurnConfig.GENERAL.playerMustSeeSky.get() && player.level.canSeeSky(playerPos) |
                SunBurnConfig.GENERAL.alwaysBurnOverYLevel.get() && playerPos.getY() >= SunBurnConfig.GENERAL.burnOverYLevel.get())
            damagePlayer(player); // apply damage accordingly.


        if (SunBurnConfig.GENERAL.burnInLight.get() && blockLightingLayer.getLightValue(playerPos) >= SunBurnConfig.GENERAL.burnLightLevel.get())
            damagePlayer(player);
    }

    private void damagePlayer(PlayerEntity player)
    {
        if (SunBurnConfig.GENERAL.bypassFireResist.get()) // Should we bypass the fire resist effect?
        {
            // if so damage them with our damage.
            player.hurt(damageSource, SunBurnConfig.GENERAL.bypassDamage.get());
            player.setSecondsOnFire(1);
        }
        else player.setSecondsOnFire(SunBurnConfig.GENERAL.lengthOfBurn.get()); //otherwise use a regular burn.
    }

    private void damageGearRandomly(PlayerEntity player, ItemStack stack, EquipmentSlotType equipmentSlot)
    {
        if (SunBurnConfig.GENERAL.damageEquippedGear.get() && player.level.random.nextFloat() * 30.0F < (player.getBrightness() - 0.4F) * 2.0F)
            stack.hurtAndBreak(1, player, (player1 -> player1.broadcastBreakEvent(equipmentSlot)));
    }
}