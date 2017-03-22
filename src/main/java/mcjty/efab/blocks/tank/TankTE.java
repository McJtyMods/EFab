package mcjty.efab.blocks.tank;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TankTE extends GenericEFabTile {

    public static final int MAXCAPACITY = 10000;

    private FluidTank handler = new FluidTank(MAXCAPACITY);

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    public FluidStack getFluid() {
        return getHandler().getFluid();
    }

    public FluidTank getHandler() {
        if (handler == null) {
            TankTE tankTE = (TankTE) getWorld().getTileEntity(pos.down());
            if (tankTE == null) {
                // Cannot happen!
                return null;
            }
            return tankTE.getHandler();
        }
        return handler;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // First find the bottom tank
        if (!world.isRemote) {
            BlockPos bottomPos = pos;
            while (world.getBlockState(bottomPos.down()).getBlock() == ModBlocks.tankBlock) {
                bottomPos = bottomPos.down();
            }
            if (world.getBlockState(bottomPos.up()).getBlock() == ModBlocks.tankBlock) {
                // We have a multiblock. Need to combine handlers.
                BlockPos p = bottomPos;
                int cnt = 0;
                while (world.getBlockState(p).getBlock() == ModBlocks.tankBlock) {
                    cnt++;
                    p = p.up();
                }
                FluidTank bottomHandler = new FluidTank(MAXCAPACITY * cnt);
                p = bottomPos;
                while (world.getBlockState(p).getBlock() == ModBlocks.tankBlock) {
                    TankTE te = (TankTE) world.getTileEntity(bottomPos);
                    te.markDirtyQuick();
                    if (te.handler != null) {
                        bottomHandler.fill(te.getFluid(), true);
                    }
                    te.handler = null;
                    p = p.up();
                }
                TankTE bottomTE = (TankTE) world.getTileEntity(bottomPos);
                bottomTE.handler = bottomHandler;
            }
        }
    }

    @Override
    public void onBlockBreak(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            BlockPos bottomPos = pos;
            while (world.getBlockState(bottomPos.down()).getBlock() == ModBlocks.tankBlock) {
                bottomPos = bottomPos.down();
            }
            if (world.getBlockState(bottomPos.up()).getBlock() == ModBlocks.tankBlock) {
                TankTE bottomTE = (TankTE) world.getTileEntity(bottomPos);
                // Empty the complete tank first
                FluidStack drained = bottomTE.getHandler().drain(bottomTE.getHandler().getCapacity(), true);

                // We have a multiblock. Need to split handlers.
                int cntBelow = pos.getY() - bottomPos.getY();       // Number of tank blocks below this one
                if (cntBelow > 0) {
                    bottomTE.handler = new FluidTank(cntBelow * MAXCAPACITY);
                    int accepted = bottomTE.handler.fill(drained, true);
                    drained.amount -= accepted;
                    bottomTE.markDirtyQuick();
                }

                handler = new FluidTank(MAXCAPACITY);
                if (drained.amount > 0) {
                    int accepted = handler.fill(drained, true);
                    drained.amount -= accepted;
                    markDirtyQuick();
                }

                TankTE topTE = (TankTE) world.getTileEntity(pos.up());
                if (topTE != null) {
                    BlockPos p = pos.up();
                    int cnt = 0;
                    while (world.getBlockState(p).getBlock() == ModBlocks.tankBlock) {
                        cnt++;
                        p = p.up();
                    }
                    topTE.handler = new FluidTank(cnt * MAXCAPACITY);
                    if (drained.amount > 0) {
                        topTE.handler.fill(drained, true);
                    }
                    topTE.markDirtyQuick();
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        if (tagCompound.hasKey("fluid")) {
            handler.readFromNBT(tagCompound.getCompoundTag("fluid"));
        }
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        if (handler != null) {
            NBTTagCompound fluidTc = new NBTTagCompound();
            handler.writeToNBT(fluidTc);
            tagCompound.setTag("fluid", fluidTc);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getHandler());
        }
        return super.getCapability(capability, facing);
    }

}
