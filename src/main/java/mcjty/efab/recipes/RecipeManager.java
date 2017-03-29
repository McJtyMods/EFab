package mcjty.efab.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeManager {

    private static final List<IEFabRecipe> recipes = new ArrayList<>();

    public static void clear() {
        recipes.clear();
    }

    public static void init() {
    }

    public static Iterator<IEFabRecipe> getRecipes() {
        return recipes.iterator();
    }

    public static void registerRecipe(IEFabRecipe recipe) {
        recipes.add(recipe);
    }

    @Nonnull
    public static List<IEFabRecipe> findValidRecipes(InventoryCrafting inventoryCrafting, World world) {
        List<IEFabRecipe> foundRecipes = new ArrayList<>();
        for (IEFabRecipe recipe : recipes) {
            if (recipe.cast().matches(inventoryCrafting, world)) {
                foundRecipes.add(recipe);
            }
        }
        return foundRecipes;
    }

}
