package mcjty.efab.items;

import mcjty.efab.EFab;
import mcjty.efab.recipes.RecipeTier;
import mcjty.lib.McJtyRegister;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class UpgradeItem extends Item {

    public UpgradeItem(String name) {
        super();
        setMaxStackSize(16);
        setRegistryName(name);
        setUnlocalizedName(EFab.MODID + "." + name);
        setCreativeTab(EFab.tabEFab);
        McJtyRegister.registerLater(this, EFab.instance);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    public abstract RecipeTier providesTier();

    public abstract int getPriority();  // Used to get a deterministic ordering for the best grid to use for auto crafting. These priorities are actually flags that have to be added

}
