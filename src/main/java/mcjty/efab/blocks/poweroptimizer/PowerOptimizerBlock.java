package mcjty.efab.blocks.poweroptimizer;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PowerOptimizerBlock extends GenericEFabMultiBlockPart<PowerOptimizerTE, EmptyContainer> {

    public PowerOptimizerBlock() {
        super(Material.IRON, PowerOptimizerTE.class, EmptyContainer::new, "power_optimizer", false);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block considerably optimizes");
        tooltip.add(TextFormatting.WHITE + "RF based crafting operations by");
        tooltip.add(TextFormatting.WHITE + "negating the time constraint");
        tooltip.add(TextFormatting.WHITE + "and allowing the craft to go as");
        tooltip.add(TextFormatting.WHITE + "fast as there is available power");
    }

    @Override
    public void initModel() {
        super.initModel();
        PowerOptimizerRenderer.register();
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
