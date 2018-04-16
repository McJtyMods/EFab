package mcjty.efab.blocks.tank;

import mcjty.efab.blocks.ModBlocks;
import net.minecraft.block.Block;

public class AdvancedTankTE extends TankTE {

    @Override
    public boolean isAdvanced() {
        return true;
    }

    @Override
    public int getCapacity() {
        return ModBlocks.tank2Block.capacity;
    }

    @Override
    public Block getTankBlock() {
        return ModBlocks.tank2Block;
    }
}
