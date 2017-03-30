package mcjty.efab.jei.grid;

import mcjty.efab.recipes.IEFabRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;
import java.util.List;

class GridCraftingRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper {

    protected final IEFabRecipe recipe;
    protected final List<List<ItemStack>> inputs;
    protected final ItemStack output;


    public GridCraftingRecipeWrapper(IEFabRecipe recipe) {
        this.recipe = recipe;

        this.inputs = recipe.getInputLists();
        this.output = recipe.cast().getRecipeOutput();
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setOutput(ItemStack.class, output);
        ingredients.setInputLists(ItemStack.class, inputs);
    }


    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        GridRecipeHandler.drawInfo(recipe, this, minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
    }
}
