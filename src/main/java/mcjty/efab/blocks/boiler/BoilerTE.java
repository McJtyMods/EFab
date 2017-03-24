package mcjty.efab.blocks.boiler;

import mcjty.efab.blocks.GenericEFabTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class BoilerTE extends GenericEFabTile implements ITickable {

    public static final int MAX_TEMPERATURE = 200;

    private float temperature = 20.0f;

    @Override
    public void update() {
        if (hasHeatBelow()) {
            if (temperature < MAX_TEMPERATURE) {
                temperature += 2;
            }
        } else {
            if (temperature > 20) {
                temperature -= 2;
            }
        }
        markDirtyQuick();
    }

    public float getTemperature() {
        return temperature;
    }

    public boolean canMakeSteam() {
        return temperature >= 100;
    }

    private boolean hasHeatBelow() {
        return isHot(getPos().down());
    }


    private boolean isHot(BlockPos p) {
        IBlockState state = getWorld().getBlockState(p);
        Block block = state.getBlock();
        if (block == Blocks.FIRE) {
            return true;
        } else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
            return true;
        } else if (block.isBurning(getWorld(), p)) {
            return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        temperature = tagCompound.getFloat("temperature");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setFloat("temperature", temperature);
        return super.writeToNBT(tagCompound);
    }

}
