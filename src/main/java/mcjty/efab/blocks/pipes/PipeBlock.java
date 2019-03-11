package mcjty.efab.blocks.pipes;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.ConfigSetup;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class PipeBlock extends GenericEFabMultiBlockPart<PipeTE, EmptyContainer> {

    public static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public PipeBlock() {
        super(Material.IRON, PipeTE.class, EmptyContainer::new, "pipes", false);
    }

    public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 1);

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        if (ConfigSetup.maxPipeSpeedBonus.get() > 1) {
            tooltip.add(TextFormatting.GOLD + "You can use up to " + ConfigSetup.maxPipeSpeedBonus + " pipes");
            tooltip.add(TextFormatting.GOLD + "to speed up liquid related recipes");
        }
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return EMPTY;
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
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, blockIn, fromPos);
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
                        .withProperty(STATE, random.nextInt(2))
//                        .withProperty(STATE, pos.getX()%4)
                , 2);
//        System.out.println("pos.getX()%4 = " + pos.getX() % 4);
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
