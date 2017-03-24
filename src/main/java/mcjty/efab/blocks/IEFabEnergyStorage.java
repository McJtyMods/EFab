package mcjty.efab.blocks;

import net.minecraft.util.EnumFacing;

public interface IEFabEnergyStorage {

    boolean extractEnergy(int amount);

    int getEnergyStored(EnumFacing from);

    int getMaxEnergyStored(EnumFacing from);
}
