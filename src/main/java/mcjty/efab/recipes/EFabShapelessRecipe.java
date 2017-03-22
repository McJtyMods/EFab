package mcjty.efab.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fluids.FluidStack;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class EFabShapelessRecipe extends ShapelessRecipes implements IEFabRecipe {

    @Nonnull private final Set<RecipeTier> requiredTiers = EnumSet.noneOf(RecipeTier.class);
    private FluidStack requiredFluid = null;
    private int requiredRF = 0;
    private int craftTime = 0;

    public EFabShapelessRecipe(ItemStack[] ingredientsIn, ItemStack output) {
        super(output, Arrays.asList(ingredientsIn));
    }

    public EFabShapelessRecipe tier(RecipeTier tier) {
        requiredTiers.add(tier);
        return this;
    }

    public EFabShapelessRecipe fluid(FluidStack stack) {
        requiredFluid = stack;
        return this;
    }

    public EFabShapelessRecipe energy(int rf) {
        requiredRF = rf;
        return this;
    }

    public EFabShapelessRecipe time(int t) {
        craftTime = t;
        return this;
    }

    @Nullable
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

    @Nonnull
    @Override
    public Set<RecipeTier> getRequiredTiers() {
        return requiredTiers;
    }

    @Nonnull
    @Override
    public IRecipe cast() {
        return this;
    }
}
