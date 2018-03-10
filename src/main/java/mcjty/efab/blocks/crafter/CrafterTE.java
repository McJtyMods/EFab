package mcjty.efab.blocks.crafter;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.ISpeedBooster;
import mcjty.efab.blocks.grid.GridCrafterHelper;
import mcjty.efab.blocks.grid.GridTE;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.recipes.IEFabRecipe;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.Argument;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.IntStream;


public class CrafterTE extends GenericEFabTile implements DefaultSidedInventory, ITickable, ISpeedBooster {

    public static final String CMD_LEFT = "left";
    public static final String CMD_RIGHT = "right";

    public static final int[] SLOTS = new int[]{CrafterContainer.SLOT_CRAFTOUTPUT, CrafterContainer.SLOT_CRAFTOUTPUT + 1, CrafterContainer.SLOT_CRAFTOUTPUT + 2};

    private InventoryHelper inventoryHelper = new InventoryHelper(this, CrafterContainer.factory, 9 + 3 + 1);

    private float speed = 1.0f;
    private int speedBoost = 0;

    private int ticksRemaining = -1;
    private int totalTicks = 0;

    private String lastError = "";

    // These two variables are used client side only for animation
    private float cnt = 0;
    private float cnt2 = 0;

    private final GridCrafterHelper crafterHelper = new GridCrafterHelper(this);

    public float getCnt() {
        return cnt;
    }

    public void setCnt(float cnt) {
        this.cnt = cnt;
    }

    public float getCnt2() {
        return cnt2;
    }

    public void setCnt2(float cnt2) {
        this.cnt2 = cnt2;
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    public boolean isOn() {
        return powerLevel > 0;
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
            return ItemStack.EMPTY;
        } else {
            return recipe.cast().getCraftingResult(crafterHelper.getWorkInventory());
        }
    }

    @Nullable
    private IEFabRecipe findRecipeForOutput(ItemStack output) {
        List<IEFabRecipe> recipes = crafterHelper.findCurrentRecipes(getWorld());
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
        return crafterHelper.getOutputs(getWorld());
    }

    private void abortCraft() {
        ticksRemaining = -1;
        crafterHelper.abortCraft();
    }

    public boolean isCrafting() {
        return ticksRemaining >= 0 && !crafterHelper.getCraftingOutput().isEmpty();
    }

