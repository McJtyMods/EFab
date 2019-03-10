package mcjty.efab.blocks.rfstorage;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.IEFabEnergyStorage;
import mcjty.efab.config.GeneralConfiguration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class RFStorageTE extends GenericEFabTile implements IEFabEnergyStorage {

    private int energy = 0;

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        energy = tagCompound.getInteger("energy");
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("energy", energy);
    }

    protected int getMaxStorage() {
        return GeneralConfiguration.rfStorageMax.get();
    }

    @Override
    public int getMaxInternalConsumption() {
        return GeneralConfiguration.rfStorageInternalFlow.get();
    }

    @Override
    public boolean extractEnergy(int amount) {
        if (amount > energy) {
            return false;
        }
        energy -= amount;
        markDirtyQuick();
        return true;
    }

    private int receiveEnergyInternal(int maxReceive, boolean simulate) {
        int toreceive = Math.min(maxReceive, GeneralConfiguration.rfStorageInputPerTick.get());
        int newenergy = energy + toreceive;
        if (newenergy > getMaxStorage()) {
            toreceive -= newenergy - getMaxStorage();
            newenergy = getMaxStorage();
        }
        if (!simulate && energy != newenergy) {
            energy = newenergy;
            markDirtyQuick();
        }
        return toreceive;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return energy;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return getMaxStorage();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    private IEnergyStorage handler = null;

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            if (handler == null) {
                handler = new IEnergyStorage() {
                    @Override
                    public int receiveEnergy(int maxReceive, boolean simulate) {
                        return receiveEnergyInternal(maxReceive, simulate);
                    }

                    @Override
                    public int extractEnergy(int maxExtract, boolean simulate) {
                        return 0;
                    }

                    @Override
                    public int getEnergyStored() {
                        return RFStorageTE.this.energy;
                    }

                    @Override
                    public int getMaxEnergyStored() {
                        return getMaxStorage();
                    }

                    @Override
                    public boolean canExtract() {
                        return false;
                    }

                    @Override
                    public boolean canReceive() {
                        return true;
                    }
                };
            }
            return CapabilityEnergy.ENERGY.cast(handler);
        }
        return super.getCapability(capability, facing);
    }

}
