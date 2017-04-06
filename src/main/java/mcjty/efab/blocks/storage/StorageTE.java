package mcjty.efab.blocks.storage;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class StorageTE extends GenericEFabTile implements DefaultSidedInventory {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, StorageContainer.factory, 9 * 3);

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
