package mcjty.xnet;


import com.google.common.base.Function;
import com.google.common.base.Optional;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.CompatCreativeTabs;
import mcjty.xnet.api.IEFab;
import mcjty.xnet.apiimpl.EFabApi;
import mcjty.xnet.commands.CommandCheck;
import mcjty.xnet.commands.CommandDump;
import mcjty.xnet.commands.CommandGen;
import mcjty.xnet.commands.CommandRebuild;
import mcjty.xnet.items.manual.GuiEFabManual;
import mcjty.xnet.multiblock.EFabBlobData;
import mcjty.xnet.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(modid = EFab.MODID, name = EFab.MODNAME,
        dependencies =
                        "required-after:mcjtylib_ng@[" + EFab.MIN_MCJTYLIB_VER + ",);" +
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
    public static final String MIN_MCJTYLIB_VER = "2.3.8";
    public static final String COMPATLAYER_VER = "0.2.1";

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    @SidedProxy(clientSide = "mcjty.xnet.proxy.ClientProxy", serverSide = "mcjty.xnet.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static EFab instance;
    public static Logger logger;

    public static EFabApi xNetApi = new EFabApi();

    public static CreativeTabs tabEFab = new CompatCreativeTabs("EFab") {
        @Override
        protected Item getItem() {
            return Item.getItemFromBlock(Blocks.ANVIL);
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

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        EFabBlobData.clearInstance();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDump());
        event.registerServerCommand(new CommandGen());
        event.registerServerCommand(new CommandRebuild());
        event.registerServerCommand(new CommandCheck());
    }

    public String getModId() {
        return MODID;
    }

}
