package mcjty.efab.blocks.tank;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.tools.FluidTools;
import mcjty.efab.tools.InventoryHelper;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TankBlock extends GenericEFabMultiBlockPart<TankTE, EmptyContainer> {

    public static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public static final PropertyEnum<EnumTankState> STATE = PropertyEnum.create("state", EnumTankState.class, EnumTankState.values());

    public final int capacity;

    public TankBlock(String name, int capacity, Class<? extends TankTE> clazz) {
        super(Material.IRON, clazz, EmptyContainer::new, TankItemBlock.class, name, false);
        this.capacity = capacity;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This tank can store " + TextFormatting.GREEN + capacity + TextFormatting.WHITE + " mb");
        tooltip.add(TextFormatting.WHITE + "Combine tanks by placing them");
        tooltip.add(TextFormatting.WHITE + "on top of each other");
        tooltip.add(TextFormatting.WHITE + "Used for " + TextFormatting.GREEN + "liquid" + TextFormatting.WHITE + " and "
            + TextFormatting.GREEN + "steam" + TextFormatting.WHITE + " crafting");
        if (stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("fluid"));
            if (fluid != null) {
                tooltip.add(TextFormatting.GRAY + "Fluid: " + TextFormatting.BLUE + fluid.amount + "mb (" + fluid.getLocalizedName() + ")");
            }
        }

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(TankTE.class, new TankRenderer());
    }


    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return EMPTY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TankTE) {
                TankTE tankTE = (TankTE) te;
                if (!heldItem.isEmpty()) {
                    if (FluidTools.isEmptyContainer(heldItem)) {
                        extractIntoContainer(player, tankTE);
                        return true;
                    } else if (FluidTools.isFilledContainer(heldItem)) {
                        fillFromContainer(player, world, tankTE);
                        return true;
                    }
                }
                FluidStack fluid = tankTE.getFluid();
                if (fluid == null || fluid.amount <= 0) {
                    ITextComponent component = new TextComponentString("Tank is empty");
                    player.sendStatusMessage(component, false);
                } else {
                    ITextComponent component = new TextComponentString("Tank contains " + fluid.amount + "mb of " + fluid.getLocalizedName());
                    player.sendStatusMessage(component, false);
                }
            }
        }
        return true;
    }

    private void fillFromContainer(EntityPlayer player, World world, TankTE tank) {
        ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        ItemStack container = heldItem.copy().splitStack(1);
        FluidStack fluidStack = FluidTools.convertBucketToFluid(container);
        if (fluidStack != null) {
            int filled = tank.getHandler().fill(fluidStack, false);
            if (filled == fluidStack.amount) {
                // Success
                tank.getHandler().fill(fluidStack, true);
                tank.getBottomTank().markDirtyClient();
                if (!player.capabilities.isCreativeMode) {
                    heldItem.splitStack(1);
                    ItemStack emptyContainer = FluidTools.drainContainer(container);
                    mcjty.efab.tools.InventoryHelper.giveItemToPlayer(player, emptyContainer);
                }
            }
        }
    }

    private void extractIntoContainer(EntityPlayer player, TankTE tank) {
        FluidStack drained = tank.getHandler().drain(1, false);
        if (drained != null) {
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            if (heldItem.isEmpty()) {
                return;
            }
            ItemStack container = heldItem.copy().splitStack(1);
            int capacity = FluidTools.getCapacity(drained, container);
            if (capacity != 0) {
                drained = tank.getHandler().drain(capacity, false);
                if (drained != null && drained.amount == capacity) {
                    tank.getHandler().drain(capacity, true);
                    tank.getBottomTank().markDirtyClient();
                    ItemStack filledContainer = FluidTools.fillContainer(drained, container);
                    if (!filledContainer.isEmpty()) {
                        heldItem.splitStack(1);
                        InventoryHelper.giveItemToPlayer(player, filledContainer);
                    }
                }
            }
            player.openContainer.detectAndSendChanges();
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, blockIn, fromPos);
        // @todo: on 1.11 we could have used the position from which the update is coming
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState metadata, int fortune) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TankTE) {
            TankTE bottomTank = ((TankTE) tileEntity).getBottomTank();

            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
            NBTTagCompound tagCompound = new NBTTagCompound();
            bottomTank.writeRestorableToNBT(tagCompound);

            // Correct the amount in the fluid so it doesn't go beyond cap based on where the tank is located
            if (tagCompound.hasKey("fluid")) {
                tagCompound.setInteger("capacity", capacity);
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("fluid"));
                if (fluid != null) {
                    int index = ((TankTE) tileEntity).getTankIndex();
                    int totalamount = bottomTank.getFluid().amount;
                    while (index > 0) {
                        totalamount -= capacity;
                        index--;
                    }
                    if (totalamount < 0) {
                        totalamount = 0;
                    } else if (totalamount > capacity) {
                        totalamount = capacity;
                    }

                    if (fluid.amount > totalamount) {
                        fluid.amount = totalamount;
                    }
                    if (fluid.amount > 0) {
                        tagCompound.setTag("fluid", new NBTTagCompound());
                        fluid.writeToNBT(tagCompound.getCompoundTag("fluid"));
                    } else {
                        tagCompound.removeTag("fluid");
                    }
                }
            }

            stack.setTagCompound(tagCompound);
            List<ItemStack> result = new ArrayList<>();
            result.add(stack);
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
//        boolean half = isNeedToBeHalf(worldIn.getBlockState(pos.down()).getBlock());
        boolean tankUp = worldIn.getBlockState(pos.up()).getBlock() == this;
        boolean tankDown = worldIn.getBlockState(pos.down()).getBlock() == this;
        EnumTankState s;
        if (tankUp && tankDown) {
            s = EnumTankState.MIDDLE;
        } else if (tankUp) {
            s = EnumTankState.BOTTOM;
        } else if (tankDown) {
            s = EnumTankState.TOP;
        } else {
            s = EnumTankState.FULL;
        }
        return state.withProperty(STATE, s);
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ, STATE);
    }
}
