package mcjty.efab.network;

import io.netty.buffer.ByteBuf;
import mcjty.efab.EFab;
import mcjty.efab.blocks.grid.GridTE;
import mcjty.lib.network.NetworkTools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnGridStatus implements IMessage {
    private BlockPos pos;
    private int ticks;
    private int total;
    private String error;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        ticks = buf.readInt();
        total = buf.readInt();
        error = NetworkTools.readStringUTF8(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeInt(ticks);
        buf.writeInt(total);
        NetworkTools.writeStringUTF8(buf, error);
    }

    public PacketReturnGridStatus() {
    }

    public PacketReturnGridStatus(BlockPos pos, GridTE gridTE) {
        this.pos = pos;
        this.ticks = gridTE.getTicksRemaining();
        this.total = gridTE.getTotalTicks();
        this.error = gridTE.getErrorState();
    }

    public static class Handler implements IMessageHandler<PacketReturnGridStatus, IMessage> {
        @Override
        public IMessage onMessage(PacketReturnGridStatus message, MessageContext ctx) {
            EFab.proxy.addScheduledTaskClient(() -> {
                TileEntity te = EFab.proxy.getClientWorld().getTileEntity(message.pos);
                if (te instanceof GridTE) {
                    ((GridTE) te).syncFromServer(message.ticks, message.total, message.error);
                }
            });
            return null;
        }

    }
}