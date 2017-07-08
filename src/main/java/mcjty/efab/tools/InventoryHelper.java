package mcjty.efab.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * Created by jorrit on 22-3-2017.
 */
public class InventoryHelper {

    public static void giveItemToPlayer(EntityPlayer player, ItemStack stack) {
        ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            player.setHeldItem(EnumHand.MAIN_HAND, stack);
            player.openContainer.detectAndSendChanges();
            return;
        } else if (isItemStackConsideredEqual(heldItem, stack)) {
            if (heldItem.getCount() < heldItem.getMaxStackSize()) {
                int itemsToAdd = Math.min(stack.getCount(), heldItem.getMaxStackSize() - heldItem.getCount());
                heldItem.grow(itemsToAdd);
                int amount = -itemsToAdd;
                stack.grow(amount);
                if (stack.isEmpty()) {
                    player.openContainer.detectAndSendChanges();
                    return;
                }
            }
        }
        // We have items remaining. Add them elsewhere
        if (player.inventory.addItemStackToInventory(stack)) {
            player.openContainer.detectAndSendChanges();
            return;
        }
        // Spawn in world
        net.minecraft.inventory.InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(),
                player.getPosition().getY(), player.getPosition().getZ(), stack);
    }

    public static boolean isItemStackConsideredEqual(ItemStack result, ItemStack itemstack1) {
        return !itemstack1.isEmpty() && itemstack1.getItem() == result.getItem() && (!result.getHasSubtypes() || result.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(result, itemstack1);
    }


}
