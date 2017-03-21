package mcjty.efab.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import scala.actors.threadpool.Arrays;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class EFabShapelessRecipe extends ShapelessRecipes {

    private final Set<RecipeTier> requiredTiers = EnumSet.noneOf(RecipeTier.class);

    public EFabShapelessRecipe(ItemStack[] ingredientsIn, ItemStack output, RecipeTier... tiers) {
        super(output, Arrays.asList(ingredientsIn));
        Collections.addAll(requiredTiers, tiers);
    }

    public Set<RecipeTier> getRequiredTiers() {
        return requiredTiers;
    }
}
