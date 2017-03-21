package mcjty.efab.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.List;
import java.util.Set;

public class RecipeManager {

    static {
        RecipeSorter.register("efab:efabshaped", EFabShapedRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("efab:efabshapeless", EFabShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    public static void init() {
    }

    public static IRecipe findValidRecipe(InventoryCrafting inventoryCrafting, World world, Set<RecipeTier> availableTiers) {
        List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
        for (IRecipe recipe : recipeList) {
            if (recipe instanceof EFabShapedRecipe) {
                EFabShapedRecipe shapedRecipe = (EFabShapedRecipe) recipe;
                if (availableTiers.containsAll(shapedRecipe.getRequiredTiers())) {
                    if (shapedRecipe.matches(inventoryCrafting, world)) {
                        return shapedRecipe;
                    }
                }
            } else if (recipe instanceof EFabShapelessRecipe) {
                EFabShapelessRecipe shapelessRecipe = (EFabShapelessRecipe) recipe;
                if (availableTiers.containsAll(shapelessRecipe.getRequiredTiers())) {
                    if (shapelessRecipe.matches(inventoryCrafting, world)) {
                        return shapelessRecipe;
                    }
                }
            }
        }
        return null;
    }

}
