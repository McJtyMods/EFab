package mcjty.efab.proxy;

import mcjty.efab.EFab;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.compat.botania.BotaniaSupportSetup;
import mcjty.efab.config.ConfigSetup;
import mcjty.efab.items.ModItems;
import mcjty.efab.network.EFabMessages;
import mcjty.efab.recipes.RecipeManager;
import mcjty.efab.recipes.StandardRecipes;
import mcjty.lib.McJtyRegister;
import mcjty.lib.setup.DefaultCommonSetup;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.File;

public class CommonSetup extends DefaultCommonSetup {

    public static boolean botania;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(this);

        setupModCompat();

        RecipeManager.init();

        EFabMessages.registerMessages("efab");

        ConfigSetup.init();
        ModItems.init();
        ModBlocks.init();
    }

    private void setupModCompat() {
        botania = Loader.isModLoaded("botania") || Loader.isModLoaded("Botania");
        if (botania) {
            BotaniaSupportSetup.preInit();
        }
    }

    @Override
    public void createTabs() {
        createTab("EFab", new ItemStack(Blocks.CRAFTING_TABLE));
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        McJtyRegister.registerBlocks(EFab.instance, event.getRegistry());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        McJtyRegister.registerItems(EFab.instance, event.getRegistry());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        NetworkRegistry.INSTANCE.registerGuiHandler(EFab.instance, new GuiProxy());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
        StandardRecipes.init();
        if (botania) {
            BotaniaSupportSetup.postInit();
        }
        File file = new File(modConfigDir.getPath(), "efab_recipes.json");
        StandardRecipes.readRecipesConfig(file);
    }
}
