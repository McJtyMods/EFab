package mcjty.efab.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    public static UpgradeArmory upgradeArmory;
    public static UpgradeMagic upgradeMagic;
    public static UpgradePower upgradePower;
    public static UpgradeDigital upgradeDigital;

    public static void init() {
        upgradeArmory = new UpgradeArmory();
        upgradeMagic = new UpgradeMagic();
        upgradePower = new UpgradePower();
        upgradeDigital = new UpgradeDigital();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        upgradeArmory.initModel();
        upgradeMagic.initModel();
        upgradePower.initModel();
        upgradeDigital.initModel();
    }
}
