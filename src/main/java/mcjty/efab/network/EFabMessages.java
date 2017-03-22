package mcjty.efab.network;

import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class EFabMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
        net.registerMessage(PacketGetGridStatus.Handler.class, PacketGetGridStatus.class, PacketHandler.nextID(), Side.SERVER);

        // Client side
        net.registerMessage(PacketReturnGridStatus.Handler.class, PacketReturnGridStatus.class, PacketHandler.nextID(), Side.CLIENT);
    }
}
