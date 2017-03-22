package mcjty.efab.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class EFabMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
//        net.registerMessage(PacketGetChannels.Handler.class, PacketGetChannels.class, PacketHandler.nextID(), Side.SERVER);

        // Client side
//        net.registerMessage(PacketChannelsReady.Handler.class, PacketChannelsReady.class, PacketHandler.nextID(), Side.CLIENT);
    }
}
