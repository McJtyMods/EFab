package mcjty.efab.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class EFabShapedRecipe extends ShapedRecipes {

    private final Set<RecipeTier> requiredTiers = EnumSet.noneOf(RecipeTier.class);

    public EFabShapedRecipe(ItemStack[] ingredientsIn, ItemStack output, RecipeTier... tiers) {
        super(3, 3, ingredientsIn, output);
        Collections.addAll(requiredTiers, tiers);
    }

    public Set<RecipeTier> getRequiredTiers() {
        return requiredTiers;
    }
}
