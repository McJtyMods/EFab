package mcjty.efab.blocks.crafter;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.ISpeedBooster;
import mcjty.efab.blocks.grid.GridTE;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.recipes.IEFabRecipe;
import mcjty.efab.recipes.RecipeManager;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.Argument;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CrafterTE extends GenericEFabTile implements DefaultSidedInventory, ITickable, ISpeedBooster {

    public static final String CMD_LEFT = "left";
    public static final String CMD_RIGHT = "right";

    public static final int[] SLOTS = new int[]{CrafterContainer.SLOT_CRAFTOUTPUT, CrafterContainer.SLOT_CRAFTOUTPUT + 1, CrafterContainer.SLOT_CRAFTOUTPUT + 2};

    private InventoryHelper inventoryHelper = new InventoryHelper(this, CrafterContainer.factory, 9 + 3 + 1);
    private InventoryCrafting workInventory = new InventoryCrafting(new Container() {
        @SuppressWarnings("NullableProblems")
        @Override
        public boolean canInteractWith(EntityPlayer var1) {
            return false;
        }
    }, 3, 3);

    private float speed = 1.0f;
    private int speedBoost = 0;

    private int ticksRemaining = -1;
    private int totalTicks = 0;
    private ItemStack craftingOutput = ItemStackTools.getEmptyStack();

    // Client side only and contains the last outputs from the server
    private List<ItemStack> outputsFromServer = Collections.emptyList();

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
        markDirtyClient();
    }

    @Override
    public int getSpeedBoost() {
        return speedBoost;
    }

    @Override
    public void setSpeedBoost(int speedBoost) {
        this.speedBoost = speedBoost;
        markDirtyClient();
    }

    @Override
    public void update() {
        if (speed > 1.0f) {
            speed -= GeneralConfiguration.craftAnimationSpinDown;
            if (speed < 1.0f) {
                speed = 1.0f;
            }
            markDirtyQuick();
        }
        if (speedBoost > 0) {
            speedBoost--;
            speed += GeneralConfiguration.craftAnimationSpeedUp;
            if (speed > GeneralConfiguration.maxCraftAnimationSpeed) {
                speed = GeneralConfiguration.maxCraftAnimationSpeed;
            }
            markDirtyQuick();
        }
    }

    private ItemStack getCurrentOutput(@Nullable IEFabRecipe recipe) {
        if (recipe == null) {
            return ItemStackTools.getEmptyStack();
        } else {
            return recipe.cast().getCraftingResult(workInventory);
        }
    }

    @Nonnull
    private List<IEFabRecipe> findCurrentRecipes() {
        for (int i = 0; i < 9; i++) {
            workInventory.setInventorySlotContents(i, inventoryHelper.getStackInSlot(i));
        }
        return RecipeManager.findValidRecipes(workInventory, getWorld());
    }

    @Nullable
    private IEFabRecipe findRecipeForOutput(ItemStack output) {
        List<IEFabRecipe> recipes = findCurrentRecipes();
        for (IEFabRecipe recipe : recipes) {
            if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(output, recipe.cast().getRecipeOutput())) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Return all current outputs with the first outputs the ones that are actually possible
     * given current configuration
     */
    @Nonnull
    public List<ItemStack> getOutputs() {
        if (getWorld().isRemote) {
            return outputsFromServer;
        } else {
            return findCurrentRecipes()
                    .stream()
                    .map(r -> r.cast().getRecipeOutput())
                    .collect(Collectors.toList());
        }
    }

    private void abortCraft() {
        ticksRemaining = -1;
        craftingOutput = ItemStackTools.getEmptyStack();
    }

    public boolean isCrafting() {
        return ItemStackTools.isValid(craftingOutput);
    }

    public void handleCraft(GridTE grid) {
        if (ticksRemaining >= 0) {
            markDirtyQuick();

            IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());
            if (recipe == null) {
                abortCraft();
                return;
            }

            ticksRemaining--;
            if (totalTicks - ticksRemaining < 2) {
                // Send to client so it knows that the craft is progressing and that ticksRemaining is no longer equal to totalTicks
                markDirtyClient();
            }

            if (ticksRemaining % 20 == 0 || ticksRemaining < 0) {
                // Every 20 ticks we check if the inventory still matches what we want to craft
                if (!ItemStack.areItemsEqual(craftingOutput, getCurrentOutput(recipe))) {
                    // Reset craft
                    abortCraft();
                    return;
                }
            }

            if (ticksRemaining < 0) {
                craftFinished(recipe, grid);
            } else {
                GridTE.CraftProgressResult result = grid.craftInProgress(recipe);
                if (result == GridTE.CraftProgressResult.WAIT) {
                    ticksRemaining--;
                } else if (result == GridTE.CraftProgressResult.ABORT) {
                    abortCraft();
                }
            }
        }
    }

    private void craftFinished(@Nonnull IEFabRecipe recipe, GridTE grid) {
        ticksRemaining = -1;
        markDirtyQuick();
        // Craft finished. Consume items and do the actual crafting. If there is no room to place
        // the craft result then nothing happens

        if (!checkRoomForOutput(craftingOutput.copy())) {
            // Not enough room. Abort craft
            return;
        }

        if (grid.checkFinalCraftRequirements(recipe)) {
            return;
        }

        List<ItemStack> ingredients = grid.condenseIngredients(workInventory);
        if (!grid.checkIngredients(ingredients)) {
            // Ingredients are missing
            return;
        }
        grid.consumeIngredients(ingredients);

        insertOutput(craftingOutput.copy());
        craftingOutput = ItemStackTools.getEmptyStack();
    }

    // @todo common with GridTE?
    private boolean checkRoomForOutput(ItemStack output) {
        for (int i = CrafterContainer.SLOT_CRAFTOUTPUT; i < CrafterContainer.SLOT_CRAFTOUTPUT + 3; i++) {
            ItemStack currentStack = getStackInSlot(i);
            if (ItemStackTools.isEmpty(currentStack)) {
                return true;
            } else if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(currentStack, output)) {
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
        for (int i = CrafterContainer.SLOT_CRAFTOUTPUT; i < CrafterContainer.SLOT_CRAFTOUTPUT + 3; i++) {
            ItemStack currentStack = getStackInSlot(i);
            if (ItemStackTools.isEmpty(currentStack)) {
                setInventorySlotContents(i, output);
                return;
            } else if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(currentStack, output)) {
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

    public boolean startCraft(GridTE grid) {
        IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());
        if (recipe != null) {
            boolean error = grid.getErrorsForOutput(recipe, null);
            if (error) {
                return false; // Don't start
            }
            List<ItemStack> ingredients = grid.condenseIngredients(workInventory);
            if (!grid.checkIngredients(ingredients)) {
                return false;
            }

            craftingOutput = getCurrentOutput(recipe);
            ticksRemaining = recipe.getCraftTime();
            totalTicks = recipe.getCraftTime();
            markDirtyQuick();
            return true;
        }
        return false;
    }


    /**
     * Set the ghost output slot to one of the possible outputs for the current
     * grid. If the output is already one of the possible outputs then nothing happens
     */
    private void setValidRecipeGhostOutput() {
        ItemStack current = inventoryHelper.getStackInSlot(CrafterContainer.SLOT_GHOSTOUT);
        List<IEFabRecipe> recipes = findCurrentRecipes();
        if (ItemStackTools.isEmpty(current)) {
            if (!recipes.isEmpty()) {
                inventoryHelper.setStackInSlot(CrafterContainer.SLOT_GHOSTOUT, recipes.get(0).cast().getRecipeOutput());
                markDirtyQuick();
            }
        } else {
            if (recipes.isEmpty()) {
                inventoryHelper.setStackInSlot(CrafterContainer.SLOT_GHOSTOUT, ItemStackTools.getEmptyStack());
                markDirtyQuick();
            } else {
                for (IEFabRecipe recipe : recipes) {
                    if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(current, recipe.cast().getRecipeOutput())) {
                        return; // Ok, already present
                    }
                }
                inventoryHelper.setStackInSlot(CrafterContainer.SLOT_GHOSTOUT, recipes.get(0).cast().getRecipeOutput());
                markDirtyQuick();
            }
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        getInventoryHelper().setInventorySlotContents(getInventoryStackLimit(), index, stack);
        if (index >= CrafterContainer.SLOT_CRAFTINPUT && index < CrafterContainer.SLOT_CRAFTOUTPUT) {
            setValidRecipeGhostOutput();
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getInventoryHelper().decrStackSize(index, count);
        if (index >= CrafterContainer.SLOT_CRAFTINPUT && index < CrafterContainer.SLOT_CRAFTOUTPUT) {
            setValidRecipeGhostOutput();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = getInventoryHelper().removeStackFromSlot(index);
        if (index >= CrafterContainer.SLOT_CRAFTINPUT && index < CrafterContainer.SLOT_CRAFTOUTPUT) {
            setValidRecipeGhostOutput();
        }
        return stack;
    }


    public void setGridContents(List<ItemStack> stacks) {
        setInventorySlotContents(CrafterContainer.SLOT_GHOSTOUT, stacks.get(0));
        for (int i = 1 ; i < stacks.size() ; i++) {
            setInventorySlotContents(CrafterContainer.SLOT_CRAFTINPUT + i-1, stacks.get(i));
        }
    }

    // Called client-side only
    public void syncFromServer(List<ItemStack> outputs) {
        outputsFromServer = outputs;
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
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index >= CrafterContainer.SLOT_CRAFTOUTPUT && index < CrafterContainer.SLOT_GHOSTOUT;
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
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        speed = tagCompound.getFloat("speed");
        speedBoost = tagCompound.getInteger("boost");
        ticksRemaining = tagCompound.getInteger("ticks");
        totalTicks = tagCompound.getInteger("total");
        if (tagCompound.hasKey("output")) {
            craftingOutput = ItemStackTools.loadFromNBT(tagCompound.getCompoundTag("output"));
        } else {
            craftingOutput = ItemStackTools.getEmptyStack();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setFloat("speed", speed);
        tagCompound.setInteger("boost", speedBoost);
        tagCompound.setInteger("ticks", ticksRemaining);
        tagCompound.setInteger("total", totalTicks);
        if (ItemStackTools.isValid(craftingOutput)) {
            NBTTagCompound out = new NBTTagCompound();
            craftingOutput.writeToNBT(out);
            tagCompound.setTag("output", out);
        }
        return super.writeToNBT(tagCompound);
    }

    private void left() {
        List<IEFabRecipe> sorted = findCurrentRecipes();
        OptionalInt first = findCurrentGhost(sorted);
        if (first.isPresent()) {
            int i = (first.getAsInt() - 1 + sorted.size()) % sorted.size();
            IEFabRecipe recipe = sorted.get(i);
            setInventorySlotContents(CrafterContainer.SLOT_GHOSTOUT, recipe.cast().getRecipeOutput());
            markDirtyQuick();
        }
    }

    private void right() {
        List<IEFabRecipe> sorted = findCurrentRecipes();
        OptionalInt first = findCurrentGhost(sorted);
        if (first.isPresent()) {
            int i = (first.getAsInt() + 1) % sorted.size();
            IEFabRecipe recipe = sorted.get(i);
            setInventorySlotContents(CrafterContainer.SLOT_GHOSTOUT, recipe.cast().getRecipeOutput());
            markDirtyQuick();
        }
    }

    private OptionalInt findCurrentGhost(List<IEFabRecipe> sorted) {
        ItemStack ghostOutput = getCurrentGhostOutput();
        return IntStream.range(0, sorted.size())
                .filter(i -> mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(sorted.get(i).cast().getRecipeOutput(), ghostOutput))
                .findFirst();
    }

    private ItemStack getCurrentGhostOutput() {
        return getStackInSlot(CrafterContainer.SLOT_GHOSTOUT);
    }



    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return rc;
        }
        if (CMD_LEFT.equals(command)) {
            left();
            return true;
        } else if (CMD_RIGHT.equals(command)) {
            right();
            return true;
        }
        return false;
    }
}
