package mcjty.efab.compat.botania;

import mcjty.efab.blocks.ISpeedBooster;
import mcjty.efab.blocks.manareceptacle.ManaReceptacleBlock;
import mcjty.efab.blocks.manareceptacle.ManaReceptacleTE;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    public static boolean isManaReceptacle(Block block) {
        return block == manaReceptacleBlock;
    }

    public static int getMana(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ManaReceptacleTE) {
            return ((ManaReceptacleTE) te).getCurrentMana();
        }
        return 0;
    }

    public static ISpeedBooster getSpeedBooster(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ManaReceptacleTE) {
            return (ISpeedBooster) te;
        }
        return null;
    }

    public static void consumeMana(World world, BlockPos pos, int amount) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ManaReceptacleTE) {
            ((ManaReceptacleTE) te).consumeMana(amount);
        }
    }

    public static Block getManaReceptacle() {
        return manaReceptacleBlock;
    }
}
