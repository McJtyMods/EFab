package mcjty.efab.network;

import io.netty.buffer.ByteBuf;
import mcjty.efab.EFab;
import mcjty.efab.blocks.crafter.CrafterTE;
import mcjty.efab.blocks.grid.GridTE;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.thirteen.Context;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

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

        errors = NetworkTools.readStringList(buf);
        usage = NetworkTools.readStringList(buf);
        outputs = NetworkTools.readItemStackList(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeInt(ticks);
        buf.writeInt(total);
        NetworkTools.writeStringList(buf, errors);
        NetworkTools.writeStringList(buf, usage);
        NetworkTools.writeItemStackList(buf, outputs);
    }

    public PacketReturnGridStatus() {
    }

    public PacketReturnGridStatus(ByteBuf buf) {
        fromBytes(buf);
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

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = EFab.proxy.getClientWorld().getTileEntity(pos);
            if (te instanceof GridTE) {
                ((GridTE) te).syncFromServer(ticks, total, errors, outputs, usage);
            } else if (te instanceof CrafterTE) {
                ((CrafterTE) te).syncFromServer(errors, outputs);
            }
        });
        ctx.setPacketHandled(true);
    }
}