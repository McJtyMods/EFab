package mcjty.efab.jei;

import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.jei.grid.GridRecipeCategory;
import mcjty.efab.jei.grid.GridRecipeHandler;
import mcjty.lib.jei.JeiCompatTools;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class EFabJeiPlugin extends BlankModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();

        registry.addRecipeCategories(
                new GridRecipeCategory(guiHelper));
        registry.addRecipeHandlers(
                new GridRecipeHandler());

        List<IRecipeWrapper> recipes = new ArrayList<>();
//        for (String registryName : LaserTileEntity.infusingBonusMap.keySet()) {
//            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
//            recipes.add(new LaserRecipeWrapper(item));
//        }
        JeiCompatTools.addRecipes(registry, recipes);

        registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.gridBlock), GridRecipeCategory.ID);
    }
}
