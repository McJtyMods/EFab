package mcjty.efab.network;

import io.netty.buffer.ByteBuf;
import mcjty.efab.EFab;
import mcjty.lib.network.IClientCommandHandler;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.network.PacketListToClient;
import mcjty.lib.varia.Logging;
import mcjty.lib.typed.Type;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketMonitorTextReady extends PacketListToClient<String> {

    public PacketMonitorTextReady() {
    }

    public PacketMonitorTextReady(BlockPos pos, String command, List<String> list) {
        super(pos, command, list);
    }

    public static class Handler implements IMessageHandler<PacketMonitorTextReady, IMessage> {
        @Override
        public IMessage onMessage(PacketMonitorTextReady message, MessageContext ctx) {
            EFab.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketMonitorTextReady message, MessageContext ctx) {
            TileEntity te = EFab.proxy.getClientWorld().getTileEntity(message.pos);
            if(!(te instanceof IClientCommandHandler)) {
                Logging.log("TileEntity is not a ClientCommandHandler!");
                return;
            }
            IClientCommandHandler clientCommandHandler = (IClientCommandHandler) te;
            if (!clientCommandHandler.receiveListFromServer(message.command, message.list, Type.STRING)) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }

    @Override
    protected String createItem(ByteBuf buf) {
        return NetworkTools.readStringUTF8(buf);
    }

    @Override
    protected void writeItemToBuf(ByteBuf buf, String s) {
        NetworkTools.writeStringUTF8(buf, s);
    }
}
