package mcjty.efab.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class StandardRecipes {

    public static void init() {
        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(Blocks.FURNACE),
                "ccc", "c c", "ccc", 'c', Blocks.COBBLESTONE));

        RecipeManager.registerRecipe(new EFabShapelessRecipe(
                new ItemStack(Items.GLOWSTONE_DUST),
                "r", 'r', Items.REDSTONE)
                .tier(RecipeTier.RF)
                .rfPerTick(10)
                .time(100));

        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(Blocks.PISTON),
                "ppp", "crc", "cic", 'p', "plankWood", 'c', "cobblestone", 'r', Items.REDSTONE, 'i', "ingotIron")
                .time(20)
                .tier(RecipeTier.GEARBOX));

        RecipeManager.registerRecipe(new EFabShapedRecipe(
                new ItemStack(Blocks.OBSIDIAN),
                "ccc", "ccc", "ccc", 'c', "cobblestone")
                .time(60)
                .tier(RecipeTier.STEAM));

        RecipeManager.registerRecipe(new EFabShapelessRecipe(
                new ItemStack(Items.WATER_BUCKET),
                "b", 'b', Items.BUCKET)
                .time(40)
                .tier(RecipeTier.LIQUID)
                .fluid(new FluidStack(FluidRegistry.WATER, 1000)));
        RecipeManager.registerRecipe(new EFabShapelessRecipe(
                new ItemStack(Items.LAVA_BUCKET),
                "b", 'b', Items.BUCKET)
                .time(40)
                .tier(RecipeTier.LIQUID)
                .fluid(new FluidStack(FluidRegistry.LAVA, 1000)));
    }

}
