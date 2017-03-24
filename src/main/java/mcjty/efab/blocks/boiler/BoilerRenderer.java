package mcjty.efab.blocks.boiler;

import mcjty.efab.blocks.ModBlocks;
import mcjty.efab.render.ParticleRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BoilerRenderer extends TileEntitySpecialRenderer<BoilerTE> {

    @Override
    public void renderTileEntityAt(BoilerTE te, double x, double y, double z, float partialTicks, int destroyStage) {
//        IBlockState blockState = getWorld().getBlockState(te.getPos());
//        if (blockState.getBlock() != ModBlocks.boilerBlock) {
//            return;
//        }
//        this.bindTexture(ParticleRenderer.particles);
//        ParticleRenderer.renderSystem((float) x, (float) y, (float) z);
    }

}
