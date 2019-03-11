package mcjty.efab.blocks.steamengine;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.ISpeedBooster;
import mcjty.efab.config.ConfigSetup;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class SteamEngineTE extends GenericEFabTile implements ITickable, ISpeedBooster {

    private float speed = 1.0f;
    private int speedBoost = 0;

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
        markDirtyClient();
    }

    @Override
    public int getSpeedBoost() {
        return speedBoost;
    }

    @Override
    public void setSpeedBoost(int speedBoost) {
        this.speedBoost = speedBoost;
        markDirtyClient();
    }

    @Override
    public void update() {
        if (speed > 1.0f) {
            speed -= ConfigSetup.steamWheelSpinDown.get();
            if (speed < 1.0f) {
                speed = 1.0f;
            }
            markDirtyQuick();
        }
        if (speedBoost > 0) {
            speedBoost--;
            speed += ConfigSetup.steamWheelSpeedUp.get();
            if (speed > ConfigSetup.maxSteamWheelSpeed.get()) {
                speed = (float) ConfigSetup.maxSteamWheelSpeed.get();
            }
            markDirtyQuick();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        speed = tagCompound.getFloat("speed");
        speedBoost = tagCompound.getInteger("boost");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setFloat("speed", speed);
        tagCompound.setInteger("boost", speedBoost);
        return super.writeToNBT(tagCompound);
    }
}
