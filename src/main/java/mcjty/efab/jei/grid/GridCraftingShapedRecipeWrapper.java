package mcjty.efab.jei.grid;

import mcjty.efab.recipes.IEFabRecipe;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;

class GridCraftingShapedRecipeWrapper extends GridCraftingRecipeWrapper implements IShapedCraftingRecipeWrapper {
    final int width;
    final int height;

    public GridCraftingShapedRecipeWrapper(IEFabRecipe recipe, int width, int height) {
        super(recipe);

        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
