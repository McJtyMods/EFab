package mcjty.efab.blocks.storage;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.grid.GridContainer;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class StorageTE extends GenericEFabTile implements DefaultSidedInventory {

    private static int[] SLOTS = null;
    private InventoryHelper inventoryHelper = new InventoryHelper(this, StorageContainer.factory, 9 * 3);

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
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
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (SLOTS == null) {
            SLOTS = new int[inventoryHelper.getCount()];
            for (int i = 0 ; i < inventoryHelper.getCount() ; i++) {
                SLOTS[i] = i;
            }
        }
        return SLOTS;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }


}
