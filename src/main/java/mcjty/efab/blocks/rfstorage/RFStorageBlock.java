package mcjty.efab.blocks.rfstorage;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class RFStorageBlock extends GenericEFabMultiBlockPart<RFStorageTE, EmptyContainer> {

    public RFStorageBlock() {
        super(Material.IRON, RFStorageTE.class, EmptyContainer.class, "rfstorage", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block can store " + TextFormatting.GREEN + GeneralConfiguration.rfStorageMax
                + TextFormatting.WHITE + " RF");
        tooltip.add(TextFormatting.WHITE + "and contributes " + TextFormatting.GREEN + GeneralConfiguration.rfStorageInternalFlow
                + TextFormatting.WHITE + " RF/t to crafting");
    }
}
