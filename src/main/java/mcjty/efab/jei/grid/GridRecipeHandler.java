package mcjty.efab.jei.grid;

import mcjty.efab.recipes.EFabShapedRecipe;
import mcjty.efab.recipes.IEFabRecipe;
import mcjty.lib.jei.CompatRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import java.awt.*;

public class GridRecipeHandler extends CompatRecipeHandler<IEFabRecipe> {

    public GridRecipeHandler() {
        super(GridRecipeCategory.ID);
    }


    @Nonnull
    @Override
    public Class getRecipeClass() {
        return IEFabRecipe.class;
    }

    @Nonnull
    public String getRecipeCategoryUid() {
        return VanillaRecipeCategoryUid.CRAFTING;
    }



    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull IEFabRecipe recipe) {
        if (recipe instanceof EFabShapedRecipe) {
            return new GridCraftingShapedRecipeWrapper(recipe, 3, 3);
        }

        return new GridCraftingRecipeWrapper(recipe);
    }


    public static void drawInfo(IEFabRecipe recipe, ICraftingRecipeWrapper iCraftingRecipeWrapper, Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String info = "Extra stuff";
        if (info != null) minecraft.fontRenderer.drawString(info, 60, 10, Color.black.getRGB());

    }

}
