package mcjty.efab.blocks;

import mcjty.efab.EFab;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.container.GenericItemBlock;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GenericEFabBlockWithTE<T extends GenericTileEntity, C extends Container> extends GenericBlock<T, C> {

    public GenericEFabBlockWithTE(Material material,
                                  Class<? extends T> tileEntityClass,
                                  Class<? extends C> containerClass,
                                  String name, boolean isContainer) {
        this(material, tileEntityClass, containerClass, GenericItemBlock.class, name, isContainer);
    }

    public GenericEFabBlockWithTE(Material material,
                                  Class<? extends T> tileEntityClass,
                                  Class<? extends C> containerClass,
                                  Class<? extends ItemBlock> itemBlockClass,
                                  String name, boolean isContainer) {
        super(EFab.instance, material, tileEntityClass, containerClass, name, isContainer);
        setCreativeTab(EFab.tabEFab);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return null;
    }
}
