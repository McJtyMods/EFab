package mcjty.efab.blocks.tank;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;

public class EFabFluidTank extends FluidTank {
    private final TankTE tank;

    public EFabFluidTank(int capacity, TankTE tank) {
        super(capacity);
        this.tank = tank;
    }

    @Nullable
    @Override
    public FluidStack drainInternal(FluidStack resource, boolean doDrain) {
        Fluid o = (fluid == null || fluid.amount <= 0) ? null : fluid.getFluid();
        FluidStack fluidStack = super.drainInternal(resource, doDrain);
        Fluid n = (fluid == null || fluid.amount <= 0) ? null : fluid.getFluid();
        if (o != n) {
            tank.markDirtyClient();

        }
        return fluidStack;
    }

    @Nullable
    @Override
    public FluidStack drainInternal(int maxDrain, boolean doDrain) {
        Fluid o = (fluid == null || fluid.amount <= 0) ? null : fluid.getFluid();
        FluidStack fluidStack = super.drainInternal(maxDrain, doDrain);
        Fluid n = (fluid == null || fluid.amount <= 0) ? null : fluid.getFluid();
        if (o != n) {
            tank.markDirtyClient();

        }
        return fluidStack;
    }

    @Override
    public int fillInternal(FluidStack resource, boolean doFill) {
        Fluid o = (fluid == null || fluid.amount <= 0) ? null : fluid.getFluid();
        int i = super.fillInternal(resource, doFill);
        Fluid n = (fluid == null || fluid.amount <= 0) ? null : fluid.getFluid();
        if (o != n) {
            tank.markDirtyClient();

        }
        return i;
    }
}

