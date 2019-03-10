package mcjty.efab.config;

import mcjty.lib.thirteen.ConfigSpec;
import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {

    public static final String CATEGORY_GENERAL = "general";

    public static ConfigSpec.DoubleValue baseMachineVolume;
    public static ConfigSpec.DoubleValue baseSparksVolume;
    public static ConfigSpec.DoubleValue baseSteamVolume;
    public static ConfigSpec.DoubleValue baseBeepsVolume;
    public static ConfigSpec.IntValue soundMachineTicks;
    public static ConfigSpec.IntValue soundSparksTicks;
    public static ConfigSpec.IntValue soundSteamTicks;
    public static ConfigSpec.IntValue soundBeepsTicks;

    public static ConfigSpec.IntValue maxSpeedupBonus;
    public static ConfigSpec.IntValue maxPipeSpeedBonus;
    public static ConfigSpec.IntValue vanillaCraftTime;
    public static ConfigSpec.BooleanValue vanillaCraftingAllowed;

    public static ConfigSpec.IntValue waterSteamCraftingConsumption;
    public static ConfigSpec.IntValue waterSteamStartAmount;

    public static ConfigSpec.DoubleValue maxBoilerTemperature;
    public static ConfigSpec.DoubleValue ambientBoilerTemperature;
    public static ConfigSpec.DoubleValue boilerRiseTemperature;
    public static ConfigSpec.DoubleValue boilerCoolTemperature;

    public static ConfigSpec.DoubleValue maxSteamWheelSpeed;
    public static ConfigSpec.IntValue steamWheelBoost;
    public static ConfigSpec.DoubleValue steamWheelSpeedUp;
    public static ConfigSpec.DoubleValue steamWheelSpinDown;

    public static ConfigSpec.DoubleValue maxCraftAnimationSpeed;
    public static ConfigSpec.IntValue craftAnimationBoost;
    public static ConfigSpec.DoubleValue craftAnimationSpeedUp;
    public static ConfigSpec.DoubleValue craftAnimationSpinDown;
    public static ConfigSpec.IntValue crafterDelay;

    public static ConfigSpec.IntValue tankCapacity;
    public static ConfigSpec.IntValue tank2Capacity;

    public static ConfigSpec.IntValue rfControlInputPerTick;
    public static ConfigSpec.IntValue rfControlMax;

    public static ConfigSpec.IntValue rfStorageInputPerTick;
    public static ConfigSpec.IntValue rfStorageMax;
    public static ConfigSpec.IntValue rfStorageInternalFlow;
    public static ConfigSpec.IntValue advancedRfStorageMax;
    public static ConfigSpec.IntValue advancedRfStorageInternalFlow;
    public static ConfigSpec.IntValue ticksAllowedWithoutRF;

    public static ConfigSpec.IntValue maxMana;
    public static ConfigSpec.IntValue maxManaUsage;
    public static ConfigSpec.IntValue ticksAllowedWithoutMana;

    public static ConfigSpec.DoubleValue maxManaRotationSpeed;
    public static ConfigSpec.IntValue manaRotationBoost;
    public static ConfigSpec.DoubleValue manaRotationSpeedUp;
    public static ConfigSpec.DoubleValue manaRotationSpinDown;

    private static final ConfigSpec.Builder SERVER_BUILDER = new ConfigSpec.Builder();
    private static final ConfigSpec.Builder CLIENT_BUILDER = new ConfigSpec.Builder();

    static {
        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        baseMachineVolume = CLIENT_BUILDER
                .comment("The volume for the machine sound (0.0 is off)")
                .defineInRange("baseMachineVolume", 1.0, 0.0, 1.0);
        baseSparksVolume = CLIENT_BUILDER
                .comment("The volume for the sparks sound (0.0 is off)")
                .defineInRange("baseSparksVolume", 0.7, 0.0, 1.0);
        baseSteamVolume = CLIENT_BUILDER
                .comment("The volume for the steam sound (0.0 is off)")
                .defineInRange("baseSteamVolume", 1.0, 0.0, 1.0);
        baseBeepsVolume = CLIENT_BUILDER
                .comment("The volume for the beeps sound (0.0 is off)")
                .defineInRange("baseBeepsVolume", 0.2, 0.0, 1.0);
        soundMachineTicks = CLIENT_BUILDER
                .comment("Amount of ticks that the gearbox sound should play")
                .defineInRange("soundMachineTicks", 50, 0, 1000000);
        soundSparksTicks = CLIENT_BUILDER
                .comment("Amount of ticks that the sparks sound should play")
                .defineInRange("soundSparksTicks", 25, 0, 1000000);
        soundSteamTicks = CLIENT_BUILDER
                .comment("Amount of ticks that the steam sound should play")
                .defineInRange("soundSteamTicks", 50, 0, 1000000);
        soundBeepsTicks = CLIENT_BUILDER
                .comment("Amount of ticks that the beeps sound should play")
                .defineInRange("soundBeepsTicks", 8, 0, 1000000);


        maxSpeedupBonus = SERVER_BUILDER
                .comment("The maximum speed bonus on a crafting operation you can get by adding multiple machine parts")
                .defineInRange("maxSpeedupBonus", 4, 1, 64);
        maxPipeSpeedBonus = SERVER_BUILDER
                .comment("The maximum speed bonus on a liquid crafting operation")
                .defineInRange("maxPipeSpeedBonus", 2, 1, 64);
        vanillaCraftTime = SERVER_BUILDER
                .comment("The amount of time needed for the grid/crafter to craft normal vanilla recipes")
                .defineInRange("vanillaCraftTime", 1, 0, 1000000);
        vanillaCraftingAllowed = SERVER_BUILDER
                .comment("If enabled the EFab grid and crafter will also support vanilla recipes. If disabled they don't")
                .define("vanillaCraftingAllowed", true);

        waterSteamCraftingConsumption = SERVER_BUILDER
                .comment("Amount of water per tick that is being consumed during a steam crafting operation")
                .defineInRange("waterSteamCraftingConsumption", 5, 0, 1000000);
        waterSteamStartAmount = SERVER_BUILDER
                .comment("Amount of water that is needed to be able to start a steam crafting operation")
                .defineInRange("waterSteamStartAmount", 500, 0, 1000000);

        maxBoilerTemperature = SERVER_BUILDER
                .comment("The maximum temperature for the boiler (it generates steam over 100C)")
                .defineInRange("maxBoilerTemperature", 200, 100.0f, 1000.0f);
        ambientBoilerTemperature = SERVER_BUILDER
                .comment("The ambient temperature of the boiler when no heat is below it")
                .defineInRange("ambientBoilerTemperature", 20, 0.0f, 100.0f);
        boilerRiseTemperature = SERVER_BUILDER
                .comment("Every tick the boiler will heat up this amount if there is a source of heat below it")
                .defineInRange("boilerRiseTemperature", .5, 0.0f, 100.0f);
        boilerCoolTemperature = SERVER_BUILDER
                .comment("Every tick the boiler will cool down this amount if there is no source of heat below it")
                .defineInRange("boilerCoolTemperature", .3, 0.0f, 100.0f);

        maxSteamWheelSpeed = SERVER_BUILDER
                .comment("Maximum speed factor for the spinning wheel of the stean engine during steam crafting")
                .defineInRange("maxSteamWheelSpeed", 20, 1.0f, 1000.0f);
        steamWheelBoost = SERVER_BUILDER
                .comment("Number of ticks that the steam wheel will speed up during steam crafting")
                .defineInRange("steamWheelBoost", 40, 1, 100000);
        steamWheelSpeedUp = SERVER_BUILDER
                .comment("Every tick the steam wheel will speed up during steam crafting (and during boost)")
                .defineInRange("steamWheelSpeedUp", 2, 0.0f, 1000.0f);
        steamWheelSpinDown = SERVER_BUILDER
                .comment("If not steam crafting this is the speed at which the speed of the wheel will decrease")
                .defineInRange("steamWheelSpinDown", 0.3, 0.0f, 1000.0f);

        tankCapacity = SERVER_BUILDER
                .comment("Capacity of each tank block in MB")
                .defineInRange("tankCapacity", 16000, 1, 1000000);
        tank2Capacity = SERVER_BUILDER
                .comment("Capacity of each tank (tier 2) block in MB")
                .defineInRange("tank2Capacity", 64000, 1, 1000000);

        maxMana = SERVER_BUILDER
                .comment("Maximum amount of mana that can be stored in the mana receptacle")
                .defineInRange("maxMana", 10000, 1, 100000000);
        maxManaUsage = SERVER_BUILDER
                .comment("Maximum amount of mana that can be used per tick per mana receptacle")
                .defineInRange("maxManaUsage", 1000, 1, 10000000);

        maxManaRotationSpeed = SERVER_BUILDER
                .comment("Maximum speed factor for the rotating spheres of the mana receptacle during mana crafting")
                .defineInRange("maxManaRotationSpeed", 20, 1.0f, 1000.0f);
        manaRotationBoost = SERVER_BUILDER
                .comment("Number of ticks that the rotating spheres will speed up during mana crafting")
                .defineInRange("manaRotationBoost", 40, 1, 100000);
        manaRotationSpeedUp = SERVER_BUILDER
                .comment("Every tick the rotating spheres will speed up during mana crafting (and during boost)")
                .defineInRange("manaRotationSpeedUp", 2, 0.0f, 1000.0f);
        manaRotationSpinDown = SERVER_BUILDER
                .comment("If not mana crafting this is the speed at which the speed of the rotating spheres will decrease")
                .defineInRange("manaRotationSpinDown", 0.3, 0.0f, 1000.0f);

        ticksAllowedWithoutMana = SERVER_BUILDER
                .comment("If -1 then the efab will pause the crafting until mana is available. Otherwise it will allow a delay of the specified amount of ticks before aborting")
                .defineInRange("ticksAllowedWithoutMana", 1, -1, 1000000000);

        rfControlInputPerTick = SERVER_BUILDER
                .comment("How much RF/t the RF Control block can receive")
                .defineInRange("rfControlInputPerTick", 10, 1, 10000000);
        rfControlMax = SERVER_BUILDER
                .comment("The maximum amount of RF for the RF Control block")
                .defineInRange("rfControlMax", 20, 1, 100000000);

        rfStorageInputPerTick = SERVER_BUILDER
                .comment("How much RF/t the RF Storage block can receive")
                .defineInRange("rfStorageInputPerTick", 50, 1, 10000000);
        rfStorageMax = SERVER_BUILDER
                .comment("The maximum amount of RF for the RF Storage block")
                .defineInRange("rfStorageMax", 10000, 1, 100000000);
        rfStorageInternalFlow = SERVER_BUILDER
                .comment("How much RF/t the RF Storage block can contribute to crafting")
                .defineInRange("rfStorageInternalFlow", 30, 1, 10000000);
        advancedRfStorageMax = SERVER_BUILDER
                .comment("The maximum amount of RF for the advanced RF Storage block")
                .defineInRange("advancedRfStorageMax", 100000, 1, 1000000000);
        advancedRfStorageInternalFlow = SERVER_BUILDER
                .comment("How much RF/t the advanced RF Storage block can contribute to crafting")
                .defineInRange("advancedRfStorageInternalFlow", 100, 1, 10000000);

        ticksAllowedWithoutRF = SERVER_BUILDER
                .comment("If -1 then the efab will pause the crafting until power is available. Otherwise it will allow a delay of the specified amount of ticks before aborting")
                .defineInRange("ticksAllowedWithoutRF", 1, -1, 1000000000);

        maxCraftAnimationSpeed = SERVER_BUILDER
                .comment("Maximum speed factor for the animation of the crafter during auto crafting")
                .defineInRange("maxCraftAnimationSpeed", 20, 1.0f, 1000.0f);
        craftAnimationBoost = SERVER_BUILDER
                .comment("Number of ticks that the animation of the crafter will speed up during auto crafting")
                .defineInRange("craftAnimationBoost", 40, 1, 100000);
        craftAnimationSpeedUp = SERVER_BUILDER
                .comment("Every tick the animation of the crafter will speed up during auto crafting (and during boost)")
                .defineInRange("craftAnimationSpeedUp", 3, 0.0f, 1000.0f);
        craftAnimationSpinDown = SERVER_BUILDER
                .comment("If not auto crafting this is the speed at which the speed of the animation of the crafter will decrease")
                .defineInRange("craftAnimationSpinDown", 0.3, 0.0f, 1000.0f);

        crafterDelay = SERVER_BUILDER
                .comment("Number of ticks between every auto craft operation")
                .defineInRange("crafterDelay", 10, 1, 100000);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }

    public static ConfigSpec SERVER_CONFIG;
    public static ConfigSpec CLIENT_CONFIG;

    public static void init(Configuration cfg) {
        SERVER_CONFIG = SERVER_BUILDER.build(cfg);
        CLIENT_CONFIG = CLIENT_BUILDER.build(cfg);
    }
}
