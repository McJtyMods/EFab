package mcjty.efab.network;

import io.netty.buffer.ByteBuf;
import mcjty.efab.blocks.crafter.CrafterTE;
import mcjty.efab.blocks.grid.GridTE;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.thirteen.Context;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

public class PacketGetGridStatus implements IMessage {
    private BlockPos pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
    }

    public PacketGetGridStatus() {
    }

    public PacketGetGridStatus(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetGridStatus(BlockPos pos) {
        this.pos = pos;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getEntityWorld();
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof GridTE) {
                GridTE gridTE = (GridTE) te;
                PacketReturnGridStatus returnMessage = new PacketReturnGridStatus(pos, gridTE);
                EFabMessages.INSTANCE.sendTo(returnMessage, ctx.getSender());
            } else if (te instanceof CrafterTE) {
                CrafterTE crafterTE = (CrafterTE) te;
                PacketReturnGridStatus returnMessage = new PacketReturnGridStatus(pos, crafterTE);
                EFabMessages.INSTANCE.sendTo(returnMessage, ctx.getSender());
            }
        });
        ctx.setPacketHandled(true);
    }
}