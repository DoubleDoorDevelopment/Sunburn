package net.doubledoordev.sunburn;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("${modID}")
public class SunBurn {
    DamageSource damageSource = new DamageSource("sunburn").bypassArmor().setScalesWithDifficulty();
    private static final Logger LOGGER = LogManager.getLogger();
    int tickCounterToStopLogSpam = 100;

    public SunBurn() {
        MinecraftForge.EVENT_BUS.register(SunBurn.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SunBurnConfig.spec);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        tickCounterToStopLogSpam++;

        Player player = event.player;
        String dimResourceLocation = player.level.dimension().location().toString();
        long time = player.level.getDayTime() % 24000L;

        if (event.side.isClient() | event.phase == TickEvent.Phase.END) // Need to filter these out cause they cause issues.
            return;

        if (SunBurnConfig.GENERAL.wetStopsBurn.get() && player.isInWaterRainOrBubble() |
                SunBurnConfig.GENERAL.powderSnowStopsBurn.get() && (player.isInPowderSnow || player.wasInPowderSnow))
            return;

        if (player.isCreative() || player.isSpectator() || // If the player isn't in creative or spectator.
                player.tickCount <= SunBurnConfig.GENERAL.waitToBurnTime.get() ||     // If the player entity is younger than the safe wait time.
                time <= SunBurnConfig.GENERAL.burnTimeStart.get() || time >= SunBurnConfig.GENERAL.burnTimeStop.get()) // If the current time is below the burn start or the time is after the burn stop.
            return;

        ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legStack = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feetStack = player.getItemBySlot(EquipmentSlot.FEET);

        // if hats block burn and the player is wearing one, ignore the burn.
        if (SunBurnConfig.GENERAL.hatsBlockBurn.get() && headStack.getItem() instanceof ArmorItem) {
            damageGearRandomly(player, headStack, EquipmentSlot.HEAD);
            return;
        }

        // if full armor blocks burn and the player is wearing something in each slot that is armor, ignore the burn.
        if (SunBurnConfig.GENERAL.fullArmorBlocksBurn.get() && player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem &&
                player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem &&
                player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ArmorItem) {
            damageGearRandomly(player, headStack, EquipmentSlot.HEAD);
            damageGearRandomly(player, chestStack, EquipmentSlot.CHEST);
            damageGearRandomly(player, legStack, EquipmentSlot.LEGS);
            damageGearRandomly(player, feetStack, EquipmentSlot.FEET);

            return;
        }

        // This only exists to keep the log spam down.
        if (SunBurnConfig.GENERAL.debug.get() && tickCounterToStopLogSpam > 100) {
            LOGGER.info(player.getDisplayName() + " is in Dim: [" + dimResourceLocation + "] To disable set debug to false in the config. Copy paste the text inside the [] to the dimlist to effect/block this dim.");
            tickCounterToStopLogSpam = 0;
        }

        if (SunBurnConfig.GENERAL.whitelistOrBlacklist.get()) // Check whitelist/blacklist state. True = Whitelist
        {
            // Whitelist methods. (Damage only in)
            if (SunBurnConfig.GENERAL.dimList.get().contains(dimResourceLocation)) // is the player in this dim?
                damageConditionCheck(player);
        } else {
            // Blacklist method. (Damage everything but)
            if (!SunBurnConfig.GENERAL.dimList.get().contains(dimResourceLocation)) // is the player not in this dim?
                damageConditionCheck(player);
        }
    }

    private void damageConditionCheck(Player player) {
        BlockPos playerPos = player.blockPosition();
        LayerLightEventListener blockLightingLayer = player.level.getLightEngine().getLayerListener(LightLayer.BLOCK);

        // If we need to check for Y level burn.
        if (SunBurnConfig.GENERAL.alwaysBurnOverYLevel.get()) {
            // Check Y level requirement.
            if (playerPos.getY() >= SunBurnConfig.GENERAL.burnOverYLevel.get()) {
                // Check if they need to see the sky to burn. If they do not, Burn them.
                if (SunBurnConfig.GENERAL.playerMustSeeSky.get()) {
                    // If they can see sky burn them.
                    if (player.level.canSeeSky(playerPos)) {
                        damagePlayer(player);
                    }
                } else damagePlayer(player);
            }
        }
        // Do sky burns if they see sky and don't care about Y.
        else if (SunBurnConfig.GENERAL.playerMustSeeSky.get() && player.level.canSeeSky(playerPos))
            damagePlayer(player);

        if (SunBurnConfig.GENERAL.burnInLight.get() && blockLightingLayer.getLightValue(playerPos) >= SunBurnConfig.GENERAL.burnLightLevel.get())
            damagePlayer(player);
    }

    private void damagePlayer(Player player) {
        if (SunBurnConfig.GENERAL.bypassFireResist.get()) // Should we bypass the fire resist effect?
        {
            // if so damage them with our damage.
            player.hurt(damageSource, SunBurnConfig.GENERAL.bypassDamage.get());
            player.setSecondsOnFire(1);
        } else player.setSecondsOnFire(SunBurnConfig.GENERAL.lengthOfBurn.get()); //otherwise use a regular burn.
    }

    private void damageGearRandomly(Player player, ItemStack stack, EquipmentSlot equipmentSlot) {
        if (SunBurnConfig.GENERAL.damageEquippedGear.get() && player.level.random.nextFloat() * 30.0F < (player.getBrightness() - 0.4F) * 2.0F)
            stack.hurtAndBreak(1, player, (player1 -> player1.broadcastBreakEvent(equipmentSlot)));
    }
}
