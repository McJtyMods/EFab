package mcjty.efab.blocks;

import mcjty.efab.blocks.grid.GridTE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class GenericEFabMultiBlockPart<T extends GenericEFabTile, C extends Container> extends GenericEFabBlockWithTE<T,C> {

    public GenericEFabMultiBlockPart(Material material, Class<? extends T> tileEntityClass, Class<? extends C> containerClass, String name, boolean isContainer) {
        super(material, tileEntityClass, containerClass, name, isContainer);
    }

    public GenericEFabMultiBlockPart(Material material, Class<? extends T> tileEntityClass, Class<? extends C> containerClass, Class<? extends ItemBlock> itemBlockClass, String name, boolean isContainer) {
        super(material, tileEntityClass, containerClass, itemBlockClass, name, isContainer);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    private void invalidateGrids(World world, BlockPos current, Set<BlockPos> visited) {
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);
        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos p = current.offset(dir);
            Block block = world.getBlockState(p).getBlock();
            if (block == ModBlocks.gridBlock) {
                TileEntity te = world.getTileEntity(p);
                if (te instanceof GridTE) {
                    ((GridTE) te).invalidateMultiBlockCache();
                }
                invalidateGrids(world, p, visited);
            } else if (block instanceof GenericEFabMultiBlockPart) {
                invalidateGrids(world, p, visited);
            }
        }
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            invalidateGrids(world, pos, new HashSet<>());
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            invalidateGrids(world, pos, new HashSet<>());
        }
        super.breakBlock(world, pos, state);
    }
}
