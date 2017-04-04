package mcjty.efab.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    public static UpgradeArmory upgradeArmory;
    public static UpgradeMagic upgradeMagic;

    public static void init() {
        upgradeArmory = new UpgradeArmory();
        upgradeMagic = new UpgradeMagic();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        upgradeArmory.initModel();
        upgradeMagic.initModel();
    }
}
