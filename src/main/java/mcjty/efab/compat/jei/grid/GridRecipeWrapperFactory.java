package mcjty.efab.compat.jei.grid;

import mcjty.efab.recipes.IEFabRecipe;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class GridRecipeWrapperFactory implements IRecipeWrapperFactory<IEFabRecipe> {
    @Override
    public IRecipeWrapper getRecipeWrapper(IEFabRecipe recipe) {
        return new GridCraftingRecipeWrapper(recipe);
    }
}
