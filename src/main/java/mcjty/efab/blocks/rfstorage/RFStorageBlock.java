package mcjty.efab.blocks.rfstorage;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;

public class RFStorageBlock extends GenericEFabMultiBlockPart<RFStorageTE, EmptyContainer> {

    public RFStorageBlock() {
        super(Material.IRON, RFStorageTE.class, EmptyContainer.class, "rfstorage", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }
}
