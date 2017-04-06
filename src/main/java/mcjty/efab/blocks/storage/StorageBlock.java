package mcjty.efab.blocks.storage;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.blocks.grid.GridGui;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.proxy.GuiProxy;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericGuiContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class StorageBlock extends GenericEFabMultiBlockPart<StorageTE, StorageContainer> {

    public StorageBlock() {
        super(Material.IRON, StorageTE.class, StorageContainer.class, "storage", true);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.clAddInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block can store items for");
        tooltip.add(TextFormatting.WHITE + "usage with the " + TextFormatting.GREEN + "crafter");
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
