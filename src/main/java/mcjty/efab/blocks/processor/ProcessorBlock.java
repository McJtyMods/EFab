package mcjty.efab.blocks.processor;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ProcessorBlock extends GenericEFabMultiBlockPart<ProcessorTE, EmptyContainer> {

    public ProcessorBlock() {
        super(Material.IRON, ProcessorTE.class, EmptyContainer.class, "processor", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block adds " + TextFormatting.GREEN + "processing"
                + TextFormatting.WHITE + " power to the fabricator");
        tooltip.add(TextFormatting.WHITE + "and is also needed for auto crafting");
    }

}
