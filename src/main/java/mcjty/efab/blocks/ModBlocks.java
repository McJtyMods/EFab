package mcjty.efab.blocks;

import mcjty.efab.blocks.base.BaseBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static BaseBlock baseBlock;

    public static void init() {
        baseBlock = new BaseBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        baseBlock.initModel();
    }
}
