package mcjty.efab.blocks.grid;

import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.sound.ISoundProducer;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class GridTE extends GenericTileEntity implements DefaultSidedInventory, ISoundProducer, ITickable {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, GridContainer.factory, 9 + 3);

    @Override
    public void update() {

    }

    private void updateMachineSound() {
        if (GeneralConfiguration.baseMachineVolume > 0.01f) {
//            int boilingState = getBoilingState();
//            if (boilingState >= 1) {
//                float vol = (boilingState-1.0f)/9.0f;
//                if (!SoundController.isBoilingPlaying(getWorld(), pos)) {
//                    SoundController.playBoiling(getWorld(), getPos(), vol);
//                } else {
//                    SoundController.updateVolume(getWorld(), getPos(), vol);
//                }
//            } else {
//                SoundController.stopSound(getWorld(), getPos());
//            }
        }
    }



    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }


    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public boolean isUsable(EntityPlayer player) {
        return canPlayerAccess(player);
    }


}
