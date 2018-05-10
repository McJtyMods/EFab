package mcjty.efab.network;

import mcjty.efab.EFab;
import mcjty.lib.network.PacketRequestServerList;
import mcjty.lib.network.PacketRequestServerListHandler;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.List;

public class PacketGetMonitorText extends PacketRequestServerList<String> {

    public static String CMD_GETMESSAGES = "getMessages";
    public static String CLIENTCMD_GETMESSAGES = "getMessages";

    public PacketGetMonitorText() {
    }

    public PacketGetMonitorText(BlockPos pos) {
        super(EFab.MODID, pos, CMD_GETMESSAGES, TypedMap.EMPTY);
    }

    public static class Handler extends PacketRequestServerListHandler<PacketGetMonitorText, String> {

        public Handler() {
            super(Type.STRING);
        }

        @Override
        protected void sendToClient(BlockPos pos, @Nonnull List<String> list, MessageContext messageContext) {
            EFabMessages.INSTANCE.sendTo(new PacketMonitorTextReady(pos, CLIENTCMD_GETMESSAGES, list), messageContext.getServerHandler().player);
        }
    }
}
