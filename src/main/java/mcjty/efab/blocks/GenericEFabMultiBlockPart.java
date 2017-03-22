package mcjty.efab.blocks;

import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GenericEFabMultiBlockPart<T extends GenericTileEntity, C extends Container> extends GenericEFabBlockWithTE<T,C> {

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

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {

        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {

        }
        super.breakBlock(world, pos, state);
    }
}
