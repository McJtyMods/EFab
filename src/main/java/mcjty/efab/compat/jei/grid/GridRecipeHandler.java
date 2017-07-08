package mcjty.efab.compat.jei.grid;

import mcjty.efab.recipes.IEFabRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class GridRecipeHandler implements IRecipeHandler<IEFabRecipe> {

    private final String id;

    public GridRecipeHandler() {
        this.id = GridRecipeCategory.ID;
    }

    @Override
    public String getRecipeCategoryUid(IEFabRecipe recipe) {
        return id;
    }

    @Override
    public boolean isRecipeValid(IEFabRecipe recipe) {
        return true;
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
