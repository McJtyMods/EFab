package mcjty.efab.jei;

import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.blocks.grid.GridContainer;
import mcjty.efab.jei.grid.GridRecipeCategory;
import mcjty.efab.jei.grid.GridRecipeHandler;
import mcjty.efab.jei.grid.JEIRecipeAdapter;
import mcjty.efab.recipes.IEFabRecipe;
import mcjty.efab.recipes.RecipeManager;
import mcjty.lib.jei.JeiCompatTools;
import mezz.jei.api.*;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JEIPlugin
public class EFabJeiPlugin extends BlankModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();

        registry.addRecipeCategories(new GridRecipeCategory(guiHelper));
        registry.addRecipeHandlers(new GridRecipeHandler());

        Iterator<IEFabRecipe> recipeIterator = RecipeManager.getRecipes();
        List<IEFabRecipe> efabRecipes = new ArrayList<>();
        while (recipeIterator.hasNext()) {
            efabRecipes.add(new JEIRecipeAdapter(recipeIterator.next()));
        }
        JeiCompatTools.addRecipes(registry, efabRecipes);

        registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.gridBlock), GridRecipeCategory.ID);

        IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
        transferRegistry.addRecipeTransferHandler(GridContainer.class, GridRecipeCategory.ID, GridContainer.SLOT_CRAFTINPUT, 9, GridContainer.SLOT_GHOSTOUT+1, 36);
    }
}
