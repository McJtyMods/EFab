package mcjty.efab.blocks.storage;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class StorageTE extends GenericEFabTile implements DefaultSidedInventory {

    private static int[] SLOTS = null;
    private InventoryHelper inventoryHelper = new InventoryHelper(this, StorageContainer.factory, 9 * 3);
    private String name;

    public static final Key<String> VALUE_CRAFTING_NAME = new Key<>("name", Type.STRING);

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_CRAFTING_NAME, this::getCraftingName, this::setCraftingName),
        };
    }

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
        if (name != null) {
            tagCompound.setString("name", name);
        }
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
