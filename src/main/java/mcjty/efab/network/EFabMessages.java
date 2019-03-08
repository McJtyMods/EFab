package mcjty.efab.network;

import mcjty.efab.EFab;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.thirteen.ChannelBuilder;
import mcjty.lib.thirteen.SimpleChannel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class EFabMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = ChannelBuilder
                .named(new ResourceLocation(EFab.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net.getNetwork();

        // Server side
        net.registerMessageServer(id(), PacketGetGridStatus.class, PacketGetGridStatus::toBytes, PacketGetGridStatus::new, PacketGetGridStatus::handle);
        net.registerMessageServer(id(), PacketGetMonitorText.class, PacketGetMonitorText::toBytes, PacketGetMonitorText::new, PacketGetMonitorText::handle);
        net.registerMessageServer(id(), PacketSendRecipe.class, PacketSendRecipe::toBytes, PacketSendRecipe::new, PacketSendRecipe::handle);

        // Client side
        net.registerMessageClient(id(), PacketReturnGridStatus.class, PacketReturnGridStatus::toBytes, PacketReturnGridStatus::new, PacketReturnGridStatus::handle);
        net.registerMessageClient(id(), PacketMonitorTextReady.class, PacketMonitorTextReady::toBytes, PacketMonitorTextReady::new, PacketMonitorTextReady::handle);
    }

    private static int id() {
        return PacketHandler.nextPacketID();
    }
}
