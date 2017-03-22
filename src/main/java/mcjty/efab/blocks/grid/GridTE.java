package mcjty.efab.blocks.grid;

import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.recipes.RecipeManager;
import mcjty.efab.sound.ISoundProducer;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import java.util.Collections;
import java.util.Map;

public class GridTE extends GenericTileEntity implements DefaultSidedInventory, ISoundProducer, ITickable {

    public static final String CMD_CRAFT = "craft";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, GridContainer.factory, 9 + 3 + 1);
    private InventoryCrafting workInventory = new InventoryCrafting(new Container() {
        @SuppressWarnings("NullableProblems")
        @Override
        public boolean canInteractWith(EntityPlayer var1) {
            return false;
        }
    }, 3, 3);

    @Override
    public void update() {
        // @todo only do this when gui is open?
        if (!getWorld().isRemote) {
            for (int i = 0 ; i < 9 ; i++) {
                workInventory.setInventorySlotContents(i, inventoryHelper.getStackInSlot(i));
            }
            IRecipe validRecipe = RecipeManager.findValidRecipe(workInventory, getWorld(), Collections.emptySet());
            if (validRecipe != null) {
                ItemStack craftingResult = validRecipe.getCraftingResult(workInventory);
                inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, craftingResult);
            } else {
                inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, ItemStackTools.getEmptyStack());
            }
        }
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

    private void startCraft() {

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

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return rc;
        }
        if (CMD_CRAFT.equals(command)) {
            startCraft();
            return true;
        }
        return false;
    }
}
