package mcjty.efab.blocks.grid;

import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.recipes.IEFabRecipe;
import mcjty.efab.recipes.RecipeManager;
import mcjty.efab.recipes.VanillaRecipeAdapter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GridCrafterHelper {

    private final IInventory inventory;
    private final InventoryCrafting workInventory = new InventoryCrafting(new Container() {
        @SuppressWarnings("NullableProblems")
        @Override
        public boolean canInteractWith(EntityPlayer var1) {
            return false;
        }
    }, 3, 3);

    private ItemStack craftingOutput = ItemStack.EMPTY;

    private IRecipe vanillaRecipe = null;       // A vanilla recipe if possible
    private boolean vanillaRecipeValid = false;

    // This itemstack is the last itemstack for which we calculated findRecipeForOutput so it acts like
    // a cache. If it is null then the cache is invalid
    // This cache is for the efab recipes. Vanilla recipes are cached in the vanillaRecipe* fields
    @Nullable private ItemStack cachedItemStackForRecipe = null;
    @Nullable private IEFabRecipe cachedRecipe = null;


    // Client side only and contains the last outputs from the server
    private List<ItemStack> outputsFromServer = Collections.emptyList();

    public GridCrafterHelper(IInventory inventory) {
        this.inventory = inventory;
    }

    public InventoryCrafting getWorkInventory() {
        return workInventory;
    }

    public void invalidateCache() {
        vanillaRecipe = null;
        vanillaRecipeValid = false;
        cachedItemStackForRecipe = null;
        cachedRecipe = null;
    }

    @Nullable
    public IEFabRecipe findRecipeForOutput(ItemStack output, World world) {
        if (cachedItemStackForRecipe != null &&
                ((cachedItemStackForRecipe.isEmpty() && output.isEmpty()) ||
                        mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(output, cachedItemStackForRecipe))) {
            return cachedRecipe;
        }

        cachedItemStackForRecipe = output;
        List<IEFabRecipe> recipes = findCurrentRecipes(world);
        for (IEFabRecipe recipe : recipes) {
            if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(output, recipe.cast().getRecipeOutput())) {
                cachedRecipe = recipe;
                return recipe;
            }
        }
        cachedRecipe = null;
        return null;
    }

    /**
     * Return all current outputs with the first outputs the ones that are actually possible
     * given current configuration
     */
    @Nonnull
    public List<ItemStack> getOutputs(World world) {
        if (world.isRemote) {
            return outputsFromServer;
        } else {
            return findCurrentRecipes(world)
                    .stream()
                    .map(r -> r.cast().getRecipeOutput())
                    .collect(Collectors.toList());
        }
    }

    public void syncFromServer(List<ItemStack> outputs) {
        outputsFromServer = outputs;
    }

    public void abortCraft() {
        craftingOutput = ItemStack.EMPTY;
    }

    public ItemStack getCraftingOutput() {
        return craftingOutput;
    }

    public void setCraftingOutput(ItemStack craftingOutput) {
        this.craftingOutput = craftingOutput;
    }

    @Nonnull
    public List<IEFabRecipe> findCurrentRecipes(World world) {
        for (int i = 0; i < 9; i++) {
            workInventory.setInventorySlotContents(i, inventory.getStackInSlot(i));
        }
        List<IEFabRecipe> validRecipes = RecipeManager.findValidRecipes(workInventory, world);
        if (validRecipes.isEmpty() && GeneralConfiguration.vanillaCraftingAllowed) {
            // Try vanilla
            if (!vanillaRecipeValid) {
                vanillaRecipe = CraftingManager.findMatchingRecipe(workInventory, world);
                vanillaRecipeValid = true;
            }
            if (vanillaRecipe != null) {
                validRecipes.add(new VanillaRecipeAdapter(vanillaRecipe));
            }

        }
        return validRecipes;
    }

    public boolean checkRoomForOutput(ItemStack output, int start, int stop) {
        for (int i = start ; i < stop ; i++) {
            ItemStack currentStack = inventory.getStackInSlot(i);
            if (currentStack.isEmpty()) {
                return true;
            } else if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(currentStack, output)) {
                int remaining = currentStack.getMaxStackSize() - currentStack.getCount();
                if (remaining >= output.getCount()) {
                    return true;
                }
                int amount = -remaining;
                output.grow(amount);
            }
        }
        return false;
    }

    // This function assumes there is room (i.e. check with checkRoomForOutput first)
    public void insertOutput(ItemStack output, int start, int stop) {
        for (int i = start ; i < stop ; i++) {
            ItemStack currentStack = inventory.getStackInSlot(i);
            if (currentStack.isEmpty()) {
                inventory.setInventorySlotContents(i, output);
                return;
            } else if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(currentStack, output)) {
                int remaining = currentStack.getMaxStackSize() - currentStack.getCount();
                if (remaining >= output.getCount()) {
                    int amount = output.getCount() + currentStack.getCount();
                    if (amount <= 0) {
                        output.setCount(0);
                    } else {
                        output.setCount(amount);
                    }
                    inventory.setInventorySlotContents(i, output);
                    return;
                } else {
                    int amount = currentStack.getMaxStackSize();
                    if (amount <= 0) {
                        currentStack.setCount(0);
                    } else {
                        currentStack.setCount(amount);
                    }
                    inventory.setInventorySlotContents(i, currentStack);
                }
                int amount = -remaining;
                output.grow(amount);
            }
        }
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey("output")) {
            craftingOutput = new ItemStack(tagCompound.getCompoundTag("output"));
        } else {
            craftingOutput = ItemStack.EMPTY;
        }
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        if (!craftingOutput.isEmpty()) {
            NBTTagCompound out = new NBTTagCompound();
            craftingOutput.writeToNBT(out);
            tagCompound.setTag("output", out);
        }
    }
}

