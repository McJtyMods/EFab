package mcjty.efab.items;

import mcjty.efab.recipes.RecipeTier;

public class UpgradeMagic extends UpgradeItem {

    public UpgradeMagic() {
        super("upgrade_magic");
    }

    @Override
    public RecipeTier providesTier() {
        return RecipeTier.UPGRADE_MAGIC;
    }
}
