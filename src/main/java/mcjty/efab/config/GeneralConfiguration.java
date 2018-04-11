package mcjty.efab.config;

import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {

    public static final String CATEGORY_GENERAL = "general";

    public static float baseMachineVolume = 1.0f;
    public static float baseSparksVolume = 0.7f;
    public static float baseSteamVolume = 1.0f;
    public static float baseBeepsVolume = 0.2f;
    public static int soundMachineTicks = 50;
    public static int soundSparksTicks = 25;
    public static int soundSteamTicks = 50;
    public static int soundBeepsTicks = 8;

    public static int maxSpeedupBonus = 4;
    public static int maxPipeSpeedBonus = 2;
    public static int vanillaCraftTime = 1;
    public static boolean vanillaCraftingAllowed = true;

    public static int waterSteamCraftingConsumption = 5;
    public static int waterSteamStartAmount = 500;

    public static float maxBoilerTemperature = 200;
    public static float ambientBoilerTemperature = 20;
    public static float boilerRiseTemperature = .5f;
    public static float boilerCoolTemperature = .3f;

    public static float maxSteamWheelSpeed = 20;
    public static int steamWheelBoost = 40;
    public static float steamWheelSpeedUp = 2;
    public static float steamWheelSpinDown = 0.3f;

    public static float maxCraftAnimationSpeed = 20;
    public static int craftAnimationBoost = 40;
    public static float craftAnimationSpeedUp = 3;
    public static float craftAnimationSpinDown = 0.3f;
    public static int crafterDelay = 10;

    public static int tankCapacity = 16000;
    public static int tank2Capacity = 64000;

    public static int rfControlInputPerTick = 10;
    public static int rfControlMax = 20;

    public static int rfStorageInputPerTick = 50;
    public static int rfStorageMax = 10000;
    public static int rfStorageInternalFlow = 30;
    public static int ticksAllowedWithoutRF = 1;

    public static int maxMana = 10000;
    public static int maxManaUsage = 1000;
    public static int ticksAllowedWithoutMana = 1;

    public static float maxManaRotationSpeed = 20;
    public static int manaRotationBoost = 40;
    public static float manaRotationSpeedUp = 2;
    public static float manaRotationSpinDown = 0.3f;

    public static void init(Configuration cfg) {
        baseMachineVolume = cfg.getFloat("baseMachineVolume", CATEGORY_GENERAL, baseMachineVolume,
                0.0f, 1.0f, "The volume for the machine sound (0.0 is off)");
        baseSparksVolume = cfg.getFloat("baseSparksVolume", CATEGORY_GENERAL, baseSparksVolume,
                0.0f, 1.0f, "The volume for the sparks sound (0.0 is off)");
        baseSteamVolume = cfg.getFloat("baseSteamVolume", CATEGORY_GENERAL, baseSteamVolume,
                0.0f, 1.0f, "The volume for the steam sound (0.0 is off)");
        baseBeepsVolume = cfg.getFloat("baseBeepsVolume", CATEGORY_GENERAL, baseBeepsVolume,
                0.0f, 1.0f, "The volume for the beeps sound (0.0 is off)");
        soundMachineTicks = cfg.getInt("soundMachineTicks", CATEGORY_GENERAL, soundMachineTicks,
                0, 1000000, "Amount of ticks that the gearbox sound should play");
        soundSparksTicks = cfg.getInt("soundSparksTicks", CATEGORY_GENERAL, soundSparksTicks,
                0, 1000000, "Amount of ticks that the sparks sound should play");
        soundSteamTicks = cfg.getInt("soundSteamTicks", CATEGORY_GENERAL, soundSteamTicks,
                0, 1000000, "Amount of ticks that the steam sound should play");
        soundBeepsTicks = cfg.getInt("soundBeepsTicks", CATEGORY_GENERAL, soundBeepsTicks,
                0, 1000000, "Amount of ticks that the beeps sound should play");

        maxSpeedupBonus = cfg.getInt("maxSpeedupBonus", CATEGORY_GENERAL, maxSpeedupBonus,
                1, 64, "The maximum speed bonus on a crafting operation you can get by adding multiple machine parts");
        maxPipeSpeedBonus = cfg.getInt("maxPipeSpeedBonus", CATEGORY_GENERAL, maxPipeSpeedBonus,
                1, 64, "The maximum speed bonus on a liquid crafting operation");
        vanillaCraftTime = cfg.getInt("vanillaCraftTime", CATEGORY_GENERAL, vanillaCraftTime,
                0, 1000000, "The amount of time needed for the grid/crafter to craft normal vanilla recipes");
        vanillaCraftingAllowed = cfg.getBoolean("vanillaCraftingAllowed", CATEGORY_GENERAL, vanillaCraftingAllowed,
                "If enabled the EFab grid and crafter will also support vanilla recipes. If disabled they don't");

        waterSteamCraftingConsumption = cfg.getInt("waterSteamCraftingConsumption", CATEGORY_GENERAL, waterSteamCraftingConsumption,
                0, 1000000, "Amount of water per tick that is being consumed during a steam crafting operation");
        waterSteamStartAmount = cfg.getInt("waterSteamStartAmount", CATEGORY_GENERAL, waterSteamStartAmount,
                0, 1000000, "Amount of water that is needed to be able to start a steam crafting operation");

        maxBoilerTemperature = cfg.getFloat("maxBoilerTemperature", CATEGORY_GENERAL, maxBoilerTemperature,
                100.0f, 1000.0f, "The maximum temperature for the boiler (it generates steam over 100C)");
        ambientBoilerTemperature = cfg.getFloat("ambientBoilerTemperature", CATEGORY_GENERAL, ambientBoilerTemperature,
                0.0f, 100.0f, "The ambient temperature of the boiler when no heat is below it");
        boilerRiseTemperature = cfg.getFloat("boilerRiseTemperature", CATEGORY_GENERAL, boilerRiseTemperature,
                0.0f, 100.0f, "Every tick the boiler will heat up this amount if there is a source of heat below it");
        boilerCoolTemperature = cfg.getFloat("boilerCoolTemperature", CATEGORY_GENERAL, boilerCoolTemperature,
                0.0f, 100.0f, "Every tick the boiler will cool down this amount if there is no source of heat below it");

        maxSteamWheelSpeed = cfg.getFloat("maxSteamWheelSpeed", CATEGORY_GENERAL, maxSteamWheelSpeed,
                1.0f, 1000.0f, "Maximum speed factor for the spinning wheel of the stean engine during steam crafting");
        steamWheelBoost = cfg.getInt("steamWheelBoost", CATEGORY_GENERAL, steamWheelBoost,
                1, 100000, "Number of ticks that the steam wheel will speed up during steam crafting");
        steamWheelSpeedUp = cfg.getFloat("steamWheelSpeedUp", CATEGORY_GENERAL, steamWheelSpeedUp,
                0.0f, 1000.0f, "Every tick the steam wheel will speed up during steam crafting (and during boost)");
        steamWheelSpinDown = cfg.getFloat("steamWheelSpinDown", CATEGORY_GENERAL, steamWheelSpinDown,
                0.0f, 1000.0f, "If not steam crafting this is the speed at which the speed of the wheel will decrease");

        tankCapacity = cfg.getInt("tankCapacity", CATEGORY_GENERAL, tankCapacity,
                1, 1000000, "Capacity of each tank block in MB");
        tank2Capacity = cfg.getInt("tank2Capacity", CATEGORY_GENERAL, tank2Capacity,
                1, 1000000, "Capacity of each tank (tier 2) block in MB");

        maxMana = cfg.getInt("maxMana", CATEGORY_GENERAL, maxMana,
                1, 100000000, "Maximum amount of mana that can be stored in the mana receptacle");
        maxManaUsage = cfg.getInt("maxManaUsage", CATEGORY_GENERAL, maxManaUsage,
                1, 10000000, "Maximum amount of mana that can be used per tick per mana receptacle");

        maxManaRotationSpeed = cfg.getFloat("maxManaRotationSpeed", CATEGORY_GENERAL, maxManaRotationSpeed,
                1.0f, 1000.0f, "Maximum speed factor for the rotating spheres of the mana receptacle during mana crafting");
        manaRotationBoost = cfg.getInt("manaRotationBoost", CATEGORY_GENERAL, manaRotationBoost,
                1, 100000, "Number of ticks that the rotating spheres will speed up during mana crafting");
        manaRotationSpeedUp = cfg.getFloat("manaRotationSpeedUp", CATEGORY_GENERAL, manaRotationSpeedUp,
                0.0f, 1000.0f, "Every tick the rotating spheres will speed up during mana crafting (and during boost)");
        manaRotationSpinDown = cfg.getFloat("manaRotationSpinDown", CATEGORY_GENERAL, manaRotationSpinDown,
                0.0f, 1000.0f, "If not mana crafting this is the speed at which the speed of the rotating spheres will decrease");

        ticksAllowedWithoutMana = cfg.getInt("ticksAllowedWithoutMana", CATEGORY_GENERAL, ticksAllowedWithoutMana, -1, 1000000000,
                "If -1 then the efab will pause the crafting until mana is available. Otherwise it will allow a delay of the specified amount of ticks before aborting");

        rfControlInputPerTick = cfg.getInt("rfControlInputPerTick", CATEGORY_GENERAL, rfControlInputPerTick,
                1, 1000000, "How much RF/t the RF Control block can receive");
        rfControlMax = cfg.getInt("rfControlMax", CATEGORY_GENERAL, rfControlMax,
                1, 10000000, "The maximum amount of RF for the RF Control block");

        rfStorageInputPerTick = cfg.getInt("rfStorageInputPerTick", CATEGORY_GENERAL, rfStorageInputPerTick,
                1, 1000000, "How much RF/t the RF Storage block can receive");
        rfStorageMax = cfg.getInt("rfStorageMax", CATEGORY_GENERAL, rfStorageMax,
                1, 10000000, "The maximum amount of RF for the RF Storage block");
        rfStorageInternalFlow = cfg.getInt("rfStorageInternalFlow", CATEGORY_GENERAL, rfStorageInternalFlow,
                1, 1000000, "How much RF/t the RF Storage block can contribute to crafting");

        ticksAllowedWithoutRF = cfg.getInt("ticksAllowedWithoutRF", CATEGORY_GENERAL, ticksAllowedWithoutRF, -1, 1000000000,
                "If -1 then the efab will pause the crafting until power is available. Otherwise it will allow a delay of the specified amount of ticks before aborting");

        maxCraftAnimationSpeed = cfg.getFloat("maxCraftAnimationSpeed", CATEGORY_GENERAL, maxCraftAnimationSpeed,
                1.0f, 1000.0f, "Maximum speed factor for the animation of the crafter during auto crafting");
        craftAnimationBoost = cfg.getInt("craftAnimationBoost", CATEGORY_GENERAL, craftAnimationBoost,
                1, 100000, "Number of ticks that the animation of the crafter will speed up during auto crafting");
        craftAnimationSpeedUp = cfg.getFloat("craftAnimationSpeedUp", CATEGORY_GENERAL, craftAnimationSpeedUp,
                0.0f, 1000.0f, "Every tick the animation of the crafter will speed up during auto crafting (and during boost)");
        craftAnimationSpinDown = cfg.getFloat("craftAnimationSpinDown", CATEGORY_GENERAL, craftAnimationSpinDown,
                0.0f, 1000.0f, "If not auto crafting this is the speed at which the speed of the animation of the crafter will decrease");

        crafterDelay = cfg.getInt("crafterDelay", CATEGORY_GENERAL, crafterDelay,
                1, 100000, "Number of ticks between every auto craft operation");
    }
}
