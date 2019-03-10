package mcjty.efab.blocks.manareceptacle;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.ISpeedBooster;
import mcjty.efab.config.GeneralConfiguration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import vazkii.botania.api.mana.IManaReceiver;

public class ManaReceptacleTE extends GenericEFabTile implements IManaReceiver, ITickable, ISpeedBooster {

    private int mana = 0;

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
            speed -= GeneralConfiguration.manaRotationSpinDown.get();
            if (speed < 1.0f) {
                speed = 1.0f;
            }
            markDirtyQuick();
        }
        if (speedBoost > 0) {
            speedBoost--;
            speed += GeneralConfiguration.manaRotationSpeedUp.get();
            if (speed > GeneralConfiguration.maxManaRotationSpeed.get()) {
                speed = (float) GeneralConfiguration.maxManaRotationSpeed.get();
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

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        mana = tagCompound.getInteger("mana");
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("mana", mana);
    }

    @Override
    public boolean isFull() {
        return mana >= GeneralConfiguration.maxMana.get();
    }

    @Override
    public void recieveMana(int mana) {
        this.mana = Math.min(GeneralConfiguration.maxMana.get(), this.mana + mana);
        markDirtyQuick();
    }

    public void consumeMana(int mana) {
        this.mana = Math.max(0, this.mana - mana);
        markDirtyQuick();
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return !isFull();
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }
}
