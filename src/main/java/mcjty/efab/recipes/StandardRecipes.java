package mcjty.efab.recipes;

import mcjty.lib.tools.ItemStackTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class StandardRecipes {

    public static void init() {
        RecipeManager.registerRecipe(new EFabShapedRecipe(new ItemStack[] {
                new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.COBBLESTONE),
                new ItemStack(Blocks.COBBLESTONE), ItemStackTools.getEmptyStack(), new ItemStack(Blocks.COBBLESTONE),
                new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.COBBLESTONE)
        }, new ItemStack(Blocks.FURNACE)));

        RecipeManager.registerRecipe(new EFabShapelessRecipe(new ItemStack[] {
                new ItemStack(Items.BUCKET)
        }, new ItemStack(Items.WATER_BUCKET))
                .time(40)
                .tier(RecipeTier.LIQUID)
                .fluid(new FluidStack(FluidRegistry.WATER, 1000)));
        RecipeManager.registerRecipe(new EFabShapelessRecipe(new ItemStack[] {
                new ItemStack(Items.BUCKET)
        }, new ItemStack(Items.LAVA_BUCKET))
                .time(40)
                .tier(RecipeTier.LIQUID)
                .fluid(new FluidStack(FluidRegistry.LAVA, 1000)));
    }

}
