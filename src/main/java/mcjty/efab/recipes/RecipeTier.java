package mcjty.efab.recipes;

public enum RecipeTier {
    STEAM("The boiler and/or steam engine is missing!", 0, 0),
    GEARBOX("The gearbox is missing!", 16, 0),
    ADVANCED_GEARBOX("The advanced gearbox is missing!", 64, 0),
    RF("This machine does not understand RF!", 48, 0),
//    EU("This machine does not understand EU!", 80, 0),
    LIQUID("There are no tanks for liquids!", 32, 0),
    MANA("There is no mana support!", 96, 0),
    COMPUTING("Computing core is missing!", 0, 0),
    UPGRADE_ARMORY("The armory upgrade is needed!", 144, 0),
    UPGRADE_MAGIC("The magic upgrade is needed!", 128, 0),
    UPGRADE_POWER("The power upgrade is needed!", 160, 0),
    UPGRADE_DIGITAL("The digital upgrade is needed!", 0, 0);

    private final String missingError;
    private final int iconX;
    private final int iconY;

    RecipeTier(String missingError, int iconX, int iconY) {
        this.missingError = missingError;
        this.iconX = iconX;
        this.iconY = iconY;
    }

    public String getMissingError() {
        return missingError;
    }

    public int getIconX() {
        return iconX;
    }

    public int getIconY() {
        return iconY;
    }
}
