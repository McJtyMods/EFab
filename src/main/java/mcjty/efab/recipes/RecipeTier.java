package mcjty.efab.recipes;

public enum RecipeTier {
    STEAM("The boiler and/or steam engine is missing!"),
    GEARBOX("The gearbox is missing!"),
    ADVANCED_GEARBOX("The advanced gearbox is missing!"),
    RF("This machine does not understand RF!"),
    EU("This machine does not understand EU!"),
    LIQUID("There are no tanks for liquids!"),
    MANA("There is no mana support!"),
    COMPUTING("Computing core is missing!"),
    UPGRADE_ARMORY("The armory upgrade is needed!"),
    UPGRADE_MAGIC("The magic upgrade is needed!"),
    UPGRADE_ADVANCED_POWER("The advanced power upgrade is needed!"),
    UPGRADE_DIGITAL("The digital upgrade is needed!");

    private final String missingError;

    RecipeTier(String missingError) {
        this.missingError = missingError;
    }

    public String getMissingError() {
        return missingError;
    }
}
