package mcjty.efab.blocks.steamengine;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
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

public class SteamEngineBlock extends GenericEFabMultiBlockPart<SteamEngineTE, EmptyContainer> {

    public static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public SteamEngineBlock() {
        super(Material.IRON, SteamEngineTE.class, EmptyContainer::new, "steamengine", false);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block adds " + TextFormatting.GREEN + "steam" + TextFormatting.WHITE + " crafting to the fabricator");
        tooltip.add(TextFormatting.WHITE + "Also needs a " + TextFormatting.GREEN + "boiler" + TextFormatting.WHITE
                + " and a " + TextFormatting.GREEN + "tank" + TextFormatting.WHITE + " with water");
        if (GeneralConfiguration.maxSpeedupBonus > 1) {
            tooltip.add(TextFormatting.GOLD + "You can use up to " + GeneralConfiguration.maxSpeedupBonus + " steam engines");
            tooltip.add(TextFormatting.GOLD + "to speed up steam related recipes");
        }
    }

    @Override
    public void initModel() {
        super.initModel();
        SteamEngineRenderer.register();
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
}
