package mcjty.efab.items;

import mcjty.efab.recipes.RecipeTier;

public class UpgradeDigital extends UpgradeItem {

    public UpgradeDigital() {
        super("upgrade_digital");
    }

    @Override
    public RecipeTier providesTier() {
        return RecipeTier.UPGRADE_DIGITAL;
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
