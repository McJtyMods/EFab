package mcjty.efab.compat.jei.grid;

import mcjty.efab.EFab;
import mcjty.lib.jei.JeiCompatTools;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public class GridRecipeCategory extends BlankRecipeCategory<GridCraftingRecipeWrapper> {

    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot1 = 1;

    public static final int width = 140; //116;
    public static final int height = 110; //54;

    private final IGuiHelper guiHelper;
    private final IDrawable background;
//    private final String localizedName;
    private final ICraftingGridHelper craftingGridHelper;

    public static final String ID = "EFabGrid";

    public GridRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(EFab.MODID, "textures/gui/grid_jei.png");
        background = guiHelper.createDrawable(location, 29, 16, width, height);
//        localizedName = Translator.translateToLocal("gui.jei.category.craftingTable");
        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);

        this.guiHelper = guiHelper;
//        slot = guiHelper.getSlotDrawable();
    }

    @Nonnull
    @Override
    public String getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "EFab Grid";
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GridCraftingRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(craftOutputSlot, false, 94, 18);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot1 + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<ItemStack> outputs = JeiCompatTools.getOutputs(ingredients, ItemStack.class);

        JeiCompatTools.setInputs(craftingGridHelper, guiItemStacks, inputs, recipeWrapper.getWidth(), recipeWrapper.getHeight());
        IGuiIngredientGroup<ItemStack> g = guiItemStacks;
        g.set(craftOutputSlot, outputs);
    }

    @Override
    public String getModName() {
        return EFab.MODNAME;
    }
}
