package mcjty.efab.blocks.processor;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ProcessorBlock extends GenericEFabMultiBlockPart<ProcessorTE, EmptyContainer> {

    public ProcessorBlock() {
        super(Material.IRON, ProcessorTE.class, EmptyContainer::new, "processor", false);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block adds " + TextFormatting.GREEN + "processing"
                + TextFormatting.WHITE + " power to the fabricator");
        tooltip.add(TextFormatting.WHITE + "and is also needed for auto crafting");
        if (GeneralConfiguration.maxSpeedupBonus > 1) {
            tooltip.add(TextFormatting.GOLD + "You can use up to " + GeneralConfiguration.maxSpeedupBonus + " processors");
            tooltip.add(TextFormatting.GOLD + "to speed up processing based recipes");
        }
    }

}
