package mcjty.efab.compat.botania;

import mcjty.efab.blocks.manareceptacle.ManaReceptacleBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BotaniaSupportSetup {

    private static ManaReceptacleBlock manaReceptacleBlock;

    public static void preInit() {
        manaReceptacleBlock = new ManaReceptacleBlock();
    }

    public static void postInit() {

    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        manaReceptacleBlock.initModel();
    }
}
