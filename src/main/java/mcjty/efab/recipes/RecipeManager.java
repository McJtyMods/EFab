package mcjty.efab.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecipeManager {

    private static final List<EFabShapedRecipe> shapedRecipes = new ArrayList<>();
    private static final List<EFabShapelessRecipe> shapelessRecipes = new ArrayList<>();

    public static void init() {
    }

    public static void registerRecipe(EFabShapedRecipe recipe) {
        shapedRecipes.add(recipe);
    }

    public static void registerRecipe(EFabShapelessRecipe recipe) {
        shapelessRecipes.add(recipe);
    }

    @Nonnull
    public static List<IEFabRecipe> findValidRecipes(InventoryCrafting inventoryCrafting, World world) {
        List<IEFabRecipe> recipes = new ArrayList<>();
        for (EFabShapedRecipe recipe : shapedRecipes) {
            if (recipe.matches(inventoryCrafting, world)) {
                recipes.add(recipe);
            }
        }

        for (EFabShapelessRecipe recipe : shapelessRecipes) {
            if (recipe.matches(inventoryCrafting, world)) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

}
