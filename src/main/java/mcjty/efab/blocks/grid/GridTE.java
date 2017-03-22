package mcjty.efab.blocks.grid;

import mcjty.efab.EFab;
import mcjty.efab.blocks.network.EFabMessages;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.recipes.IEFabRecipe;
import mcjty.efab.recipes.RecipeManager;
import mcjty.efab.sound.ISoundProducer;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.network.PacketRequestIntegerFromServer;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class GridTE extends GenericTileEntity implements DefaultSidedInventory, ISoundProducer, ITickable {

    public static final String CMD_CRAFT = "craft";
    public static final String CMD_GETPROGRESS = "getProgress";
    public static final String CLIENTCMD_GETPROGRESS = "getProgress";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, GridContainer.factory, 9 + 3 + 1);
    private InventoryCrafting workInventory = new InventoryCrafting(new Container() {
        @SuppressWarnings("NullableProblems")
        @Override
        public boolean canInteractWith(EntityPlayer var1) {
            return false;
        }
    }, 3, 3);

    private int ticksRemaining = 0;
    private ItemStack craftingOutput = ItemStackTools.getEmptyStack();

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (ticksRemaining > 0) {
                markDirtyQuick();

                ticksRemaining--;

                if (ticksRemaining % 20 == 0 || ticksRemaining <= 0) {
                    // Every 20 ticks we check if the inventory still matches what we want to craft
                    if (!ItemStack.areItemsEqual(craftingOutput, getCurrentOutput())) {
                        // Reset craft
                        ticksRemaining = 0;
                        craftingOutput = ItemStackTools.getEmptyStack();
                        return;
                    }
                }

                if (ticksRemaining <= 0) {
                    ticksRemaining = 0;
                    // Craft finished. Consume items and do the actual crafting. If there is no room to place
                    // the craft result then nothing happens

                    if (!checkRoomForOutput(craftingOutput.copy())) {
                        // Not enough room. Abort craft
                        return;
                    }

                    insertOutput(craftingOutput.copy());

                    // Consume items
                    for (int i = GridContainer.SLOT_CRAFTINPUT ; i < GridContainer.SLOT_CRAFTOUTPUT ; i++) {
                        decrStackSize(i, 1);
                    }
                }
            }
        }
    }

    private boolean checkRoomForOutput(ItemStack output) {
        for (int i = GridContainer.SLOT_CRAFTOUTPUT; i < GridContainer.SLOT_CRAFTOUTPUT + 3 ; i++) {
            ItemStack currentStack = getStackInSlot(i);
            if (ItemStackTools.isEmpty(currentStack)) {
                return true;
            } else if (isItemStackConsideredEqual(currentStack, output)) {
                int remaining = currentStack.getMaxStackSize() - ItemStackTools.getStackSize(currentStack);
                if (remaining >= ItemStackTools.getStackSize(output)) {
                    return true;
                }
                ItemStackTools.incStackSize(output, -remaining);
            }
        }
        return false;
    }

    // This function assumes there is room (i.e. check with checkRoomForOutput first)
    private void insertOutput(ItemStack output) {
        for (int i = GridContainer.SLOT_CRAFTOUTPUT; i < GridContainer.SLOT_CRAFTOUTPUT + 3 ; i++) {
            ItemStack currentStack = getStackInSlot(i);
            if (ItemStackTools.isEmpty(currentStack)) {
                setInventorySlotContents(i, output);
                return;
            } else if (isItemStackConsideredEqual(currentStack, output)) {
                int remaining = currentStack.getMaxStackSize() - ItemStackTools.getStackSize(currentStack);
                if (remaining >= ItemStackTools.getStackSize(output)) {
                    ItemStackTools.setStackSize(output, ItemStackTools.getStackSize(output) + ItemStackTools.getStackSize(currentStack));
                    setInventorySlotContents(i, output);
                    return;
                } else {
                    ItemStackTools.setStackSize(currentStack, currentStack.getMaxStackSize());
                    setInventorySlotContents(i, currentStack);
                }
                ItemStackTools.incStackSize(output, -remaining);
            }
        }
    }

    private static boolean isItemStackConsideredEqual(ItemStack result, ItemStack itemstack1) {
        return ItemStackTools.isValid(itemstack1) && itemstack1.getItem() == result.getItem() && (!result.getHasSubtypes() || result.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(result, itemstack1);
    }

    private void setRecipeGhostOutput() {
        inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, getCurrentOutput());
    }

    private ItemStack getCurrentOutput() {
        return getCurrentOutput(findCurrentRecipe());
    }

    private ItemStack getCurrentOutput(@Nullable IEFabRecipe recipe) {
        if (recipe == null) {
            return ItemStackTools.getEmptyStack();
        } else {
            return recipe.cast().getCraftingResult(workInventory);
        }
    }

    private IEFabRecipe findCurrentRecipe() {
        for (int i = 0 ; i < 9 ; i++) {
            workInventory.setInventorySlotContents(i, inventoryHelper.getStackInSlot(i));
        }
        return RecipeManager.findValidRecipe(workInventory, getWorld(), Collections.emptySet());
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        getInventoryHelper().setInventorySlotContents(getInventoryStackLimit(), index, stack);
        if (index >= GridContainer.SLOT_CRAFTINPUT && index < GridContainer.SLOT_CRAFTOUTPUT) {
            setRecipeGhostOutput();
            // We need to update the visual crafting grid client side
            markDirtyClient();
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getInventoryHelper().decrStackSize(index, count);
        if (index >= GridContainer.SLOT_CRAFTINPUT && index < GridContainer.SLOT_CRAFTOUTPUT) {
            setRecipeGhostOutput();
            // We need to update the visual crafting grid client side
            markDirtyClient();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = getInventoryHelper().removeStackFromSlot(index);
        if (index >= GridContainer.SLOT_CRAFTINPUT && index < GridContainer.SLOT_CRAFTOUTPUT) {
            setRecipeGhostOutput();
            // We need to update the visual crafting grid client side
            markDirtyClient();
        }
        return stack;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index != GridContainer.SLOT_GHOSTOUT;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index != GridContainer.SLOT_GHOSTOUT;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index != GridContainer.SLOT_GHOSTOUT;
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
        IEFabRecipe recipe = findCurrentRecipe();
        if (recipe != null) {
            craftingOutput = getCurrentOutput(recipe);
            ticksRemaining = recipe.getCraftTime();
            markDirtyQuick();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        ticksRemaining = tagCompound.getInteger("ticks");
        if (tagCompound.hasKey("output")) {
            craftingOutput = ItemStackTools.loadFromNBT(tagCompound.getCompoundTag("output"));
        } else {
            craftingOutput = ItemStackTools.getEmptyStack();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("ticks", ticksRemaining);
        if (ItemStackTools.isValid(craftingOutput)) {
            NBTTagCompound out = new NBTTagCompound();
            craftingOutput.writeToNBT(out);
            tagCompound.setTag("output", out);
        }
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

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public void requestProgressFromServer() {
        EFabMessages.INSTANCE.sendToServer(new PacketRequestIntegerFromServer(EFab.MODID, getPos(),
                CMD_GETPROGRESS,
                CLIENTCMD_GETPROGRESS));
    }

    @Override
    public Integer executeWithResultInteger(String command, Map<String, Argument> args) {
        Integer rc = super.executeWithResultInteger(command, args);
        if (rc != null) {
            return rc;
        }
        if (CMD_GETPROGRESS.equals(command)) {
            return ticksRemaining;
        }
        return null;
    }

    @Override
    public boolean execute(String command, Integer result) {
        boolean rc = super.execute(command, result);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_GETPROGRESS.equals(command)) {
            ticksRemaining = result;
            return true;
        }
        return false;
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
