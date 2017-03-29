package mcjty.efab.jei.grid;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class GridRecipeWrapper implements IShapedCraftingRecipeWrapper {

    private final Item item;

    public GridRecipeWrapper(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, new ItemStack(item));
    }


    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
//        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
//        InfusingBonus bonus = LaserTileEntity.infusingBonusMap.get(item.getRegistryName().toString());
//
//        renderStat("Purity:", bonus.getPurityModifier(), 30);
//        renderStat("Strength:", bonus.getStrengthModifier(), 40);
//        renderStat("Efficiency:", bonus.getEfficiencyModifier(), 50);
    }

//    private void renderStat(String label, InfusingBonus.Modifier modifier, int y) {
//        RenderHelper.getMCFontrenderer().drawString(label, 0, y, 0xffffffff, true);
//        float purityBonus = modifier.getBonus();
//        RenderHelper.getMCFontrenderer().drawString(String.valueOf(purityBonus)+"%", 60, y,
//                purityBonus > 0 ? 0xff006600 : 0xffff0000, false);
//        RenderHelper.getMCFontrenderer().drawString("(" + String.valueOf(modifier.getMaxOrMin()) + ")", 100, y,
//                0xff000000, false);
//    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
