package mcjty.efab.blocks.boiler;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.config.ConfigSetup;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class BoilerTE extends GenericEFabTile implements ITickable {

    private float temperature = (float) ConfigSetup.ambientBoilerTemperature.get();

    // Client side only for steam rendering
    private double timer = 0;

    @Override
    public void update() {
        if (getWorld().isRemote) {
            if (timer > 0) {
                timer--;
            } else {
                timer = 0;
            }
        }

        if (hasHeatBelow()) {
            if (temperature < ConfigSetup.maxBoilerTemperature.get()) {
                temperature += ConfigSetup.boilerRiseTemperature.get();
            }
        } else {
            if (temperature > ConfigSetup.ambientBoilerTemperature.get()) {
                temperature -= ConfigSetup.boilerCoolTemperature.get();
                if (temperature < ConfigSetup.ambientBoilerTemperature.get()) {
                    temperature = (float) ConfigSetup.ambientBoilerTemperature.get();
                }
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

    public void setTimer(double timer) {
        this.timer = timer;
    }

    public double getTimer() {
        return timer;
    }

    private boolean isHot(BlockPos p) {
        IBlockState state = getWorld().getBlockState(p);
        Block block = state.getBlock();
        if (block == Blocks.FIRE) {
            return true;
        } else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
            return true;
        } else if (block == Blocks.MAGMA) {
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
