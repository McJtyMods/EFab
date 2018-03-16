package mcjty.efab.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IEFabRecipe {

    @Nonnull
    List<FluidStack> getRequiredFluids();

    int getRequiredRfPerTick();

    int getRequiredManaPerTick();

    int getCraftTime();

    @Nonnull
    Set<RecipeTier> getRequiredTiers();

    IEFabRecipe tier(RecipeTier tier);

    IEFabRecipe fluid(FluidStack stack);

    IEFabRecipe rfPerTick(int rf);

    IEFabRecipe manaPerTick(int mana);

    IEFabRecipe time(int t);

    // @todo not yet implemented
    void copyNBTFrom(String character);

    String getCopyNBTFrom();

    @Nonnull
    IRecipe cast();

    List<String> getInputs();

    Map<String, Object> getInputMap();

    List<List<ItemStack>> getInputLists();
}
