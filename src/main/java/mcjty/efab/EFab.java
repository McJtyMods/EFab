package mcjty.efab;


import mcjty.commands.CmdSaveDefaults;
import mcjty.efab.proxy.CommonProxy;
import mcjty.lib.base.ModBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = EFab.MODID, name = EFab.MODNAME,
        dependencies =
                "required-after:mcjtylib_ng@[" + EFab.MIN_MCJTYLIB_VER + ",);" +
                "after:forge@[" + EFab.MIN_FORGE11_VER + ",)",
        version = EFab.MODVERSION)
public class EFab implements ModBase {

    public static final String MODID = "efab";
    public static final String MODNAME = "EFab";
    public static final String MODVERSION = "0.0.28";
    public static final String MIN_MCJTYLIB_VER = "2.6.3";

    public static final String MIN_FORGE11_VER = "13.19.0.2176";

    @SidedProxy(clientSide = "mcjty.efab.proxy.ClientProxy", serverSide = "mcjty.efab.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static EFab instance;

    public static Logger logger;

    public static boolean botania;

    public static CreativeTabs tabEFab = new CreativeTabs("EFab") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Blocks.CRAFTING_TABLE);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        botania = Loader.isModLoaded("botania") || Loader.isModLoaded("Botania");

        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CmdSaveDefaults());
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookindex, String page) {

    }
}
