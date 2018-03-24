package mcjty.efab.network;

import io.netty.buffer.ByteBuf;
import mcjty.efab.EFab;
import mcjty.efab.blocks.crafter.CrafterTE;
import mcjty.efab.blocks.grid.GridTE;
import mcjty.lib.network.NetworkTools;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PacketReturnGridStatus implements IMessage {
    private BlockPos pos;
    private int ticks;
    private int total;
    private List<String> errors;
    private List<String> usage;
    private List<ItemStack> outputs;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        ticks = buf.readInt();
        total = buf.readInt();

        int size = buf.readInt();
        errors = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            errors.add(NetworkTools.readStringUTF8(buf));
        }

        size = buf.readInt();
        usage = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            usage.add(NetworkTools.readStringUTF8(buf));
        }

        size = buf.readInt();
        outputs = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            outputs.add(NetworkTools.readItemStack(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeInt(ticks);
        buf.writeInt(total);
        buf.writeInt(errors.size());
        for (String error : errors) {
            NetworkTools.writeStringUTF8(buf, error);
        }
        buf.writeInt(usage.size());
        for (String use : usage) {
            NetworkTools.writeStringUTF8(buf, use);
        }
        buf.writeInt(outputs.size());
        for (ItemStack output : outputs) {
            NetworkTools.writeItemStack(buf, output);
        }
    }

    public PacketReturnGridStatus() {
    }

    public PacketReturnGridStatus(BlockPos pos, GridTE gridTE) {
        this.pos = pos;
        this.ticks = gridTE.getTicksRemaining();
        this.total = gridTE.getTotalTicks();
        this.errors = gridTE.getErrorState();
        this.usage = gridTE.getUsage();
        this.outputs = gridTE.getOutputs();
    }

    public PacketReturnGridStatus(BlockPos pos, CrafterTE crafterTE) {
        this.pos = pos;
        this.ticks = 0;
        this.total = 0;
        this.errors = Collections.singletonList(crafterTE.getLastError());
        this.usage = Collections.emptyList();
        this.outputs = crafterTE.getOutputs();
    }

    public static class Handler implements IMessageHandler<PacketReturnGridStatus, IMessage> {
        @Override
        public IMessage onMessage(PacketReturnGridStatus message, MessageContext ctx) {
            EFab.proxy.addScheduledTaskClient(() -> {
                TileEntity te = EFab.proxy.getClientWorld().getTileEntity(message.pos);
                if (te instanceof GridTE) {
                    ((GridTE) te).syncFromServer(message.ticks, message.total, message.errors, message.outputs, message.usage);
                } else if (te instanceof CrafterTE) {
                    ((CrafterTE) te).syncFromServer(message.errors, message.outputs);
                }
            });
            return null;
        }

    }
}