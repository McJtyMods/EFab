package mcjty.efab.blocks;

import mcjty.efab.blocks.base.BaseBlock;
import mcjty.efab.blocks.gearbox.GearBoxBlock;
import mcjty.efab.blocks.grid.GridBlock;
import mcjty.efab.blocks.pipes.PipeBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static BaseBlock baseBlock;
    public static GearBoxBlock gearBoxBlock;
    public static PipeBlock pipeBlock;
    public static GridBlock gridBlock;

    public static void init() {
        baseBlock = new BaseBlock();
        gearBoxBlock = new GearBoxBlock();
        pipeBlock = new PipeBlock();
        gridBlock = new GridBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        baseBlock.initModel();
        gearBoxBlock.initModel();
        pipeBlock.initModel();
        gridBlock.initModel();
    }
}
