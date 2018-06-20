package mcjty.efab.items;

import mcjty.efab.recipes.RecipeTier;

public class UpgradePower extends UpgradeItem {

    public UpgradePower() {
        super("upgrade_power");
    }

    @Override
    public RecipeTier providesTier() {
        return RecipeTier.UPGRADE_POWER;
    }

    @Override
    public int getPriority() {
        return 8;
    }
}
