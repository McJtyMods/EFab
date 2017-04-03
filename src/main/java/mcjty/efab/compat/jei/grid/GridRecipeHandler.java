package mcjty.efab.compat.jei.grid;

import mcjty.efab.recipes.IEFabRecipe;
import mcjty.lib.jei.CompatRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class GridRecipeHandler extends CompatRecipeHandler<IEFabRecipe> {

    public GridRecipeHandler() {
        super(GridRecipeCategory.ID);
    }

    @Nonnull
    @Override
    public Class getRecipeClass() {
        return IEFabRecipe.class;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull IEFabRecipe recipe) {
        return new GridCraftingRecipeWrapper(recipe);
    }


}
