![https://www.curseforge.com/minecraft/mc-mods/sun-burn](https://img.shields.io/badge/CurseForge-%23F16436?logo=curseforge&logoColor=white&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Fsun-burn
)
![https://discord.gg/M5uRF25](https://img.shields.io/badge/DoubleDoorDevelopment-%235865F2?logo=discord&logoColor=white&link=https%3A%2F%2Fdiscord.gg%2FM5uRF25
)

# Sun-Burn 1.18.X

A Minecraft Mod all about spontaneous combustion!

## Features

* Control the start & stop time for the burning.
* Limit the burning to specific dimensions.
* Control what blocks the burning.
* Burn based off the sky or block light level!
* Special damage type to prevent fire resistance from working.
* Prevents instant death from burning before connecting/loading.
* Totally customizable with datapacks.

## Custom Data / Burn Rules

Burn rule datapacks are a highly configurable system for when to apply the burning to the player.
Rules are written per dimension and can be specific to a biome. Internally the rules for a dimension
are all merged. Thus, you can have multiple files for the same dimension.

All the data is also heavily validated with helpful errors in the log file. Always make sure to read the log
file if the game complains about datapack errors or the mod stops working.

Files should be placed in `data\sunburn\burn_data`

This mod also uses the following tags:

* `#sunburn:blocks_sun`|`data\sunburn\tags\items\blocks_sun.json` for items that will make the player safe.
* `#sunburn:always_safe_biomes`|`data\sunburn\tags\worldgen\biome\always_safe.json` for biomes that are always safe
  regardless of rules.

Here is an example file.

```json
{
  "dimension": "minecraft:overworld",
  "biomes": [
    "minecraft:desert"
  ],
  "burnDays": ">0",
  "startTime": 1000,
  "endTime": 15000,
  "loadingSafeTime": 600,
  "lengthOfBurn": 1,
  "ignoreMagic": true,
  "ignoreArmor": true,
  "scalesWithDifficulty": true,
  "burnDamage": 1,
  "skyLightBurnLevel": 0,
  "blockLightBurnLevel": 0,
  "alwaysSafeBelowYLevel": 64,
  "alwaysBurnAboveYLevel": 120,
  "damageEquippedGear": true,
  "wetStopsBurn": true,
  "powderSnowStopsBurn": true,
  "fullArmorToBlockBurn": false
}
```

Requirements and information for a valid data file.

* `"dimension": ""`
  * Required: No, Default: `minecraft:overworld`
  * A valid Resource name for the dimension to apply the rules to. Use F3 to get names for this setting.
* `"biomes": []`
  * Required: No, Defaults: All biomes.
  * A list of valid Resource names for the biomes these rules apply to. Use F3 to get names for this setting.
* `"burnDays": ""`
  * Required: No, Default: `>0` (Every day)
  * A string of comma (`,`) separated days or day ranges to apply this rule to.
    * String may only contain `0-9`, `>`, `<`, `-` and `,`
    * `>` for days above this number.
    * `<` for days below this number.
    * `-` for a range of days between X and Y.
    * Example: `1,5-10,>15` Burn on the first day, 5th to 10th day and every day after 15th
* `"startTime":`
  * Required: Yes
  * Time of the day in ticks to start. Normal day length is 0 to 23999.
* `"endTime":`
  * Required: Yes
  * Time of the day in ticks to stop. Normal day length is 0 to 23999.
* `"loadingSafeTime":`
  * Required: Yes
  * Time in ticks to prevent a player from burning. This is applied when the player is logging in or switching
    dimensions. 20 ticks = 1 second, 1200 ticks = 1 minute, 72000 ticks = 1 hour
* `"lenthOfBurn":`
  * Require: Yes
  * How long the player will be lit on fire for in seconds after they are safe.
* `"ignoreMagic": boolean`
  * Required: No, Default: False
  * Makes the damage applied to ignore magic (Potions and Enchants).
* `"ignoreArmor": boolean`
  * Required: No, Default: False
  * Makes the damage applied to ignore equipped armor reduction.
* `"scalesWithDifficulty": boolean`
  * Required: No, Default: False
  * Makes the damage scale with the difficulty of the game. Peaceful: No damage, Easy: Min(Damage / 2 + 1, Damage),
    Normal: No Change, Hard: Damage * 3 / 2
* `"burnDamage":`
  * Required: Yes
  * The Normal amount of damage to hit the player for, this is not the same as fire damage after they are safe. Can be
    whole or decimal numbers.
* `"skyLightBurnLevel":`
  * Required: Yes
  * Minimum skylight level to burn the player. Numbers greater than 16 will entirely prevent burning. Use F3 to get
    numbers for this setting.
* `"blockLightBurnLevel":`
  * Required: Yes
  * Minimum blocklight level to burn the player. Numbers greater than 16 will entirely prevent burning. Use F3 to get
    numbers for this setting.
* `"alwaysSafeBelowYLevel":`
  * Required: Yes
  * When the player is below this Y level they will be entirely safe from this burn rule.
* `"alwaysBurnAboveYlevel":`
  * Required: Yes
  * When the player is above this Y Level they will always be affected by this burn rule.
* `"damageEquipedGear": boolean`
  * Required: No, Default: False
  * Should the items the player is wearing be damaged (if possible) when blocking the sun. The higher the light level
    from the trigger point the more likely damage will be done. Only items protecting from the sun can be damaged, what
    one is picked randomly.
* `"wetStopsBurn: boolean`
  * Required: No, Default: True
  * When the player is wet of any type (Rain, Water, Bubbles) they are safe from burning until dry again.
* `"powderSnowStopsBurn": boolean`
  * Required: No, Default: True
  * When the player is affect by or inside powdered snow they are safe from burning until the effect is gone.
* `"fullArmorToBlockBurn": boolean`
  * Required: No, Default: True
  * If the player is required to wear a Head, Chest, Legs and Feet parts of armor to block burning. Otherwise, anything
    in the `#sunburn:blocks_sun` Item tag will make the player safe.

## Developers

This project uses a highly customized buildscript to apply variables and other build parameters.
It also handles generating mod metadata files for the mod thus they should not be added to the project itself.
