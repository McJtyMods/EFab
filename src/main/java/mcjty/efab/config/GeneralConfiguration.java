package mcjty.efab.config;

import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {

    public static final String CATEGORY_GENERAL = "general";

    public static float baseMachineVolume = 1.0f;
    public static float baseSparksVolume = 0.7f;
    public static float baseSteamVolume = 1.0f;
    public static int soundMachineTicks = 50;
    public static int soundSparksTicks = 25;
    public static int soundSteamTicks = 50;

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

    public static int tankCapacity = 10000;

    public static int rfControlInputPerTick = 10;
    public static int rfControlMax = 20;

    public static int rfStorageInputPerTick = 50;
    public static int rfStorageMax = 10000;
    public static int rfStorageInternalFlow = 30;
    public static boolean abortCraftWhenOutOfRf = true;

    public static int maxMana = 10000;
    public static int maxManaUsage = 1000;
    public static boolean abortCraftWhenOutOfMana = true;

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
        soundMachineTicks = cfg.getInt("soundMachineTicks", CATEGORY_GENERAL, soundMachineTicks,
                0, 1000000, "Amount of ticks that the gearbox sound should play");
        soundSparksTicks = cfg.getInt("soundSparksTicks", CATEGORY_GENERAL, soundSparksTicks,
                0, 1000000, "Amount of ticks that the sparks sound should play");
        soundSteamTicks = cfg.getInt("soundSteamTicks", CATEGORY_GENERAL, soundSteamTicks,
                0, 1000000, "Amount of ticks that the steam sound should play");

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

        abortCraftWhenOutOfMana = cfg.getBoolean("abortCraftWhenOutOfMana", CATEGORY_GENERAL, abortCraftWhenOutOfMana,
                "If enabled the crafting will be aborted if there is not enough mana. If disabled the crafting operation will wait until mana becomes available");

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

        abortCraftWhenOutOfRf = cfg.getBoolean("abortCraftWhenOutOfRf", CATEGORY_GENERAL, abortCraftWhenOutOfRf,
                "If enabled the crafting will be aborted if there is not enough power. If disabled the crafting operation will wait until power becomes available");
    }
}
