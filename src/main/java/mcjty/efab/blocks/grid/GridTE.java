package mcjty.efab.blocks.grid;

import mcjty.efab.EFab;
import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.blocks.IEFabEnergyStorage;
import mcjty.efab.blocks.ISpeedBooster;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.blocks.boiler.BoilerTE;
import mcjty.efab.blocks.crafter.CrafterTE;
import mcjty.efab.blocks.monitor.MonitorTE;
import mcjty.efab.blocks.rfcontrol.RfControlTE;
import mcjty.efab.blocks.storage.StorageTE;
import mcjty.efab.blocks.tank.TankTE;
import mcjty.efab.compat.botania.BotaniaSupportSetup;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.items.UpgradeItem;
import mcjty.efab.recipes.IEFabRecipe;
import mcjty.efab.recipes.RecipeTier;
import mcjty.efab.sound.SoundController;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;

import static mcjty.efab.blocks.grid.GridContainer.COUNT_UPDATES;

public class GridTE extends GenericTileEntity implements DefaultSidedInventory, ITickable {

    public static final String CMD_CRAFT = "craft";
    public static final String CMD_LEFT = "left";
    public static final String CMD_RIGHT = "right";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, GridContainer.factory, 9 + 3 + COUNT_UPDATES + 1);

    private int ticksRemaining = -1;
    private int totalTicks = 0;
    private int errorTicks = 0;            // Where there was an error this will be > 0

    private int crafterDelay = 0;

    // Client side only and contains the last error from the server
    private List<String> errorsFromServer = Collections.emptyList();

    // Transient information that is calculated on demand
    private boolean dirty = true;       // Our cached multiblock info is invalid
    private final Set<BlockPos> boilers = new HashSet<>();
    private final Set<BlockPos> steamEngines = new HashSet<>();
    private final Set<BlockPos> tanks = new HashSet<>();
    private final Set<BlockPos> gearBoxes = new HashSet<>();
    private final Set<BlockPos> rfControls = new HashSet<>();
    private final Set<BlockPos> rfStorages = new HashSet<>();
    private final Set<BlockPos> manaReceptacles = new HashSet<>();
    private final Set<BlockPos> processors = new HashSet<>();
    private final Set<BlockPos> monitors = new HashSet<>();
    private final Set<BlockPos> crafters = new HashSet<>();
    private final Set<BlockPos> storages = new HashSet<>();
    private Set<RecipeTier> supportedTiers = null;

    private final GridCrafterHelper crafterHelper = new GridCrafterHelper(this);

    private void updateMonitorStatus(String[] crafterStatus) {
        if (monitors.isEmpty()) {
            return;
        }
        String msg;
        if (errorTicks > 0) {
            msg = TextFormatting.DARK_RED + (((errorTicks / 20) % 2 == 0) ? "  ERROR" : "");
        } else if (totalTicks == 0) {
            msg = TextFormatting.DARK_GREEN + (ticksRemaining >= 0 ? ("  100%") : "  idle");
        } else {
            msg = TextFormatting.DARK_GREEN + (ticksRemaining >= 0 ? ("  " + ((totalTicks - ticksRemaining) * 100 / totalTicks + "%")) : "  idle");
        }
        for (BlockPos monitorPos : monitors) {
            TileEntity te = getWorld().getTileEntity(monitorPos);
            if (te instanceof MonitorTE) {
                ((MonitorTE) te).setCraftStatus(msg, crafterStatus[0], crafterStatus[1]);
            }
        }
    }

    public boolean checkIngredients(List<ItemStack> ingredients) {
        for (ItemStack ingredient : ingredients) {
            int needed = ingredient.getCount();
            for (BlockPos storagePos : storages) {
                TileEntity te = getWorld().getTileEntity(storagePos);
                if (te instanceof StorageTE) {
                    StorageTE storageTE = (StorageTE) te;
                    for (int ii = 0; ii < storageTE.getSizeInventory() ; ii++) {
                        ItemStack storageStack = storageTE.getStackInSlot(ii);
                        if (!storageStack.isEmpty() && OreDictionary.itemMatches(ingredient, storageStack, false)) {
                            needed -= Math.min(storageStack.getCount(), needed);
                            if (needed <= 0) {
                                break;
                            }
                        }
                    }
                    if (needed <= 0) {
                        break;
                    }
                }
            }
            if (needed > 0) {
                return false;
            }
        }
        return true;
    }

    public void consumeIngredients(List<ItemStack> ingredients) {
        for (ItemStack ingredient : ingredients) {
            int needed = ingredient.getCount();
            for (BlockPos storagePos : storages) {
                TileEntity te = getWorld().getTileEntity(storagePos);
                if (te instanceof StorageTE) {
                    StorageTE storageTE = (StorageTE) te;
                    for (int ii = 0; ii < storageTE.getSizeInventory() ; ii++) {
                        ItemStack storageStack = storageTE.getStackInSlot(ii);
                        if (!storageStack.isEmpty() && OreDictionary.itemMatches(ingredient, storageStack, false)) {
                            ItemStack extracted = storageTE.decrStackSize(ii, needed);
                            needed -= extracted.getCount();
                            if (needed <= 0) {
                                break;
                            }
                        }
                    }
                    if (needed <= 0) {
                        break;
                    }
                }
            }
        }
    }

    private String[] craftingStatus = new String[] { "", "" };

    private void updateCrafters() {
        checkMultiBlockCache();

        if (crafters.isEmpty()) {
            return;
        }

        if (!getSupportedTiers().contains(RecipeTier.COMPUTING)) {
            craftingStatus[0] = TextFormatting.DARK_RED + "  PROCESSOR";
            craftingStatus[1] = TextFormatting.DARK_RED + "  MISSING!";
            return;
        }

        markDirtyQuick();

        int countBusy = 0;
        int countOff = 0;
        int countMissing = 0;
        for (BlockPos crafterPos : new HashSet<>(crafters)) {
            TileEntity te = getWorld().getTileEntity(crafterPos);
            if (te instanceof CrafterTE) {
                CrafterTE crafterTE = (CrafterTE) te;
                if (crafterTE.isOn()) {
                    if (crafterTE.isCrafting()) {
                        crafterTE.setSpeedBoost(GeneralConfiguration.craftAnimationBoost);
                        crafterTE.handleCraft(this);
                        countBusy++;
                    } else {
                        IEFabRecipe recipe = crafterTE.checkCraft(this);
                        if (recipe == null) {
                            countMissing++;
                        } else {
                            crafterDelay--;
                            if (crafterDelay <= 0) {
                                crafterDelay = GeneralConfiguration.crafterDelay;
                                crafterTE.startCraft(recipe);
                                countBusy++;
                            }
                        }
                    }
                } else {
                    crafterTE.setLastError("No redstone signal");
                    countOff++;
                }
            }
        }

        int idx = 0;
        if (countMissing > 0) {
            craftingStatus[idx++] = TextFormatting.DARK_RED + "  " + "fail " + countMissing;
        }
        if (countBusy > 0) {
            craftingStatus[idx++] = TextFormatting.DARK_GREEN + "  " + "busy " + countBusy;
        }
        if (countOff > 0 && idx <= 1) {
            craftingStatus[idx++] = TextFormatting.DARK_GREEN + "  " + "off " + countOff;
        }
        if (idx <= 1) {
            craftingStatus[idx] = "";
        }
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (errorTicks > 0) {
                errorTicks++;
                markDirtyQuick();
            }
            updateCrafters();
            updateMonitorStatus(craftingStatus);

            if (ticksRemaining >= 0) {
                markDirtyQuick();

                IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());
                if (recipe == null) {
                    abortCraft();
                    errorTicks = 1;
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
                        errorTicks = 1;
                        return;
                    }
                }

                if (ticksRemaining < 0) {
                    craftFinished(recipe);
                } else {
                    CraftProgressResult result = craftInProgress(recipe);
                    if (result == CraftProgressResult.WAIT) {
                        ticksRemaining--;
                    } else if (result == CraftProgressResult.ABORT) {
                        abortCraft();
                        errorTicks = 1;
                    }
                }
            }
        } else {
            updateSound();
        }
    }

    private void abortCraft() {
        ticksRemaining = -1;
        crafterHelper.abortCraft();
        markDirtyClient();
    }

    public enum CraftProgressResult {
        ABORT,
        WAIT,
        OK
    }

    // Return false if the craft should be aborted
    public CraftProgressResult craftInProgress(@Nonnull IEFabRecipe recipe) {
        checkMultiBlockCache();

        if (recipe.getRequiredTiers().contains(RecipeTier.STEAM)) {
            // Consume a bit of water
            FluidStack stack = new FluidStack(FluidRegistry.WATER, GeneralConfiguration.waterSteamCraftingConsumption);
            TankTE tank = findSuitableTank(stack);
            if (tank == null) {
                return CraftProgressResult.ABORT;
            }
            FluidStack drained = tank.getHandler().drain(stack, true);
            if (drained == null || drained.amount < GeneralConfiguration.waterSteamCraftingConsumption) {
                return CraftProgressResult.ABORT;
            }
        }
        if (recipe.getRequiredRfPerTick() > 0) {
            int stillneeded = recipe.getRequiredRfPerTick();
            stillneeded = handlePowerPerTick(stillneeded, this.rfControls, GeneralConfiguration.rfControlMax);
            if (stillneeded > 0) {
                stillneeded = handlePowerPerTick(stillneeded, this.rfStorages, GeneralConfiguration.rfStorageInternalFlow);
                if (stillneeded > 0) {
                    if (GeneralConfiguration.abortCraftWhenOutOfRf) {
                        return CraftProgressResult.ABORT;
                    } else {
                        return CraftProgressResult.WAIT;
                    }
                }
            }
        }
        if (EFab.botania && recipe.getRequiredManaPerTick() > 0) {
            int stillneeded = recipe.getRequiredManaPerTick();
            stillneeded = handleManaPerTick(stillneeded, this.manaReceptacles, GeneralConfiguration.maxManaUsage);
            if (stillneeded > 0) {
                if (GeneralConfiguration.abortCraftWhenOutOfMana) {
                    return CraftProgressResult.ABORT;
                } else {
                    return CraftProgressResult.WAIT;
                }
            }
        }
        return CraftProgressResult.OK;
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

    private int handleManaPerTick(int stillneeded, Set<BlockPos> poses, int maxUsage) {
        for (BlockPos p : poses) {
            if (BotaniaSupportSetup.isManaReceptacle(getWorld().getBlockState(p).getBlock())) {
                int canUse = Math.min(maxUsage, BotaniaSupportSetup.getMana(getWorld(), p));
                if (canUse >= stillneeded) {
                    BotaniaSupportSetup.consumeMana(getWorld(), p, stillneeded);
                    stillneeded = 0;
                    break;
                } else {
                    BotaniaSupportSetup.consumeMana(getWorld(), p, canUse);
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

        if (!checkRoomForOutput(crafterHelper.getCraftingOutput().copy())) {
            // Not enough room. Abort craft
            return;
        }

        if (checkFinalCraftRequirements(recipe, Collections.emptyList())) {
            return;
        }

        insertOutput(crafterHelper.getCraftingOutput().copy());

        // Consume items
        for (int i = GridContainer.SLOT_CRAFTINPUT; i < GridContainer.SLOT_CRAFTOUTPUT; i++) {
            decrStackSize(i, 1);
        }
    }

    // Returns true if the final craft requirements are not ok
    public boolean checkFinalCraftRequirements(IEFabRecipe recipe, @Nonnull List<ItemStack> ingredients) {
        // Now check if we have secondary requirements like fluids
        // First loop to check
        for (FluidStack stack : recipe.getRequiredFluids()) {
            TankTE tank = findSuitableTank(stack);
            if (tank == null) {
                // Abort!
                return true;
            }
        }

        if (!checkIngredients(ingredients)) {
            // Ingredients are missing. Abort!
            return true;
        }

        // Second loop to consume
        for (FluidStack stack : recipe.getRequiredFluids()) {
            TankTE tank = findSuitableTank(stack);
            tank.getHandler().drain(stack, true);
        }

        consumeIngredients(ingredients);

        return false;
    }

    private ItemStack getCurrentGhostOutput() {
        return getStackInSlot(GridContainer.SLOT_GHOSTOUT);
    }

    private boolean checkRoomForOutput(ItemStack output) {
        return crafterHelper.checkRoomForOutput(output, GridContainer.SLOT_CRAFTOUTPUT, GridContainer.SLOT_CRAFTOUTPUT+3);
    }

    // This function assumes there is room (i.e. check with checkRoomForOutput first)
    private void insertOutput(ItemStack output) {
        crafterHelper.insertOutput(output, GridContainer.SLOT_CRAFTOUTPUT, GridContainer.SLOT_CRAFTOUTPUT+3);
    }

    /**
     * Set the ghost output slot to one of the possible outputs for the current
     * grid. If the output is already one of the possible outputs then nothing happens
     */
    private void setValidRecipeGhostOutput() {
        ItemStack current = inventoryHelper.getStackInSlot(GridContainer.SLOT_GHOSTOUT);
        List<IEFabRecipe> recipes = findCurrentRecipesSorted();
        if (current.isEmpty()) {
            if (!recipes.isEmpty()) {
                inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, recipes.get(0).cast().getRecipeOutput());
                totalTicks = getCraftTime(recipes.get(0));
                markDirtyQuick();
            }
        } else {
            if (recipes.isEmpty()) {
                inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, ItemStack.EMPTY);
                markDirtyQuick();
            } else {
                for (IEFabRecipe recipe : recipes) {
                    if (mcjty.efab.tools.InventoryHelper.isItemStackConsideredEqual(current, recipe.cast().getRecipeOutput())) {
                        return; // Ok, already present
                    }
                }
                inventoryHelper.setStackInSlot(GridContainer.SLOT_GHOSTOUT, recipes.get(0).cast().getRecipeOutput());
                totalTicks = getCraftTime(recipes.get(0));
                markDirtyQuick();
            }
        }
    }

    private ItemStack getCurrentOutput(@Nullable IEFabRecipe recipe) {
        if (recipe == null) {
            return ItemStack.EMPTY;
        } else {
            return recipe.cast().getCraftingResult(crafterHelper.getWorkInventory());
        }
    }

    // Give all current recipes. Sort recipes that are possible first.
    @Nonnull
    private List<IEFabRecipe> findCurrentRecipesSorted() {
        List<IEFabRecipe> recipes = crafterHelper.findCurrentRecipes(getWorld());
        recipes.sort((r1, r2) -> {
            boolean error1 = getErrorsForOutput(r1, null);
            boolean error2 = getErrorsForOutput(r2, null);
            return error1 == error2 ? 0 : (error2 ? -1 : 1);
        });
        return recipes;
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

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        getInventoryHelper().setInventorySlotContents(getInventoryStackLimit(), index, stack);
        if (index >= GridContainer.SLOT_CRAFTINPUT && index < GridContainer.SLOT_CRAFTOUTPUT) {
            setValidRecipeGhostOutput();
            // We need to update the visual crafting grid client side
            markDirtyClient();
        } else if (index >= GridContainer.SLOT_UPDATES && index < GridContainer.SLOT_UPDATES + GridContainer.COUNT_UPDATES) {
            invalidateMultiBlockCache();
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getInventoryHelper().decrStackSize(index, count);
        if (index >= GridContainer.SLOT_CRAFTINPUT && index < GridContainer.SLOT_CRAFTOUTPUT) {
            setValidRecipeGhostOutput();
            // We need to update the visual crafting grid client side
            markDirtyClient();
        } else if (index >= GridContainer.SLOT_UPDATES && index < GridContainer.SLOT_UPDATES + GridContainer.COUNT_UPDATES) {
            invalidateMultiBlockCache();
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
        } else if (index >= GridContainer.SLOT_UPDATES && index < GridContainer.SLOT_UPDATES + GridContainer.COUNT_UPDATES) {
            invalidateMultiBlockCache();
        }
        return stack;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index >= GridContainer.SLOT_UPDATES && index < GridContainer.SLOT_UPDATES + GridContainer.COUNT_UPDATES) {
            if (!stack.isEmpty() && stack.getItem() instanceof UpgradeItem) {
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
        if (ticksRemaining >= 0) {
            IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());
            if (recipe != null) {
                Set<RecipeTier> requiredTiers = recipe.getRequiredTiers();
                if (requiredTiers.contains(RecipeTier.STEAM)) {
                    if (!SoundController.isSteamPlaying(getWorld(), pos)) {
                        SoundController.playSteamSound(getWorld(), pos);
                        // @todo optimize this?
                        List<BlockPos> positions = new ArrayList<>();
                        findBoilers(pos, new HashSet<>(), positions);
                        if (!positions.isEmpty()) {
                            BlockPos p = positions.get(random.nextInt(positions.size()));
                            TileEntity te = getWorld().getTileEntity(p);
                            if (te instanceof BoilerTE) {
                                ((BoilerTE) te).setTimer(3 * 20);
                            }
                        }
                    }
                }
                if (requiredTiers.contains(RecipeTier.COMPUTING)) {
                        if (!SoundController.isBeepsPlaying(getWorld(), pos)) {
                            if ((totalTicks - ticksRemaining < 1) || (random.nextFloat() < 0.04)) {
                                if (random.nextInt(100) < 50) {
                                    SoundController.playBeeps1Sound(getWorld(), pos);
                                } else {
                                    SoundController.playBeeps2Sound(getWorld(), pos);
                                }
                            }
                        }
                }
                if (requiredTiers.contains(RecipeTier.GEARBOX) || requiredTiers.contains(RecipeTier.ADVANCED_GEARBOX)) {
                    if (!SoundController.isMachinePlaying(getWorld(), pos)) {
                        SoundController.playMachineSound(getWorld(), pos);
                    }
                }
                if (requiredTiers.contains(RecipeTier.RF)) {
                    if (!SoundController.isSparksPlaying(getWorld(), pos)) {
                        if ((totalTicks - ticksRemaining < 1) || (random.nextFloat() < 0.04)) {
                            SoundController.playSparksSound(getWorld(), pos);
                            // @todo optimize this?
                            List<BlockPos> positions = new ArrayList<>();
                            findRFControlBlocks(pos, new HashSet<>(), positions);
                            if (!positions.isEmpty()) {
                                BlockPos p = positions.get(random.nextInt(positions.size()));
                                TileEntity te = getWorld().getTileEntity(p);
                                if (te instanceof RfControlTE) {
                                    ((RfControlTE) te).setSpark(25);
                                }
                            }
                        }
                    }
                }
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
            } else if (block == ModBlocks.baseBlock) {
                findRFControlBlocks(p, visited, positions);
            } else if (block instanceof GenericEFabMultiBlockPart) {
                if (block == ModBlocks.rfControlBlock) {
                    positions.add(p);
                }
                findRFControlBlocks(p, visited, positions);
            }
        }
    }

    // Client-side. Find boilers
    private void findBoilers(BlockPos current, Set<BlockPos> visited, List<BlockPos> positions) {
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);
        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos p = current.offset(dir);
            Block block = getWorld().getBlockState(p).getBlock();
            if (block == ModBlocks.gridBlock) {
                findBoilers(p, visited, positions);
            } else if (block == ModBlocks.baseBlock) {
                findBoilers(p, visited, positions);
            } else if (block instanceof GenericEFabMultiBlockPart) {
                if (block == ModBlocks.boilerBlock) {
                    positions.add(p);
                }
                findBoilers(p, visited, positions);
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
            } else if (block == ModBlocks.baseBlock) {
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
                } else if (block == ModBlocks.processorBlock) {
                    processors.add(p);
                } else if (block == ModBlocks.monitorBlock) {
                    monitors.add(p);
                } else if (block == ModBlocks.crafterBlock) {
                    crafters.add(p);
                } else if (block == ModBlocks.storageBlock) {
                    storages.add(p);
                } else if (block == ModBlocks.tankBlock) {
                    tanks.add(p);
                } else if (EFab.botania && BotaniaSupportSetup.isManaReceptacle(block)) {
                    manaReceptacles.add(p);
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
            processors.clear();
            monitors.clear();
            crafters.clear();
            storages.clear();
            manaReceptacles.clear();
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
        return crafterHelper.getOutputs(getWorld());
    }

    private void left() {
        List<IEFabRecipe> sorted = findCurrentRecipesSorted();
        OptionalInt first = findCurrentGhost(sorted);
        if (first.isPresent()) {
            int i = (first.getAsInt() - 1 + sorted.size()) % sorted.size();
            IEFabRecipe recipe = sorted.get(i);
            setInventorySlotContents(GridContainer.SLOT_GHOSTOUT, recipe.cast().getRecipeOutput());
            totalTicks = getCraftTime(recipe);
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
            totalTicks = getCraftTime(recipe);
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
        errorTicks = 0;
        markDirtyQuick();
        IEFabRecipe recipe = findRecipeForOutput(getCurrentGhostOutput());

        if (recipe != null) {
            boolean error = getErrorsForOutput(recipe, null);
            if (error) {
                return; // Don't start
            }

            crafterHelper.setCraftingOutput(getCurrentOutput(recipe));
            int craftTime = getCraftTime(recipe);
            ticksRemaining = craftTime;
            totalTicks = craftTime;
            markDirtyClient();

            if (recipe.getRequiredTiers().contains(RecipeTier.STEAM)) {
                handleAnimationSpeed(GeneralConfiguration.steamWheelBoost, this.steamEngines);
            }
            if (EFab.botania && recipe.getRequiredTiers().contains(RecipeTier.MANA)) {
                handleAnimationSpeed(GeneralConfiguration.manaRotationBoost, manaReceptacles);

            }
        }
    }

    private int getCraftTime(IEFabRecipe recipe) {
        getSupportedTiers();
        int craftTime = recipe.getCraftTime();
        int bonus = 1;
        if (recipe.getRequiredTiers().contains(RecipeTier.GEARBOX)) {
            int cnt = gearBoxes.size();
            if (cnt > 1 && bonus < cnt) {
                bonus = Math.min(4, cnt);
            }
        }
        if (recipe.getRequiredTiers().contains(RecipeTier.STEAM)) {
            int cnt = steamEngines.size();
            if (cnt > 1 && bonus < cnt) {
                bonus = Math.min(4, cnt);
            }
        }
        if (recipe.getRequiredTiers().contains(RecipeTier.RF)) {
            int cnt = rfControls.size();
            if (cnt > 1 && bonus < cnt) {
                bonus = Math.min(4, cnt);
            }
        }
        if (recipe.getRequiredTiers().contains(RecipeTier.COMPUTING)) {
            int cnt = processors.size();
            if (cnt > 1 && bonus < cnt) {
                bonus = Math.min(4, cnt);
            }
        }
        return craftTime / bonus;
    }

    private void handleAnimationSpeed(int boost, Set<BlockPos> posSet) {
        checkMultiBlockCache();
        for (BlockPos enginePos : posSet) {
            TileEntity te = getWorld().getTileEntity(enginePos);
            if (te instanceof ISpeedBooster) {
                ISpeedBooster speedBooster = (ISpeedBooster) te;
                speedBooster.setSpeedBoost(boost);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        ticksRemaining = tagCompound.getInteger("ticks");
        errorTicks = tagCompound.getInteger("error");
        totalTicks = tagCompound.getInteger("total");
        crafterDelay = tagCompound.getInteger("crafterDelay");
        crafterHelper.readFromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("ticks", ticksRemaining);
        tagCompound.setInteger("error", errorTicks);
        tagCompound.setInteger("total", totalTicks);
        tagCompound.setInteger("crafterDelay", crafterDelay);
        crafterHelper.writeToNBT(tagCompound);
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
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
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
        IEFabRecipe recipe = findRecipeForOutput(output);
        List<String> errors = new ArrayList<>();
        getErrorsForOutput(recipe, errors);
        return errors;
    }

    public boolean getErrorsForOutput(IEFabRecipe recipe, @Nullable List<String> errors) {
        checkMultiBlockCache();

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

        if (EFab.botania && recipe.getRequiredManaPerTick() > 0) {
            int totavailable = 0;
            int maxpertick = 0;
            for (BlockPos p : manaReceptacles) {
                TileEntity te = getWorld().getTileEntity(p);
                totavailable += BotaniaSupportSetup.getMana(getWorld(), p);
                maxpertick += GeneralConfiguration.maxManaUsage;
            }
            if (recipe.getRequiredManaPerTick() > maxpertick) {
                if (errors != null) {
                    errors.add("Not enough mana capacity!");
                    errors.add("    " + recipe.getRequiredManaPerTick() + "mana/t needed but only " +
                            maxpertick + " possible");
                } else {
                    return true;
                }
            } else if (recipe.getRequiredManaPerTick() > totavailable) {
                if (errors != null) {
                    errors.add("Not enough mana!");
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

        return errors != null && !errors.isEmpty();
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
            if (!manaReceptacles.isEmpty()) {
                supportedTiers.add(RecipeTier.MANA);
            }
            if (!processors.isEmpty()) {
                supportedTiers.add(RecipeTier.COMPUTING);
            }
            for (int i = GridContainer.SLOT_UPDATES ; i < GridContainer.SLOT_UPDATES + GridContainer.COUNT_UPDATES ; i++) {
                ItemStack stack = getStackInSlot(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof UpgradeItem) {
                        supportedTiers.add(((UpgradeItem) stack.getItem()).providesTier());
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
        crafterHelper.syncFromServer(outputs);
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
