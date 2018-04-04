package mcjty.efab.blocks.storage;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.proxy.GuiProxy;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class StorageBlock extends GenericEFabMultiBlockPart<StorageTE, StorageContainer> {

    public StorageBlock() {
        super(Material.IRON, StorageTE.class, StorageContainer.class, "storage", true);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block can store items for");
        tooltip.add(TextFormatting.WHITE + "usage with the " + TextFormatting.GREEN + "crafter");
        tooltip.add(TextFormatting.GOLD + "If you give this storage a name it will only");
        tooltip.add(TextFormatting.GOLD + "pull items from item storages with the same name!");
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof StorageTE) {
            StorageTE storageTE = (StorageTE) te;
            if (storageTE.getCraftingName() != null && !storageTE.getCraftingName().trim().isEmpty()) {
                probeInfo.text(TextStyleClass.LABEL + "Name " + TextStyleClass.INFO + storageTE.getCraftingName());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return StorageGui.class;
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_STORAGE;
    }


}
