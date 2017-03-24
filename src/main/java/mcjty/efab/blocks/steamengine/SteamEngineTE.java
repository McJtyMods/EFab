package mcjty.efab.blocks.steamengine;

import mcjty.efab.blocks.GenericEFabTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class SteamEngineTE extends GenericEFabTile implements ITickable {

    public static final float MAX_SPEED = 30.0f;

    private float speed = 1.0f;
    private int speedBoost = 0;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        markDirtyClient();
    }

    public int getSpeedBoost() {
        return speedBoost;
    }

    public void setSpeedBoost(int speedBoost) {
        this.speedBoost = speedBoost;
        markDirtyClient();
    }

    @Override
    public void update() {
        if (speed > 1.0f) {
            speed -= 0.1f;
            if (speed > 1.0f) {
                speed = 1.0f;
            }
            markDirtyQuick();
        }
        if (speedBoost > 0) {
            speedBoost--;
            speed += 1f;
            if (speed < MAX_SPEED) {
                speed = MAX_SPEED;
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
