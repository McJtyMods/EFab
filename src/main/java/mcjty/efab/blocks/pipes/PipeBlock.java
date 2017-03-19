package mcjty.efab.blocks.pipes;

import mcjty.efab.blocks.GenericEFabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class PipeBlock extends GenericEFabBlock {

    public static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 3);

    public PipeBlock() {
        super(Material.IRON, "pipes");
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

    private static Random random = new Random();

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state
                .withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite())
                .withProperty(STATE, random.nextInt(4))
                , 2);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STATE, FACING_HORIZ);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing value = EnumFacing.VALUES[(meta & 3) + 2];
        return getDefaultState()
                .withProperty(FACING_HORIZ, value)
                .withProperty(STATE, (meta>>2) & 3);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(FACING_HORIZ).getIndex()-2) + (state.getValue(STATE) << 2);
    }
}
