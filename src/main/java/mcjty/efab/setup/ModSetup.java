package mcjty.efab.setup;

import mcjty.efab.EFab;
import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.compat.botania.BotaniaSupportSetup;
import mcjty.efab.config.ConfigSetup;
import mcjty.efab.items.ModItems;
import mcjty.efab.network.EFabMessages;
import mcjty.efab.recipes.StandardRecipes;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ModSetup extends DefaultModSetup {

    public static boolean botania;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(EFab.instance, new GuiProxy());

        EFabMessages.registerMessages("efab");

        ModItems.init();
        ModBlocks.init();
    }

    @Override
    protected void setupModCompat() {
        botania = Loader.isModLoaded("botania") || Loader.isModLoaded("Botania");
        if (botania) {
            BotaniaSupportSetup.preInit();
        }
    }

    @Override
    protected void setupConfig() {
        ConfigSetup.init();
    }

    @Override
    public void createTabs() {
        createTab("EFab", () -> new ItemStack(Blocks.CRAFTING_TABLE));
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
        StandardRecipes.init();
        StandardRecipes.readRecipesConfig();
    }
}
