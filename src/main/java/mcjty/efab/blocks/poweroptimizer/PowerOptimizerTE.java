package mcjty.efab.blocks.poweroptimizer;

import mcjty.efab.blocks.GenericEFabTile;

public class PowerOptimizerTE extends GenericEFabTile {

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}
