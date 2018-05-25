package mcjty.efab.network;

import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class EFabMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
        net.registerMessage(PacketGetGridStatus.Handler.class, PacketGetGridStatus.class, PacketHandler.nextPacketID(), Side.SERVER);
        net.registerMessage(PacketGetMonitorText.Handler.class, PacketGetMonitorText.class, PacketHandler.nextPacketID(), Side.SERVER);
        net.registerMessage(PacketSendRecipe.Handler.class, PacketSendRecipe.class, PacketHandler.nextPacketID(), Side.SERVER);

        // Client side
        net.registerMessage(PacketReturnGridStatus.Handler.class, PacketReturnGridStatus.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketMonitorTextReady.Handler.class, PacketMonitorTextReady.class, PacketHandler.nextPacketID(), Side.CLIENT);
    }
}
