package mcjty.efab;


import mcjty.efab.proxy.CommonProxy;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.CompatCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = EFab.MODID, name = EFab.MODNAME,
        dependencies =
                        "required-after:compatlayer@[" + EFab.COMPATLAYER_VER + ",);" +
                        "after:Forge@[" + EFab.MIN_FORGE10_VER + ",);" +
                        "after:forge@[" + EFab.MIN_FORGE11_VER + ",)",
        version = EFab.MODVERSION,
        acceptedMinecraftVersions = "[1.10,1.12)")
public class EFab implements ModBase {

    public static final String MODID = "efab";
    public static final String MODNAME = "EFab";
    public static final String MODVERSION = "0.0.1";

    public static final String MIN_FORGE10_VER = "12.18.2.2116";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String COMPATLAYER_VER = "0.2.1";

    @SidedProxy(clientSide = "mcjty.efab.proxy.ClientProxy", serverSide = "mcjty.efab.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static EFab instance;
    public static Logger logger;

    public static CreativeTabs tabEFab = new CompatCreativeTabs("EFab") {
        @Override
        protected Item getItem() {
            return Item.getItemFromBlock(Blocks.CRAFTING_TABLE);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
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

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookindex, String page) {

    }
}
