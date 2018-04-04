package mcjty.efab.blocks.storage;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.Argument;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.Map;

public class StorageTE extends GenericEFabTile implements DefaultSidedInventory {

    public static final String CMD_SETNAME = "setName";

    private static int[] SLOTS = null;
    private InventoryHelper inventoryHelper = new InventoryHelper(this, StorageContainer.factory, 9 * 3);
    private String name;

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    public String getCraftingName() {
        return name == null ? "" : name;
    }

    public void setCraftingName(String name) {
        this.name = name;
        markDirtyClient();
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
        name = tagCompound.getString("name");
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
        tagCompound.setString("name", name);
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

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return rc;
        }
        if (CMD_SETNAME.equals(command)) {
            setCraftingName(args.get("name").getString());
            return true;
        }
        return false;
    }
}
