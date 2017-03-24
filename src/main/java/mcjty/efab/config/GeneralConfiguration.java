package mcjty.efab.config;

import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {

    public static final String CATEGORY_GENERAL = "general";

    public static float baseMachineVolume = 1.0f;
    public static float baseSparksVolume = 1.0f;
    public static float baseSteamVolume = 1.0f;

    public static int waterSteamCraftingConsumption = 5;

    public static void init(Configuration cfg) {
        baseMachineVolume = cfg.getFloat("baseMachineVolume", CATEGORY_GENERAL, baseMachineVolume,
                0.0f, 1.0f, "The volume for the machine sound (0.0 is off)");
        baseSparksVolume = cfg.getFloat("baseSparksVolume", CATEGORY_GENERAL, baseSparksVolume,
                0.0f, 1.0f, "The volume for the sparks sound (0.0 is off)");
        baseSteamVolume = cfg.getFloat("baseSteamVolume", CATEGORY_GENERAL, baseSteamVolume,
                0.0f, 1.0f, "The volume for the steam sound (0.0 is off)");

        waterSteamCraftingConsumption = cfg.getInt("waterSteamCraftingConsumption", CATEGORY_GENERAL, waterSteamCraftingConsumption,
                0, 1000000, "Amount of water per tick that is being consumed during a steam crafting operation");
    }
}
