package mcjty.efab.blocks.tank;

import mcjty.efab.blocks.ModBlocks;

public class AdvancedTankTE extends TankTE {

    @Override
    public boolean isAdvanced() {
        return true;
    }

    @Override
    public int getCapacity() {
        return ModBlocks.tank2Block.capacity;
    }
}
