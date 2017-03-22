package mcjty.efab.recipes;

import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public interface IEFabRecipe {
    FluidStack getRequiredFluid();

    int getRequiredRF();

    int getCraftTime();

    Set<RecipeTier> getRequiredTiers();
}
