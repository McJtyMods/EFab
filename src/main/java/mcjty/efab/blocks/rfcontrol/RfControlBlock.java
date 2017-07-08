package mcjty.efab.blocks.rfcontrol;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class RfControlBlock extends GenericEFabMultiBlockPart<RfControlTE, EmptyContainer> {


    public static PropertyBool SPARKS = PropertyBool.create("sparks");

    public RfControlBlock() {
        super(Material.IRON, RfControlTE.class, EmptyContainer.class, "rfcontrol", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block can store " + TextFormatting.GREEN + GeneralConfiguration.rfControlMax
                + TextFormatting.WHITE + " RF");
        tooltip.add(TextFormatting.WHITE + "and contributes " + TextFormatting.GREEN + GeneralConfiguration.rfControlMax
                + TextFormatting.WHITE + " RF/t to crafting");
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
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        if (te instanceof RfControlTE) {
            RfControlTE rfte = (RfControlTE) te;
            return super.getActualState(state, world, pos).withProperty(SPARKS, rfte.hasSpark());
        }

        return super.getActualState(state, world, pos);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ, SPARKS);
    }
}
