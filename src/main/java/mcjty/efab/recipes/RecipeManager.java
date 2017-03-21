package mcjty.efab.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecipeManager {

//    static {
//        RecipeSorter.register("efab:efabshaped", EFabShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
//        RecipeSorter.register("efab:efabshapeless", EFabShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
//    }

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

    public static IRecipe findValidRecipe(InventoryCrafting inventoryCrafting, World world, Set<RecipeTier> availableTiers) {
        for (EFabShapedRecipe recipe : shapedRecipes) {
            if (availableTiers.containsAll(recipe.getRequiredTiers())) {
                if (recipe.matches(inventoryCrafting, world)) {
                    return recipe;
                }
            }
        }

        for (EFabShapelessRecipe recipe : shapelessRecipes) {
            if (availableTiers.containsAll(recipe.getRequiredTiers())) {
                if (recipe.matches(inventoryCrafting, world)) {
                    return recipe;
                }
            }
        }
        return null;
    }

}
