package mcjty.efab.blocks.tank;

import mcjty.efab.blocks.GenericEFabTile;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.config.GeneralConfiguration;
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

    private EFabFluidTank handler;
    private int capacity = GeneralConfiguration.tankCapacity;

    private String clientFluidName = "";

    public FluidStack getFluid() {
        return getHandler().getFluid();
    }


    public String getClientFluidName() {
        return clientFluidName;
    }

    public FluidTank getHandler() {
        if (handler == null) {
            TileEntity te = getWorld().getTileEntity(pos.down());
            if (te instanceof TankTE) {
                TankTE tankTE = (TankTE) te;
                return tankTE.getHandler();
            } else {
                handler = new EFabFluidTank(capacity, this);
            }
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
                EFabFluidTank bottomHandler = new EFabFluidTank(GeneralConfiguration.tankCapacity * cnt, this);
                p = bottomPos;
                while (world.getBlockState(p).getBlock() == ModBlocks.tankBlock) {
                    TankTE te = (TankTE) world.getTileEntity(p);
                    te.markDirtyQuick();
                    if (te.handler != null) {
                        bottomHandler.fill(te.handler.getFluid(), true);
                    }
                    te.handler = null;
                    te.capacity = 0;
                    p = p.up();
                }
                TankTE bottomTE = (TankTE) world.getTileEntity(bottomPos);
                bottomTE.handler = bottomHandler;
                bottomTE.capacity = GeneralConfiguration.tankCapacity * cnt;
            }
            markDirtyClient();
        }
    }

    @Override
    public void onBlockBreak(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            BlockPos bottomPos = pos;
            while (world.getBlockState(bottomPos.down()).getBlock() == ModBlocks.tankBlock) {
                bottomPos = bottomPos.down();
            }
            if (world.getBlockState(bottomPos.up()).getBlock() == ModBlocks.tankBlock || bottomPos.up().equals(pos)) {
                TankTE bottomTE = (TankTE) world.getTileEntity(bottomPos);
                // Empty the complete tank first
                FluidStack drained = bottomTE.getHandler().drain(bottomTE.getHandler().getCapacity(), true);

                // We have a multiblock. Need to split handlers.
                int cntBelow = pos.getY() - bottomPos.getY();       // Number of tank blocks below this one
                if (cntBelow > 0) {
                    bottomTE.handler = new EFabFluidTank(cntBelow * GeneralConfiguration.tankCapacity, this);
                    bottomTE.capacity = cntBelow * GeneralConfiguration.tankCapacity;
                    if (drained != null) {
                        int accepted = bottomTE.handler.fill(drained, true);
                        drained.amount -= accepted;
                    }
                    bottomTE.markDirtyQuick();
                }

                handler = new EFabFluidTank(GeneralConfiguration.tankCapacity, this);
                capacity = GeneralConfiguration.tankCapacity;
                if (drained != null && drained.amount > 0) {
                    int accepted = handler.fill(drained, true);
                    drained.amount -= accepted;
                    markDirtyQuick();
                }

                TileEntity te = world.getTileEntity(pos.up());
                if (te instanceof TankTE) {
                    TankTE topTE = (TankTE) te;
                    BlockPos p = pos.up();
                    int cnt = 0;
                    while (world.getBlockState(p).getBlock() == ModBlocks.tankBlock) {
                        cnt++;
                        p = p.up();
                    }
                    topTE.handler = new EFabFluidTank(cnt * GeneralConfiguration.tankCapacity, this);
                    topTE.capacity = cnt * GeneralConfiguration.tankCapacity;
                    if (drained != null && drained.amount > 0) {
                        topTE.handler.fill(drained, true);
                    }
                    topTE.markDirtyQuick();
                }
            }
            markDirtyClient();
        }
    }

    @Override
    public void writeClientDataToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        if (getFluid() == null) {
            tagCompound.setString("fluidName", "");
        } else {
            tagCompound.setString("fluidName", getFluid().getLocalizedName());
        }
    }

    @Override
    public void readClientDataFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        String fluidName = tagCompound.getString("fluidName");
        setClientFluidName(fluidName);
    }

    private void setClientFluidName(String fluidName) {
        clientFluidName = fluidName;
        BlockPos p = pos;
        if (p == null || world == null) {
            return;
        }
        while (true) {
            if (world.getBlockState(p.down()) == ModBlocks.tankBlock) {
                p = p.down();
            } else {
                break;
            }
        }
        while (true) {
            TileEntity te = world.getTileEntity(p);
            if (te instanceof TankTE) {
                ((TankTE) te).clientFluidName = fluidName;
                p = p.up();
            } else {
                break;
            }
        }
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
        capacity = tagCompound.getInteger("capacity");
        if (capacity > 0) {
            handler = new EFabFluidTank(capacity, this);
        } else {
            handler = null;
        }
        if (tagCompound.hasKey("fluid")) {
            handler.readFromNBT(tagCompound.getCompoundTag("fluid"));
            if (getFluid() != null) {
                setClientFluidName(getFluid().getLocalizedName());
            }
        }
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("capacity", capacity);
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
