package mcjty.efab.blocks.storage;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class StorageBlock extends GenericEFabMultiBlockPart<StorageTE, EmptyContainer> {

    public StorageBlock() {
        super(Material.IRON, StorageTE.class, EmptyContainer.class, "storage", false);
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
}
