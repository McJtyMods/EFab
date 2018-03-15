package mcjty.efab.recipes;

import mcjty.efab.config.GeneralConfiguration;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VanillaRecipeAdapter implements IEFabRecipe {

    private final IRecipe vanillaRecipe;

    public VanillaRecipeAdapter(IRecipe vanillaRecipe) {
        this.vanillaRecipe = vanillaRecipe;
    }

    @Nonnull
    @Override
    public List<FluidStack> getRequiredFluids() {
        return Collections.emptyList();
    }

    @Override
    public int getRequiredRfPerTick() {
        return 0;
    }

    @Override
    public int getRequiredManaPerTick() {
        return 0;
    }

    @Override
    public int getCraftTime() {
        return GeneralConfiguration.vanillaCraftTime;
    }

    @Nonnull
    @Override
    public Set<RecipeTier> getRequiredTiers() {
        return Collections.emptySet();
    }

    @Override
    public IEFabRecipe tier(RecipeTier tier) {
        return this;
    }

    @Override
    public IEFabRecipe fluid(FluidStack stack) {
        return this;
    }

    @Override
    public IEFabRecipe rfPerTick(int rf) {
        return this;
    }

    @Override
    public IEFabRecipe manaPerTick(int mana) {
        return this;
    }

    @Override
    public IEFabRecipe time(int t) {
        return this;
    }

    @Nonnull
    @Override
    public IRecipe cast() {
        return vanillaRecipe;
    }

    @Override
    public void copyNBTFrom(String character) {
    }

    @Override
    public String getCopyNBTFrom() {
        return null;
    }

    @Override
    public List<String> getInputs() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getInputMap() {
        return Collections.emptyMap();
    }

    @Override
    public List<List<ItemStack>> getInputLists() {
        return Collections.emptyList();
    }
}
