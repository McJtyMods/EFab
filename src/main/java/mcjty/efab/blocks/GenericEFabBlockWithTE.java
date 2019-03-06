package mcjty.efab.blocks;

import mcjty.efab.EFab;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.blocks.GenericItemBlock;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class GenericEFabBlockWithTE<T extends GenericTileEntity, C extends Container> extends GenericBlock<T, C> {

    public GenericEFabBlockWithTE(Material material,
                                  Class<? extends T> tileEntityClass,
                                  BiFunction<EntityPlayer, IInventory, C> containerFactory,
                                  String name, boolean isContainer) {
        this(material, tileEntityClass, containerFactory, GenericItemBlock::new, name, isContainer);
    }

    public GenericEFabBlockWithTE(Material material, Class<? extends T> tileEntityClass,
                                  BiFunction<EntityPlayer, IInventory, C> containerFactory,
                                  Function<Block, ItemBlock> itemBlockFunction,
                                  String name, boolean isContainer) {
        super(EFab.instance, material, tileEntityClass, containerFactory, itemBlockFunction, name, isContainer);
        setCreativeTab(EFab.setup.getTab());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BiFunction<T, C, GenericGuiContainer<? super T>> getGuiFactory() {
        return null;
    }
}
