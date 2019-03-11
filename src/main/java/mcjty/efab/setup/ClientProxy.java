package mcjty.efab.setup;

import mcjty.efab.EFab;
import mcjty.lib.McJtyLibClient;
import mcjty.lib.setup.DefaultClientProxy;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        OBJLoader.INSTANCE.addDomain(EFab.MODID);
        McJtyLibClient.preInit(e);
    }
}
