package mcjty.efab.config;

import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {

    public static final String CATEGORY_GENERAL = "general";

    public static float baseMachineVolume = 1.0f;
    public static float baseSparksVolume = 1.0f;
    public static float baseSteamVolume = 1.0f;

    public static int waterSteamCraftingConsumption = 5;

    public static float maxBoilerTemperature = 200;
    public static float ambientBoilerTemperature = 20;
    public static float boilerRiseTemperature = 2;
    public static float boilerCoolTemperature = 2;

    public static void init(Configuration cfg) {
        baseMachineVolume = cfg.getFloat("baseMachineVolume", CATEGORY_GENERAL, baseMachineVolume,
                0.0f, 1.0f, "The volume for the machine sound (0.0 is off)");
        baseSparksVolume = cfg.getFloat("baseSparksVolume", CATEGORY_GENERAL, baseSparksVolume,
                0.0f, 1.0f, "The volume for the sparks sound (0.0 is off)");
        baseSteamVolume = cfg.getFloat("baseSteamVolume", CATEGORY_GENERAL, baseSteamVolume,
                0.0f, 1.0f, "The volume for the steam sound (0.0 is off)");

        waterSteamCraftingConsumption = cfg.getInt("waterSteamCraftingConsumption", CATEGORY_GENERAL, waterSteamCraftingConsumption,
                0, 1000000, "Amount of water per tick that is being consumed during a steam crafting operation");

        maxBoilerTemperature = cfg.getFloat("maxBoilerTemperature", CATEGORY_GENERAL, maxBoilerTemperature,
                100.0f, 1000.0f, "The maximum temperature for the boiler (it generates steam over 100C)");
        ambientBoilerTemperature = cfg.getFloat("ambientBoilerTemperature", CATEGORY_GENERAL, ambientBoilerTemperature,
                0.0f, 100.0f, "The ambient temperature of the boiler when no heat is below it");
        boilerRiseTemperature = cfg.getFloat("boilerRiseTemperature", CATEGORY_GENERAL, boilerRiseTemperature,
                0.0f, 100.0f, "Every tick the boiler will heat up this amount if there is a source of heat below it");
        boilerCoolTemperature = cfg.getFloat("boilerCoolTemperature", CATEGORY_GENERAL, boilerCoolTemperature,
                0.0f, 100.0f, "Every tick the boiler will cool down this amount if there is no source of heat below it");
    }
}
