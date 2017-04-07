package mcjty.efab.network;

import io.netty.buffer.ByteBuf;
import mcjty.efab.blocks.crafter.CrafterTE;
import mcjty.efab.blocks.grid.GridTE;
import mcjty.lib.network.NetworkTools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

    public PacketGetGridStatus(BlockPos pos) {
        this.pos = pos;
    }

    public static class Handler implements IMessageHandler<PacketGetGridStatus, PacketReturnGridStatus> {
        @Override
        public PacketReturnGridStatus onMessage(PacketGetGridStatus message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetGridStatus message, MessageContext ctx) {
            World world = ctx.getServerHandler().player.getEntityWorld();
            TileEntity te = world.getTileEntity(message.pos);
            if (te instanceof GridTE) {
                GridTE gridTE = (GridTE) te;
                PacketReturnGridStatus returnMessage = new PacketReturnGridStatus(message.pos, gridTE);
                EFabMessages.INSTANCE.sendTo(returnMessage, ctx.getServerHandler().player);
            } else if (te instanceof CrafterTE) {
                CrafterTE crafterTE = (CrafterTE) te;
                PacketReturnGridStatus returnMessage = new PacketReturnGridStatus(message.pos, crafterTE);
                EFabMessages.INSTANCE.sendTo(returnMessage, ctx.getServerHandler().player);
            }
        }
    }
}