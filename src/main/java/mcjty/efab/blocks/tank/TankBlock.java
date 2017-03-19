package mcjty.efab.blocks.tank;

import mcjty.efab.blocks.GenericEFabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TankBlock extends GenericEFabBlock {

    public static final PropertyEnum<EnumTankState> STATE = PropertyEnum.create("state", EnumTankState.class, EnumTankState.values());

    public TankBlock() {
        super(Material.IRON, "tank");
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

    @Override
    protected void clOnNeighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
        super.clOnNeighborChanged(state, world, pos, blockIn);
        // @todo: on 1.11 we could have used the position from which the update is coming
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
//        boolean half = isNeedToBeHalf(worldIn.getBlockState(pos.down()).getBlock());
        boolean tankUp = worldIn.getBlockState(pos.up()).getBlock() == this;
        boolean tankDown = worldIn.getBlockState(pos.down()).getBlock() == this;
        EnumTankState s;
        if (tankUp && tankDown) {
            s = EnumTankState.MIDDLE;
        } else if (tankUp) {
            s = EnumTankState.BOTTOM;
        } else if (tankDown) {
            s = EnumTankState.TOP;
        } else {
            s = EnumTankState.FULL;
        }
        return state.withProperty(STATE, s);
    }


    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STATE);
    }
}
