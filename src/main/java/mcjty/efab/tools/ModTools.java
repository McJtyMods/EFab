package mcjty.efab.tools;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

public class ModTools {
    public static String getModidForBlock(Block block) {
        ResourceLocation nameForObject = GameData.getBlockRegistry().getNameForObject(block);
        if (nameForObject == null) {
            return "?";
        }
        return nameForObject.getResourceDomain();
    }

    public static String getModidForItem(Item item) {
        ResourceLocation nameForObject = GameData.getItemRegistry().getNameForObject(item);
        if (nameForObject == null) {
            return "?";
        }
        return nameForObject.getResourceDomain();
    }


    public static String getMod(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();
            if (block != null) {
                return getModidForBlock(block);
            }
            return "Unknown";
        } else {
            if (item != null) {
                return getModidForItem(item);
            }
            return "Unknown";
        }
    }

}
