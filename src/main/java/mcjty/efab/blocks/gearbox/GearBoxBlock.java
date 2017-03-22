package mcjty.efab.blocks.gearbox;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;

public class GearBoxBlock extends GenericEFabMultiBlockPart<GearBoxTE, EmptyContainer> {

    public GearBoxBlock() {
        super(Material.IRON, GearBoxTE.class, EmptyContainer.class, "gearbox", false);
    }
}
