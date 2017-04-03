package mcjty.efab.compat.jei.grid;

import mcjty.efab.recipes.IEFabRecipe;
import mcjty.efab.recipes.RecipeTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JEIRecipeAdapter implements IEFabRecipe {

    private final IEFabRecipe parent;

    public JEIRecipeAdapter(IEFabRecipe parent) {
        this.parent = parent;
    }

    @Nonnull
    @Override
    public Collection<FluidStack> getRequiredFluids() {
        return parent.getRequiredFluids();
    }

    @Override
    public int getRequiredRfPerTick() {
        return parent.getRequiredRfPerTick();
    }

    @Override
    public int getCraftTime() {
        return parent.getCraftTime();
    }

    @Nonnull
    @Override
    public Set<RecipeTier> getRequiredTiers() {
        return parent.getRequiredTiers();
    }

    @Override
    public IEFabRecipe tier(RecipeTier tier) {
        return parent.tier(tier);
    }

    @Override
    public IEFabRecipe fluid(FluidStack stack) {
        return parent.fluid(stack);
    }

    @Override
    public IEFabRecipe rfPerTick(int rf) {
        return parent.rfPerTick(rf);
    }

    @Override
    public IEFabRecipe time(int t) {
        return parent.time(t);
    }

    @Nonnull
    @Override
    public IRecipe cast() {
        return parent.cast();
    }

    @Override
    public List<String> getInputs() {
        return parent.getInputs();
    }

    @Override
    public Map<String, Object> getInputMap() {
        return parent.getInputMap();
    }

    @Override
    public List<List<ItemStack>> getInputLists() {
        return parent.getInputLists();
    }
}
