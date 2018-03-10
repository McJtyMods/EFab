package mcjty.efab.blocks.grid;

import mcjty.efab.blocks.GenericEFabBlockWithTE;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.blocks.crafter.CrafterTE;
import mcjty.efab.proxy.GuiProxy;
import mcjty.efab.sound.ISoundProducer;
import mcjty.lib.container.BaseBlock;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class GridBlock extends GenericEFabBlockWithTE<GridTE, GridContainer> implements ISoundProducer {

    public static PropertyBool HALF = PropertyBool.create("half");

    public GridBlock() {
        super(Material.IRON, GridTE.class, GridContainer.class, "grid", true);
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This is the basic crafting device for the fabricator");
        tooltip.add(TextFormatting.WHITE + "Add blocks to this to extend the capabilities");
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(GridTE.class, new GridRenderer());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GridGui.class;
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_GRID;
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

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof GridTE) {
            GridTE gridTE = (GridTE) te;
            List<String> errorState = gridTE.getErrorState();
            if (!errorState.isEmpty()) {
                for (String error : errorState) {
                    probeInfo.text(TextStyleClass.LABEL + "Status " + TextStyleClass.ERROR + error);
                }
            } else {
                int ticks = gridTE.getTicksRemaining();
                if (ticks < 0) {
                    probeInfo.text(TextStyleClass.LABEL + "Status " + TextStyleClass.INFO + "IDLE");
                } else {
                    int total = gridTE.getTotalTicks();
                    if (total > 0) {
                        int progress = (total-ticks) * 100 / total;
                        probeInfo.text(TextStyleClass.LABEL + "Status " + TextStyleClass.INFO + progress + "%");
                    }
                }
            }
            List<ItemStack> outputs = gridTE.getOutputs();
            if (!outputs.isEmpty()) {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .text(TextStyleClass.LABEL + "Output ")
                        .item(outputs.get(0));
            }
        }
    }


//    @Override
//    public EnumBlockRenderType getRenderType(IBlockState state) {
//        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
//    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean half = isHalfBlock(worldIn, pos);
        return state.withProperty(HALF, half);
    }

    public static boolean isHalfBlock(IBlockAccess worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.baseBlock;
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ, HALF);
    }
}
