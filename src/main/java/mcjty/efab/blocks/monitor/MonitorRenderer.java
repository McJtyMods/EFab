package mcjty.efab.blocks.monitor;

import mcjty.lib.container.GenericBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class MonitorRenderer extends TileEntitySpecialRenderer<MonitorTE> {

    @Override
    public void renderTileEntityAt(MonitorTE te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + .5, y, z + .5);
        GlStateManager.disableRescaleNormal();

        rotateFacing(te);

        GlStateManager.popMatrix();

    }

    private static void rotateFacing(TileEntity tileEntity) {
        IBlockState state = tileEntity.getWorld().getBlockState(tileEntity.getPos());
        EnumFacing orientation = GenericBlock.getFacingHoriz(state.getBlock().getMetaFromState(state));
        switch (orientation) {
            case NORTH:
                GlStateManager.rotate(180, 0, 1, 0);
                break;
            case SOUTH:
                break;
            case WEST:
                GlStateManager.rotate(270, 0, 1, 0);
                break;
            case EAST:
                GlStateManager.rotate(90, 0, 1, 0);
                break;
            case DOWN:
            case UP:
                break;
        }
    }

}
