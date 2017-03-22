package mcjty.efab.config;

import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {

    public static final String CATEGORY_GENERAL = "general";

    public static float baseMachineVolume = 1.0f;
    public static float baseSparksVolume = 1.0f;
    public static float baseSteamVolume = 1.0f;

    public static void init(Configuration cfg) {
        baseMachineVolume = (float) cfg.get(CATEGORY_GENERAL, "baseMachineVolume", baseMachineVolume,
                "The volume for the machine sound (0.0 is off)").getDouble();
        baseSparksVolume = (float) cfg.get(CATEGORY_GENERAL, "baseSparksVolume", baseSparksVolume,
                "The volume for the sparks sound (0.0 is off)").getDouble();
        baseSteamVolume = (float) cfg.get(CATEGORY_GENERAL, "baseSteamVolume", baseSteamVolume,
                "The volume for the steam sound (0.0 is off)").getDouble();
    }
}
