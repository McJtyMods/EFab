package mcjty.efab.jei.grid;

import mcjty.lib.jei.CompatRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class GridRecipeHandler extends CompatRecipeHandler<GridRecipeWrapper> {

    public GridRecipeHandler() {
        super(GridRecipeCategory.ID);
    }

    @Nonnull
    @Override
    public Class<GridRecipeWrapper> getRecipeClass() {
        return GridRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull GridRecipeWrapper recipe) {
        return recipe;
    }
}
