package mcjty.efab.blocks;

import mcjty.efab.blocks.base.BaseBlock;
import mcjty.efab.blocks.gearbox.GearBoxBlock;
import mcjty.efab.blocks.grid.GridBlock;
import mcjty.efab.blocks.pipes.PipeBlock;
import mcjty.efab.blocks.tank.TankBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static BaseBlock baseBlock;
    public static GearBoxBlock gearBoxBlock;
    public static PipeBlock pipeBlock;
    public static GridBlock gridBlock;
    public static TankBlock tankBlock;

    public static void init() {
        baseBlock = new BaseBlock();
        gearBoxBlock = new GearBoxBlock();
        pipeBlock = new PipeBlock();
        gridBlock = new GridBlock();
        tankBlock = new TankBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        baseBlock.initModel();
        gearBoxBlock.initModel();
        pipeBlock.initModel();
        gridBlock.initModel();
        tankBlock.initModel();
    }
}