    public int getProgress() {
        if (totalTicks == 0) {
            return 100;
        }
        return (totalTicks - ticksRemaining) * 100 / totalTicks;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public void handleCraft(GridTE grid) {
        if (ticksRemaining >= 0) {
            markDirtyQuick();

            IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());
            if (recipe == null) {
                lastError = "No recipe";
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
                if (!ItemStack.areItemsEqual(crafterHelper.getCraftingOutput(), getCurrentOutput(recipe))) {
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

    private List<ItemStack> condenseIngredients(InventoryCrafting workInventory) {
        List<ItemStack> ingredients = new ArrayList<>();
        // @todo optimize and cache this?
        for (int i = 0 ; i < workInventory.getSizeInventory() ; i++) {
            ItemStack stack = workInventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                boolean found = false;
                for (ItemStack ingredient : ingredients) {
                    if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(stack, ingredient)) {
                        ingredient.grow(stack.getCount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ingredients.add(stack.copy());
                }
            }
        }
        return ingredients;
    }


    private void craftFinished(@Nonnull IEFabRecipe recipe, GridTE grid) {
        lastError = "";
        ticksRemaining = -1;
        markDirtyQuick();
        // Craft finished. Consume items and do the actual crafting. If there is no room to place
        // the craft result then nothing happens

        if (!checkRoomForOutput(crafterHelper.getCraftingOutput().copy())) {
            // Not enough room. Abort craft
            return;
        }

        List<ItemStack> ingredients = condenseIngredients(crafterHelper.getWorkInventory());
        if (grid.checkFinalCraftRequirements(recipe, ingredients)) {
            return;
        }

        insertOutput(crafterHelper.getCraftingOutput().copy());
        crafterHelper.abortCraft();
    }

    private boolean checkRoomForOutput(ItemStack output) {
        return crafterHelper.checkRoomForOutput(output, CrafterContainer.SLOT_CRAFTOUTPUT, CrafterContainer.SLOT_CRAFTOUTPUT+3);
    }

    // This function assumes there is room (i.e. check with checkRoomForOutput first)
    private void insertOutput(ItemStack output) {
        crafterHelper.insertOutput(output, CrafterContainer.SLOT_CRAFTOUTPUT, CrafterContainer.SLOT_CRAFTOUTPUT+3);
    }

    public void startCraft(GridTE grid, IEFabRecipe recipe) {
        crafterHelper.setCraftingOutput(getCurrentOutput(recipe));
        int craftTime = recipe.getCraftTime() * grid.getSpeedBonus(recipe);
        ticksRemaining = craftTime;
        totalTicks = craftTime;
        markDirtyQuick();
    }

    public IEFabRecipe checkCraft(GridTE grid) {
        IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());
        if (recipe != null) {
            List<String> errors = new ArrayList<>();
            boolean error = grid.getErrorsForOutput(recipe, errors);
            if (error) {
                lastError = errors.get(0);
                return null; // Don't start
            }
            List<ItemStack> ingredients = condenseIngredients(crafterHelper.getWorkInventory());
            if (!grid.checkIngredients(ingredients)) {
                lastError = "Ingredients missing";
                return null;
            }

            if (!checkRoomForOutput(crafterHelper.getCraftingOutput().copy())) {
                // Not enough room. Abort craft
                lastError = "Not enough room for output";
                return null;
            }

            lastError = "";
            return recipe;
        } else {
            lastError = "No recipe";
        }
        return null;
    }

    /**
     * Set the ghost output slot to one of the possible outputs for the current
     * grid. If the output is already one of the possible outputs then nothing happens
     */
    private void setValidRecipeGhostOutput() {
        ItemStack current = inventoryHelper.getStackInSlot(CrafterContainer.SLOT_GHOSTOUT);
        List<IEFabRecipe> recipes = crafterHelper.findCurrentRecipes(getWorld());
        if (current.isEmpty()) {
            if (!recipes.isEmpty()) {
                inventoryHelper.setStackInSlot(CrafterContainer.SLOT_GHOSTOUT, recipes.get(0).cast().getRecipeOutput());
                markDirtyQuick();
            }
        } else {
            if (recipes.isEmpty()) {
                inventoryHelper.setStackInSlot(CrafterContainer.SLOT_GHOSTOUT, ItemStack.EMPTY);
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
            crafterHelper.invalidateCache();
            setValidRecipeGhostOutput();
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getInventoryHelper().decrStackSize(index, count);
        if (index >= CrafterContainer.SLOT_CRAFTINPUT && index < CrafterContainer.SLOT_CRAFTOUTPUT) {
            crafterHelper.invalidateCache();
            setValidRecipeGhostOutput();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = getInventoryHelper().removeStackFromSlot(index);
        if (index >= CrafterContainer.SLOT_CRAFTINPUT && index < CrafterContainer.SLOT_CRAFTOUTPUT) {
            crafterHelper.invalidateCache();
            setValidRecipeGhostOutput();
        }
        return stack;
    }


    public void setGridContents(List<ItemStack> stacks) {
        crafterHelper.invalidateCache();
        setInventorySlotContents(CrafterContainer.SLOT_GHOSTOUT, stacks.get(0));
        for (int i = 1 ; i < stacks.size() ; i++) {
            setInventorySlotContents(CrafterContainer.SLOT_CRAFTINPUT + i-1, stacks.get(i));
        }
    }

    // Called client-side only
    public void syncFromServer(List<String> errors, List<ItemStack> outputs) {
        lastError = errors.isEmpty() ? "" : errors.get(0);
        crafterHelper.syncFromServer(outputs);
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
        crafterHelper.readFromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setFloat("speed", speed);
        tagCompound.setInteger("boost", speedBoost);
        tagCompound.setInteger("ticks", ticksRemaining);
        tagCompound.setInteger("total", totalTicks);
        crafterHelper.writeToNBT(tagCompound);
        return super.writeToNBT(tagCompound);
    }

    private void left() {
        List<IEFabRecipe> sorted = crafterHelper.findCurrentRecipes(getWorld());
        OptionalInt first = findCurrentGhost(sorted);
        if (first.isPresent()) {
            int i = (first.getAsInt() - 1 + sorted.size()) % sorted.size();
            IEFabRecipe recipe = sorted.get(i);
            setInventorySlotContents(CrafterContainer.SLOT_GHOSTOUT, recipe.cast().getRecipeOutput());
            markDirtyQuick();
        }
    }

    private void right() {
        List<IEFabRecipe> sorted = crafterHelper.findCurrentRecipes(getWorld());
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
