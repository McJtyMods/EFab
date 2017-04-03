package mcjty.efab.blocks.manareceptacle;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.config.GeneralConfiguration;
import net.minecraft.nbt.NBTTagCompound;
import vazkii.botania.api.mana.IManaReceiver;

public class ManaReceptacleTE extends GenericEFabTile implements IManaReceiver {

    private int mana = 0;

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
        return mana >= GeneralConfiguration.maxMana;
    }

    @Override
    public void recieveMana(int mana) {
        this.mana = Math.min(GeneralConfiguration.maxMana, this.mana + mana);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return false;
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }
}
