package mcjty.efab.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class EFabShapedRecipe extends ShapedRecipes implements IEFabRecipe {

    private final Set<RecipeTier> requiredTiers = EnumSet.noneOf(RecipeTier.class);
    private FluidStack requiredFluid = null;
    private int requiredRF = 0;
    private int craftTime = 0;

    public EFabShapedRecipe(ItemStack[] ingredientsIn, ItemStack output) {
        super(3, 3, ingredientsIn, output);
    }

    public EFabShapedRecipe tier(RecipeTier tier) {
        requiredTiers.add(tier);
        return this;
    }

    public EFabShapedRecipe fluid(FluidStack stack) {
        requiredFluid = stack;
        return this;
    }

    public EFabShapedRecipe energy(int rf) {
        requiredRF = rf;
        return this;
    }

    public EFabShapedRecipe time(int t) {
        craftTime = t;
        return this;
    }

    @Override
    public FluidStack getRequiredFluid() {
        return requiredFluid;
    }

    @Override
    public int getRequiredRF() {
        return requiredRF;
    }

    @Override
    public int getCraftTime() {
        return craftTime;
    }

    @Override
    public Set<RecipeTier> getRequiredTiers() {
        return requiredTiers;
    }
}
