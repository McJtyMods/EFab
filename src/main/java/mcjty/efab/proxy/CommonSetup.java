package mcjty.efab.proxy;

import mcjty.efab.EFab;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.compat.botania.BotaniaSupportSetup;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.items.ModItems;
import mcjty.efab.network.EFabMessages;
import mcjty.efab.recipes.RecipeManager;
import mcjty.efab.recipes.StandardRecipes;
import mcjty.lib.McJtyRegister;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.setup.DefaultCommonSetup;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import java.io.File;

public class CommonSetup extends DefaultCommonSetup {

    public static boolean botania;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        botania = Loader.isModLoaded("botania") || Loader.isModLoaded("Botania");

        MinecraftForge.EVENT_BUS.register(this);

        RecipeManager.init();

        mainConfig = new Configuration(new File(modConfigDir.getPath(), "efab.cfg"));

        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(EFab.MODID, "efab");
        EFabMessages.registerNetworkMessages(network);

        ModItems.init();
        ModBlocks.init();
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



    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(GeneralConfiguration.CATEGORY_GENERAL, "General settings");

            GeneralConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        NetworkRegistry.INSTANCE.registerGuiHandler(EFab.instance, new GuiProxy());
//        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
//        ModRecipes.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        mainConfig = null;
        StandardRecipes.init();
        if (botania) {
            BotaniaSupportSetup.postInit();
        }
        File file = new File(modConfigDir.getPath(), "efab_recipes.json");
        StandardRecipes.readRecipesConfig(file);
    }
}
