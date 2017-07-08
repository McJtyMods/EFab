package mcjty.efab.blocks.storage;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.proxy.GuiProxy;
import mcjty.lib.container.GenericGuiContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
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
