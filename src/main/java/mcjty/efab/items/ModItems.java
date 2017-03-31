package mcjty.efab.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    public static UpgradeArmory upgradeArmory;

    public static void init() {
        upgradeArmory = new UpgradeArmory();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        upgradeArmory.initModel();
    }
}
