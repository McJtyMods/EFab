package mcjty.efab.blocks.rfstorage;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.ConfigSetup;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class RFStorageBlock extends GenericEFabMultiBlockPart<RFStorageTE, EmptyContainer> {

    private final boolean advanced;

    public RFStorageBlock(String name, boolean advanced, Class<? extends RFStorageTE> teClazz) {
        super(Material.IRON, teClazz, EmptyContainer::new, name, false);
        this.advanced = advanced;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, playerIn, tooltip, flag);
        tooltip.add(TextFormatting.WHITE + "This block can store " + TextFormatting.GREEN
                + (advanced ? ConfigSetup.advancedRfStorageMax.get() : ConfigSetup.rfStorageMax.get())
                + TextFormatting.WHITE + " RF");
        tooltip.add(TextFormatting.WHITE + "and contributes " + TextFormatting.GREEN
                + (advanced ? ConfigSetup.advancedRfStorageInternalFlow.get() : ConfigSetup.rfStorageInternalFlow.get())
                + TextFormatting.WHITE + " RF/t to crafting");
    }
}
