package mcjty.efab.items;

import mcjty.efab.EFab;
import mcjty.lib.compat.CompatItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpgradeItem extends CompatItem {

    public UpgradeItem(String name) {
        super();
        setMaxStackSize(16);
        setRegistryName(name);
        setUnlocalizedName(EFab.MODID + "." + name);
        setCreativeTab(EFab.tabEFab);
        GameRegistry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
