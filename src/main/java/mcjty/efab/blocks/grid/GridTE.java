package mcjty.efab.blocks.grid;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.blocks.IEFabEnergyStorage;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.blocks.boiler.BoilerTE;
import mcjty.efab.blocks.rfcontrol.RfControlTE;
import mcjty.efab.blocks.steamengine.SteamEngineTE;
import mcjty.efab.blocks.tank.TankTE;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.items.ModItems;
import mcjty.efab.items.UpgradeItem;
import mcjty.efab.recipes.IEFabRecipe;
import mcjty.efab.recipes.RecipeManager;
import mcjty.efab.recipes.RecipeTier;
import mcjty.efab.sound.SoundController;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mcjty.efab.blocks.grid.GridContainer.COUNT_UPDATES;

public class GridTE extends GenericTileEntity implements DefaultSidedInventory, ITickable {

    public static final String CMD_CRAFT = "craft";
    public static final String CMD_LEFT = "left";
    public static final String CMD_RIGHT = "right";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, GridContainer.factory, 9 + 3 + COUNT_UPDATES + 1);
    private InventoryCrafting workInventory = new InventoryCrafting(new Container() {
        @SuppressWarnings("NullableProblems")
        @Override
        public boolean canInteractWith(EntityPlayer var1) {
            return false;
        }
    }, 3, 3);

    private int ticksRemaining = -1;
    private int totalTicks = 0;
    private ItemStack craftingOutput = ItemStackTools.getEmptyStack();

    // Client side only and contains the last error from the server
    private List<String> errorsFromServer = Collections.emptyList();
    private List<ItemStack> outputsFromServer = Collections.emptyList();

    // Transient information that is calculated on demand
    private boolean dirty = true;       // Our cached multiblock info is invalid
    private final Set<BlockPos> boilers = new HashSet<>();
    private final Set<BlockPos> steamEngines = new HashSet<>();
    private final Set<BlockPos> tanks = new HashSet<>();
    private final Set<BlockPos> gearBoxes = new HashSet<>();
    private final Set<BlockPos> rfControls = new HashSet<>();
    private final Set<BlockPos> rfStorages = new HashSet<>();
    private Set<RecipeTier> supportedTiers = null;

    @Override
    public void update() {
        if (!getWorld().isRemote) {
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
                    craftFinished(recipe);
                } else {
                    if (!craftInProgress(recipe)) {
                        abortCraft();
                    }
                }
            }
        } else {
            updateSound();
        }
    }

    private void abortCraft() {
        ticksRemaining = -1;
        craftingOutput = ItemStackTools.getEmptyStack();
        markDirtyClient();
    }

    // Return false if the craft should be aborted
    private boolean craftInProgress(@Nonnull IEFabRecipe recipe) {
        checkMultiBlockCache();

        if (recipe.getRequiredTiers().contains(RecipeTier.STEAM)) {
            // Consume a bit of water
            FluidStack stack = new FluidStack(FluidRegistry.WATER, GeneralConfiguration.waterSteamCraftingConsumption);
            TankTE tank = findSuitableTank(stack);
            if (tank == null) {
                return false;
            }
            FluidStack drained = tank.getHandler().drain(stack, true);
            if (drained == null || drained.amount < GeneralConfiguration.waterSteamCraftingConsumption) {
                return false;
            }
        }
        if (recipe.getRequiredRfPerTick() > 0) {
            int stillneeded = recipe.getRequiredRfPerTick();
            stillneeded = handlePowerPerTick(stillneeded, this.rfControls, GeneralConfiguration.rfControlMax);
            if (stillneeded > 0) {
                stillneeded = handlePowerPerTick(stillneeded, this.rfStorages, GeneralConfiguration.rfStorageInternalFlow);
                if (stillneeded > 0) {
                    if (GeneralConfiguration.abortCraftWhenOutOfRf) {
                        return false;
                    } else {
                        ticksRemaining--;   // One tick back
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private int handlePowerPerTick(int stillneeded, Set<BlockPos> poses, int maxUsage) {
        for (BlockPos p : poses) {
            TileEntity te = getWorld().getTileEntity(p);
            if (te instanceof IEFabEnergyStorage) {
                IEFabEnergyStorage energyStorage = (IEFabEnergyStorage) te;
                int canUse = Math.min(maxUsage, energyStorage.getEnergyStored(null));
                if (canUse >= stillneeded) {
                    energyStorage.extractEnergy(stillneeded);
                    stillneeded = 0;
                    break;
                } else {
                    energyStorage.extractEnergy(canUse);
                    stillneeded -= canUse;
                }
            }
        }
        return stillneeded;
    }

    private void craftFinished(@Nonnull IEFabRecipe recipe) {
        ticksRemaining = -1;
        markDirtyClient();
        // Craft finished. Consume items and do the actual crafting. If there is no room to place
        // the craft result then nothing happens

        if (!checkRoomForOutput(craftingOutput.copy())) {
            // Not enough room. Abort craft
            return;
        }

        if (checkFinalCraftRequirements(recipe)) {
            return;
        }

        insertOutput(craftingOutput.copy());

        // Consume items
        for (int i = GridContainer.SLOT_CRAFTINPUT; i < GridContainer.SLOT_CRAFTOUTPUT; i++) {
            decrStackSize(i, 1);
        }
    }

    // Returns true if the final craft requirements are not ok
    private boolean checkFinalCraftRequirements(IEFabRecipe recipe) {
        // Now check if we have secondary requirements like fluids
        // First loop to check
        for (FluidStack stack : recipe.getRequiredFluids()) {
            TankTE tank = findSuitableTank(stack);
            if (tank == null) {
                // Abort!
                return true;
            }
        }

        // Second loop to consume
        for (FluidStack stack : recipe.getRequiredFluids()) {
            TankTE tank = findSuitableTank(stack);
            tank.getHandler().drain(stack, true);
        }
        return false;
    }

    private ItemStack getCurrentGhostOutput() {
        return getStackInSlot(GridContainer.SLOT_GHOSTOUT);
    }

    private boolean checkRoomForOutput(ItemStack output) {
        for (int i = GridContainer.SLOT_CRAFTOUTPUT; i < GridContainer.SLOT_CRAFTOUTPUT + 3; i++) {
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
        for (int i = GridContainer.SLOT_CRAFTOUTPUT; i < GridContainer.SLOT_CRAFTOUTPUT + 3; i++) {
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

    /**
     * Set the ghost output slot to one of the possible outputs for the current
     * grid. If the output is already one of the possible outputs then nothing happens
     */
    private void setValidRecipeGhostOutput() {
        ItemStack current = inventoryHelper.getStackInSlot(GridContainer.SLOT_GHOSTOUT);
        List<IEFabRecipe> recipes = findCurrentRecipesSorted();
        if (ItemStackTools.isEmpty(current)) {
            if (!recipes.isEmpty()) {
                inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, recipes.get(0).cast().getRecipeOutput());
                totalTicks = recipes.get(0).getCraftTime();
                markDirtyQuick();
            }
        } else {
            if (recipes.isEmpty()) {
                inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, ItemStackTools.getEmptyStack());
                markDirtyQuick();
            } else {
                for (IEFabRecipe recipe : recipes) {
                    if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(current, recipe.cast().getRecipeOutput())) {
                        return; // Ok, already present
                    }
                }
                inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, recipes.get(0).cast().getRecipeOutput());
                totalTicks = recipes.get(0).getCraftTime();
                markDirtyQuick();
            }
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

    // Give all current recipes. Sort recipes that are possible first.
    @Nonnull
    private List<IEFabRecipe> findCurrentRecipesSorted() {
        List<IEFabRecipe> recipes = findCurrentRecipes();
        recipes.sort((r1, r2) -> {
            boolean error1 = getErrorsForOutput(r1.cast().getRecipeOutput(), null);
            boolean error2 = getErrorsForOutput(r2.cast().getRecipeOutput(), null);
            return error1 == error2 ? 0 : (error2 ? -1 : 1);
        });
        return recipes;
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

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        getInventoryHelper().setInventorySlotContents(getInventoryStackLimit(), index, stack);
        if (index >= GridContainer.SLOT_CRAFTINPUT && index < GridContainer.SLOT_CRAFTOUTPUT) {
            setValidRecipeGhostOutput();
            // We need to update the visual crafting grid client side
            markDirtyClient();
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getInventoryHelper().decrStackSize(index, count);
        if (index >= GridContainer.SLOT_CRAFTINPUT && index < GridContainer.SLOT_CRAFTOUTPUT) {
            setValidRecipeGhostOutput();
            // We need to update the visual crafting grid client side
            markDirtyClient();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = getInventoryHelper().removeStackFromSlot(index);
        if (index >= GridContainer.SLOT_CRAFTINPUT && index < GridContainer.SLOT_CRAFTOUTPUT) {
            setValidRecipeGhostOutput();
            // We need to update the visual crafting grid client side
            markDirtyClient();
        }
        return stack;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index >= GridContainer.SLOT_UPDATES && index < GridContainer.SLOT_UPDATES + GridContainer.COUNT_UPDATES) {
            if (ItemStackTools.isValid(stack) && stack.getItem() instanceof UpgradeItem) {
                return true;
            }
        }
        return index < GridContainer.SLOT_UPDATES;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index < GridContainer.SLOT_UPDATES;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index < GridContainer.SLOT_UPDATES;
    }

    private Random random = new Random();

    private void updateSound() {
        if (GeneralConfiguration.baseMachineVolume > 0.01f) {
            if (ticksRemaining >= 0) {
                IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());
                if (recipe != null) {
                    Set<RecipeTier> requiredTiers = recipe.getRequiredTiers();
                    if (requiredTiers.contains(RecipeTier.STEAM)) {
                        if (!SoundController.isSteamPlaying(getWorld(), pos)) {
                            SoundController.playSteamSound(getWorld(), pos, 1.0f);
                        }
                    } else if (requiredTiers.contains(RecipeTier.GEARBOX) || requiredTiers.contains(RecipeTier.ADVANCED_GEARBOX)) {
                        if (!SoundController.isMachinePlaying(getWorld(), pos)) {
                            SoundController.playMachineSound(getWorld(), pos, 1.0f);
                        }
                    } else if (requiredTiers.contains(RecipeTier.RF)) {
                        if (!SoundController.isSparksPlaying(getWorld(), pos)) {
                            if ((totalTicks - ticksRemaining < 1) || (random.nextFloat() < 0.04)) {
                                SoundController.playSparksSound(getWorld(), pos, 1.0f);
                                List<BlockPos> positions = new ArrayList<>();
                                findRFControlBlocks(pos, new HashSet<>(), positions);
                                if (!positions.isEmpty()) {
                                    BlockPos p = positions.get(random.nextInt(positions.size()));
                                    System.out.println("p = " + p);
                                    TileEntity te = getWorld().getTileEntity(p);
                                    if (te instanceof RfControlTE) {
                                        ((RfControlTE) te).setSpark(25);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
//                SoundController.stopSound(getWorld(), pos);
            }
        }
    }

    // Client-side. Find rf control blocks
    private void findRFControlBlocks(BlockPos current, Set<BlockPos> visited, List<BlockPos> positions) {
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);
        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos p = current.offset(dir);
            Block block = getWorld().getBlockState(p).getBlock();
            if (block == ModBlocks.gridBlock) {
                findRFControlBlocks(p, visited, positions);
            } else if (block instanceof GenericEFabMultiBlockPart) {
                if (block == ModBlocks.rfControlBlock) {
                    positions.add(p);
                }
                findRFControlBlocks(p, visited, positions);
            }
        }
    }

    private void findMultiBlockParts(BlockPos current, Set<BlockPos> visited) {
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);
        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos p = current.offset(dir);
            Block block = getWorld().getBlockState(p).getBlock();
            if (block == ModBlocks.gridBlock) {
                TileEntity te = getWorld().getTileEntity(p);
                if (te instanceof GridTE) {
                    ((GridTE) te).invalidateMultiBlockCache();
                }
                findMultiBlockParts(p, visited);
            } else if (block instanceof GenericEFabMultiBlockPart) {
                if (block == ModBlocks.boilerBlock) {
                    boilers.add(p);
                } else if (block == ModBlocks.steamEngineBlock) {
                    steamEngines.add(p);
                } else if (block == ModBlocks.gearBoxBlock) {
                    gearBoxes.add(p);
                } else if (block == ModBlocks.rfControlBlock) {
                    rfControls.add(p);
                } else if (block == ModBlocks.rfStorageBlock) {
                    rfStorages.add(p);
                } else if (block == ModBlocks.tankBlock) {
                    tanks.add(p);
                }
                findMultiBlockParts(p, visited);
            }
        }
    }


    private void checkMultiBlockCache() {
        if (dirty) {
            dirty = false;
            boilers.clear();
            steamEngines.clear();
            tanks.clear();
            gearBoxes.clear();
            rfControls.clear();
            rfStorages.clear();
            findMultiBlockParts(getPos(), new HashSet<>());
        }
    }

    public void invalidateMultiBlockCache() {
        dirty = true;
        supportedTiers = null;
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
            return findCurrentRecipesSorted()
                    .stream()
                    .map(r -> r.cast().getRecipeOutput())
                    .collect(Collectors.toList());
        }
    }

    private void left() {
        List<IEFabRecipe> sorted = findCurrentRecipesSorted();
        OptionalInt first = findCurrentGhost(sorted);
        if (first.isPresent()) {
            int i = (first.getAsInt() - 1 + sorted.size()) % sorted.size();
            IEFabRecipe recipe = sorted.get(i);
            setInventorySlotContents(GridContainer.SLOT_GHOSTOUT, recipe.cast().getRecipeOutput());
            totalTicks = recipe.getCraftTime();
            markDirtyQuick();
        }
    }

    private void right() {
        List<IEFabRecipe> sorted = findCurrentRecipesSorted();
        OptionalInt first = findCurrentGhost(sorted);
        if (first.isPresent()) {
            int i = (first.getAsInt() + 1) % sorted.size();
            IEFabRecipe recipe = sorted.get(i);
            setInventorySlotContents(GridContainer.SLOT_GHOSTOUT, recipe.cast().getRecipeOutput());
            totalTicks = recipe.getCraftTime();
            markDirtyQuick();
        }
    }

    private OptionalInt findCurrentGhost(List<IEFabRecipe> sorted) {
        ItemStack ghostOutput = getCurrentGhostOutput();
        return IntStream.range(0, sorted.size())
                .filter(i -> mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(sorted.get(i).cast().getRecipeOutput(), ghostOutput))
                .findFirst();
    }

    private void startCraft() {
        IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());
        if (recipe != null) {
            craftingOutput = getCurrentOutput(recipe);
            ticksRemaining = recipe.getCraftTime();
            totalTicks = recipe.getCraftTime();
            markDirtyClient();

            if (recipe.getRequiredTiers().contains(RecipeTier.STEAM)) {
                checkMultiBlockCache();
                for (BlockPos enginePos : steamEngines) {
                    TileEntity te = getWorld().getTileEntity(enginePos);
                    if (te instanceof SteamEngineTE) {
                        SteamEngineTE steamEngineTE = (SteamEngineTE) te;
                        steamEngineTE.setSpeedBoost(GeneralConfiguration.steamWheelBoost);
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
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
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("ticks", ticksRemaining);
        tagCompound.setInteger("total", totalTicks);
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

    public int getTotalTicks() {
        return totalTicks;
    }

    private TankTE findSuitableTank(@Nullable FluidStack stack) {
        if (stack == null) {
            return null;
        }
        checkMultiBlockCache();
        for (BlockPos tank : tanks) {
            TileEntity te = getWorld().getTileEntity(tank);
            if (te instanceof TankTE) {
                TankTE tankTE = (TankTE) te;
                FluidStack fluid = tankTE.getFluid();
                if (fluid != null && stack.getFluid() == fluid.getFluid()) {
                    if (fluid.amount >= stack.amount) {
                        return tankTE;
                    }
                }
            }
        }
        return null;
    }

    public List<String> getErrorState() {
        if (getWorld().isRemote) {
            return errorsFromServer;
        }

        ItemStack output = getCurrentGhostOutput();
        List<String> errors = new ArrayList<>();
        getErrorsForOutput(output, errors);
        return errors;
    }

    private boolean getErrorsForOutput(ItemStack output, @Nullable List<String> errors) {
        checkMultiBlockCache();

        IEFabRecipe recipe = findRecipeForOutput(output);
        if (recipe == null) {
            return false;
        }

        Set<RecipeTier> supported = getSupportedTiers();
        for (RecipeTier tier : recipe.getRequiredTiers()) {
            if (!supported.contains(tier)) {
                if (errors != null) {
                    errors.add(tier.getMissingError());
                } else {
                    return true;
                }
            }
        }

        for (FluidStack stack : recipe.getRequiredFluids()) {
            if (findSuitableTank(stack) == null) {
                if (errors != null) {
                    errors.add("Not enough liquid: " + stack.getLocalizedName());
                    errors.add("    " + stack.amount + " mb needed");
                } else {
                    return true;
                }
            }
        }

        if (recipe.getRequiredRfPerTick() > 0) {
            int totavailable = 0;
            int maxpertick = 0;
            for (BlockPos p : rfControls) {
                TileEntity te = getWorld().getTileEntity(p);
                if (te instanceof IEFabEnergyStorage) {
                    IEFabEnergyStorage energyStorage = (IEFabEnergyStorage) te;
                    totavailable += energyStorage.getEnergyStored(null);
                    maxpertick += energyStorage.getMaxInternalConsumption();
                }
            }
            for (BlockPos p : rfStorages) {
                TileEntity te = getWorld().getTileEntity(p);
                if (te instanceof IEFabEnergyStorage) {
                    IEFabEnergyStorage energyStorage = (IEFabEnergyStorage) te;
                    totavailable += energyStorage.getEnergyStored(null);
                    maxpertick += energyStorage.getMaxInternalConsumption();
                }
            }
            if (recipe.getRequiredRfPerTick() > maxpertick) {
                if (errors != null) {
                    errors.add("Not enough power capacity!");
                    errors.add("    " + recipe.getRequiredRfPerTick() + "RF/t needed but only " +
                        maxpertick + " possible");
                } else {
                    return true;
                }
            } else if (recipe.getRequiredRfPerTick() > totavailable) {
                if (errors != null) {
                    errors.add("Not enough power!");
                } else {
                    return true;
                }
            }
        }

        if (recipe.getRequiredTiers().contains(RecipeTier.STEAM)) {
            boolean ok = false;
            for (BlockPos boiler : boilers) {
                TileEntity te = getWorld().getTileEntity(boiler);
                if (te instanceof BoilerTE) {
                    if (((BoilerTE) te).canMakeSteam()) {
                        ok = true;
                        break;
                    }
                }
            }
            if (!ok) {
                if (errors != null) {
                    errors.add("There are no boilers hot enough!");
                } else {
                    return true;
                }
            }

            if (findSuitableTank(new FluidStack(FluidRegistry.WATER, GeneralConfiguration.waterSteamStartAmount)) == null) {
                if (errors != null) {
                    errors.add("Insufficient water to make steam!");
                } else {
                    return true;
                }
            }
        }

        return errors == null ? false : !errors.isEmpty();
    }

    private Set<RecipeTier> getSupportedTiers() {
        if (supportedTiers == null) {
            checkMultiBlockCache();
            supportedTiers = EnumSet.noneOf(RecipeTier.class);
            if (!boilers.isEmpty() && !steamEngines.isEmpty()) {
                supportedTiers.add(RecipeTier.STEAM);
            }
            if (!gearBoxes.isEmpty()) {
                supportedTiers.add(RecipeTier.GEARBOX);
            }
            if (!tanks.isEmpty()) {
                supportedTiers.add(RecipeTier.LIQUID);
            }
            if (!rfControls.isEmpty()) {
                supportedTiers.add(RecipeTier.RF);
            }
            for (int i = GridContainer.SLOT_UPDATES ; i < GridContainer.SLOT_UPDATES + GridContainer.COUNT_UPDATES ; i++) {
                ItemStack stack = getStackInSlot(i);
                if (ItemStackTools.isValid(stack)) {
                    if (stack.getItem() == ModItems.upgradeArmory) {
                        supportedTiers.add(RecipeTier.UPGRADE_ARMORY);
                    }
                }
            }
        }
        return supportedTiers;
    }

    // Called client-side only
    public void syncFromServer(int ticks, int total, List<String> errors, List<ItemStack> outputs) {
        ticksRemaining = ticks;
        totalTicks = total;
        errorsFromServer = errors;
        outputsFromServer = outputs;
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
        } else if (CMD_LEFT.equals(command)) {
            left();
            return true;
        } else if (CMD_RIGHT.equals(command)) {
            right();
            return true;
        }
        return false;
    }
}
