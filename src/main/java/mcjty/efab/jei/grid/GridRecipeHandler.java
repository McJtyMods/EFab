package mcjty.efab.jei.grid;

import mcjty.efab.recipes.IEFabRecipe;
import mcjty.efab.recipes.RecipeTier;
import mcjty.lib.jei.CompatRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Set;

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


    public static void drawInfo(IEFabRecipe recipe, ICraftingRecipeWrapper iCraftingRecipeWrapper, Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        Set<RecipeTier> tiers = recipe.getRequiredTiers();
        int y = 60;
        minecraft.fontRenderer.drawString("Tiers:", 0, y, Color.black.getRGB());
        int x = 35;
        for (RecipeTier tier : tiers) {
            String name = StringUtils.capitalize(tier.name()) + " ";
            minecraft.fontRenderer.drawString(name, x, y, Color.blue.getRGB());
            x += minecraft.fontRenderer.getStringWidth(name);
        }
        y += 12;

        if (!recipe.getRequiredFluids().isEmpty()) {
            minecraft.fontRenderer.drawString("Fluids:", 0, y, Color.black.getRGB());
            x = 35;
            for (FluidStack stack : recipe.getRequiredFluids()) {
                String name = stack.getLocalizedName() + " ";
                minecraft.fontRenderer.drawString(name, x, y, Color.blue.getRGB());
                x += minecraft.fontRenderer.getStringWidth(name);
            }

            y += 12;
        }

    }

}
