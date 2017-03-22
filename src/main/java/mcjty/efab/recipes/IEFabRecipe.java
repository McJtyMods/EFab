package mcjty.efab.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface IEFabRecipe {

    @Nullable
    FluidStack getRequiredFluid();

    int getRequiredRF();

    int getCraftTime();

    @Nonnull
    Set<RecipeTier> getRequiredTiers();

    @Nonnull
    IRecipe cast();
}
