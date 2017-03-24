package mcjty.efab.blocks.boiler;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.lib.container.EmptyContainer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;

public class BoilerBlock extends GenericEFabMultiBlockPart<BoilerTE, EmptyContainer> {

    public BoilerBlock() {
        super(Material.IRON, BoilerTE.class, EmptyContainer.class, "boiler", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof BoilerTE) {
            BoilerTE boilerTE = (BoilerTE) te;
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            probeInfo.text(TextFormatting.GREEN + "Temperature: " + decimalFormat.format(boilerTE.getTemperature()));
        }
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
