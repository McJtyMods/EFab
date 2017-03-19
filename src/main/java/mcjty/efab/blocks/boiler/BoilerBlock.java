package mcjty.efab.blocks.boiler;

import mcjty.efab.blocks.GenericEFabBlock;
import mcjty.efab.blocks.GenericEFabBlockWithTE;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BoilerBlock extends GenericEFabBlockWithTE<BoilerTE, EmptyContainer> {

    public BoilerBlock() {
        super(Material.IRON, BoilerTE.class, EmptyContainer.class, "boiler", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
