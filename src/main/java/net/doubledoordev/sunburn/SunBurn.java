package net.doubledoordev.sunburn;

import net.doubledoordev.sunburn.data.BurnRulesData;
import net.doubledoordev.sunburn.data.BurnRulesValidator;
import net.doubledoordev.sunburn.data.DataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;

@Mod("${modID}")
public class SunBurn {
    public static final String MOD_ID = "${modID}";
    // MessageID is empty as the mod will run only on the server resulting in the client not having any translation keys.
    BurnDamageSource damageSource = new BurnDamageSource("");

    public SunBurn() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SunBurnConfig.spec);
    }

    @SubscribeEvent
    public void dataLoader(AddReloadListenerEvent event) {
        event.addListener(new DataManager("burn_data"));
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        // Need to filter the client because we run on the server, we also want to apply everything once so ignore the end phase.
        boolean isServerStartPhase = event.side.isClient() || event.phase == TickEvent.Phase.END;

        if (isServerStartPhase)
            return;

        Player player = event.player;
        ResourceLocation targetDimDataLocation = player.level.dimension().location();
        int dayCount = (int) (player.level.getDayTime() / SunBurnConfig.SERVER.lengthOfDay.get() % 2147483647L);

        for (Map.Entry<String, BurnRulesData> burnData : DataManager.rulesTable.row(targetDimDataLocation).entrySet()) {
            String dayString = burnData.getKey();
            BurnRulesData burnRules = burnData.getValue();

            if (BurnRulesValidator.isValidDay(targetDimDataLocation, dayString, dayCount) &&
                    inValidBiome(burnRules, player) && isTargetUnsafe(burnRules, player)) {

                if (player.getY() > burnRules.alwaysBurnAboveYLevel()) {
                    damagePlayer(burnRules, player);
                    return;
                }

                if (isInBurningLight(burnRules, player)) {
                    ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);

                    if (burnRules.fullArmorToBlockBurn()) {
                        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
                        ItemStack legStack = player.getItemBySlot(EquipmentSlot.LEGS);
                        ItemStack feetStack = player.getItemBySlot(EquipmentSlot.FEET);

                        if (headStack.is(TagKeys.SUN_BLOCKING_ITEMS) && chestStack.is(TagKeys.SUN_BLOCKING_ITEMS) &&
                                legStack.is(TagKeys.SUN_BLOCKING_ITEMS) && feetStack.is(TagKeys.SUN_BLOCKING_ITEMS)) {
                            damageGearRandomly(burnRules, player, headStack, EquipmentSlot.HEAD);
                            damageGearRandomly(burnRules, player, chestStack, EquipmentSlot.CHEST);
                            damageGearRandomly(burnRules, player, legStack, EquipmentSlot.LEGS);
                            damageGearRandomly(burnRules, player, feetStack, EquipmentSlot.FEET);
                            return;
                        } else damagePlayer(burnRules, player);
                    }

                    if (headStack.is(TagKeys.SUN_BLOCKING_ITEMS)) {
                        damageGearRandomly(burnRules, player, headStack, EquipmentSlot.HEAD);
                        return;
                    } else damagePlayer(burnRules, player);
                }

            }

        }
    }

    private boolean inValidBiome(BurnRulesData burnRules, Player player) {
        if (player.level.getBiome(player.blockPosition()).is(TagKeys.ALWAYS_SAFE_BIOMES))
            return false;

        if (burnRules.biomeList().isEmpty())
            return true;

        for (ResourceLocation biomeResource : burnRules.biomeList()) {
            if (player.level.getBiome(player.blockPosition()).is(biomeResource)) {
                return true;
            }
        }
        return false;
    }


    private boolean isTargetUnsafe(BurnRulesData burnRules, Player player) {
        boolean isProtectedGameMode = player.isCreative() || player.isSpectator();
        if (isProtectedGameMode)
            return false;

        long time = player.level.getDayTime() % SunBurnConfig.SERVER.lengthOfDay.get();

        boolean isMoistAndSafe = burnRules.wetStopsBurn() && player.isInWaterRainOrBubble();
        boolean isSnowyAndSafe = burnRules.powderSnowStopsBurn() && (player.isInPowderSnow || player.wasInPowderSnow);
        boolean isPlayerJoinProtected = player.tickCount <= burnRules.loadingSafeTime();
        boolean withinTimeOfDayToBurn = time <= burnRules.startTime() || time >= burnRules.endTime();
        boolean isYSafe = player.getY() < burnRules.alwaysSafeBelowYLevel();


        return !isMoistAndSafe && !isSnowyAndSafe && !isPlayerJoinProtected && !withinTimeOfDayToBurn && !isYSafe;
    }

    private boolean isInBurningLight(BurnRulesData burnRules, Player player) {
        BlockPos playerPos = player.blockPosition();

        int blockLightLevel = player.level.getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(playerPos);
        int skyLightLevel = player.level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(playerPos);

        return blockLightLevel >= burnRules.blockLightBurnLevel() || skyLightLevel >= burnRules.skyLightBurnLevel();
    }

    private void damagePlayer(BurnRulesData burnData, Player player) {
        if (burnData.scalesWithDifficulty()) {
            if (burnData.ignoreArmor() && burnData.ignoreMagic())
                player.hurt(damageSource.customMessage(burnData.deathMessage()).bypassArmor().bypassMagic().setScalesWithDifficulty(), burnData.burnDamage());
            else if (burnData.ignoreMagic())
                player.hurt(damageSource.customMessage(burnData.deathMessage()).bypassMagic().setScalesWithDifficulty(), burnData.burnDamage());
            else if (burnData.ignoreArmor())
                player.hurt(damageSource.customMessage(burnData.deathMessage()).bypassArmor().setScalesWithDifficulty().setIsFire(), burnData.burnDamage());
            else
                player.hurt(damageSource.customMessage(burnData.deathMessage()).setScalesWithDifficulty().setIsFire(), burnData.burnDamage());
        } else {
            if (burnData.ignoreArmor() && burnData.ignoreMagic())
                player.hurt(damageSource.customMessage(burnData.deathMessage()).bypassArmor().bypassMagic(), burnData.burnDamage());
            else if (burnData.ignoreMagic())
                player.hurt(damageSource.customMessage(burnData.deathMessage()).bypassMagic(), burnData.burnDamage());
            else if (burnData.ignoreArmor())
                player.hurt(damageSource.customMessage(burnData.deathMessage()).bypassArmor().setIsFire(), burnData.burnDamage());
            else
                player.hurt(damageSource.customMessage(burnData.deathMessage()).setIsFire(), burnData.burnDamage());
        }
        player.setSecondsOnFire(burnData.lengthOfBurn());
    }

    private void damageGearRandomly(BurnRulesData burnRules, Player player, ItemStack stack, EquipmentSlot equipmentSlot) {
        if (burnRules.damageEquippedGear() && player.level.random.nextFloat() * 30.0F < (player.getBrightness() - 0.4F) * 2.0F)
            stack.hurtAndBreak(1, player, (player1 -> player1.broadcastBreakEvent(equipmentSlot)));
    }
}
