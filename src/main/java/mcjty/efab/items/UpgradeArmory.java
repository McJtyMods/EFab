package mcjty.efab.items;

import mcjty.efab.recipes.RecipeTier;

public class UpgradeArmory extends UpgradeItem {

    public UpgradeArmory() {
        super("upgrade_armory");
    }

    @Override
    public RecipeTier providesTier() {
        return RecipeTier.UPGRADE_ARMORY;
    }
}
