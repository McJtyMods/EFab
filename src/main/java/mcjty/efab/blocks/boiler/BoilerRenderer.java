package mcjty.efab.blocks.boiler;

import mcjty.efab.render.ParticleRenderer;
import mcjty.efab.render.RenderTools;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class BoilerRenderer extends TileEntitySpecialRenderer<BoilerTE> {

    private Random random = new Random();

    @Override
    public void renderTileEntityAt(BoilerTE te, double x, double y, double z, float partialTicks, int destroyStage) {
        this.bindTexture(ParticleRenderer.particles);

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        GlStateManager.disableAlpha();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);


        RenderTools.rotateToPlayer();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        double ox = 0;
        double oy = 0;
        double oz = -.7;
        double scale = 0.5;
        int uu = random.nextInt(4);
        double u1 = (uu * 32) / 256.0;
        double u2 = ((uu+1)*32) / 256.0;
        double v1 = 0;
        double v2 = 32.0/256;
        int b1 = 240;
        int b2 = 240;
        float r = 1.0f;
        float g = 1.0f;
        float b = 1.0f;
        float a = 0.9f;
        buffer.pos(ox - scale, oy-scale, oz).tex(u1, v1).lightmap(b1, b2).color(r, g, b, a).endVertex();
        buffer.pos(ox - scale, oy+scale, oz).tex(u1, v2).lightmap(b1, b2).color(r, g, b, a).endVertex();
        buffer.pos(ox + scale, oy+scale, oz).tex(u2, v2).lightmap(b1, b2).color(r, g, b, a).endVertex();
        buffer.pos(ox + scale, oy-scale, oz).tex(u2, v1).lightmap(b1, b2).color(r, g, b, a).endVertex();

        tessellator.draw();
        GlStateManager.popMatrix();

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

}
