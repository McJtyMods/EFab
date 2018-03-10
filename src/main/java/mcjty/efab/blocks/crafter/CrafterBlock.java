package mcjty.efab.blocks.crafter;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.proxy.GuiProxy;
import mcjty.lib.container.BaseBlock;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class CrafterBlock extends GenericEFabMultiBlockPart<CrafterTE, CrafterContainer> {

    public static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public CrafterBlock() {
        super(Material.IRON, CrafterTE.class, CrafterContainer.class, "crafter", false);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public boolean needsRedstoneCheck() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block adds auto crafting to the fabricator");
        tooltip.add(TextFormatting.WHITE + "Also needs a " + TextFormatting.GREEN + "processor" + TextFormatting.WHITE
                + " and one or more " + TextFormatting.GREEN + "storage" + TextFormatting.WHITE + " blocks");
        tooltip.add(TextFormatting.YELLOW + "The crafter needs a redstone signal to work!");
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof CrafterTE) {
            CrafterTE crafterTE = (CrafterTE) te;
            if (!crafterTE.isOn()) {
                probeInfo.text(TextStyleClass.LABEL + "Status " + TextStyleClass.INFO + "OFF");
            } else if (crafterTE.isCrafting()) {
                probeInfo.text(TextStyleClass.LABEL + "Status " + TextStyleClass.INFO + crafterTE.getProgress() + "%");
            } else if (crafterTE.getLastError() == null || crafterTE.getLastError().trim().isEmpty()) {
                probeInfo.text(TextStyleClass.LABEL + "Status " + TextStyleClass.INFO + "IDLE");
            } else {
                probeInfo.text(TextStyleClass.LABEL + "Status " + TextStyleClass.ERROR + crafterTE.getLastError());
            }
            List<ItemStack> outputs = crafterTE.getOutputs();
            if (!outputs.isEmpty()) {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                    .text(TextStyleClass.LABEL + "Output ")
                    .item(outputs.get(0));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(CrafterTE.class, new CrafterRenderer());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return CrafterGui.class;
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_CRAFTER;
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
