package mcjty.efab.jei.grid;

import mcjty.efab.blocks.grid.GridContainer;
import mcjty.efab.network.EFabMessages;
import mcjty.lib.jei.CompatRecipeTransferHandler;
import mcjty.lib.tools.ItemStackList;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class GridTransferHandler implements CompatRecipeTransferHandler {

    @Override
    public Class getContainerClass() {
        return GridContainer.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = recipeLayout.getItemStacks().getGuiIngredients();

        GridContainer containerWorktable = (GridContainer) container;
        IInventory inventory = containerWorktable.getInventory(GridContainer.CONTAINER_INVENTORY);
        BlockPos pos = ((TileEntity) inventory).getPos();

        if (doTransfer) {
            transferRecipe(guiIngredients, pos);
        }

        return null;
    }

    public static void transferRecipe(Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients, BlockPos pos) {
        ItemStackList items = ItemStackList.create(10);
        for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
            int recipeSlot = entry.getKey();
            List<ItemStack> allIngredients = entry.getValue().getAllIngredients();
            if (!allIngredients.isEmpty()) {
                items.set(recipeSlot, allIngredients.get(0));
            }
        }

        EFabMessages.INSTANCE.sendToServer(new PacketSendRecipe(items, pos));
    }


}
