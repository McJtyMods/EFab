package mcjty.efab.blocks.boiler;

import mcjty.efab.blocks.steamengine.SteamEngineRenderer;
import mcjty.efab.render.ParticleRenderer;
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

    private static class Particle {
        private final double offsetx;
        private final double offsety;

        public Particle(double offsetx, double offsety) {
            this.offsetx = offsetx;
            this.offsety = offsety;
        }

        public double getOffsetx() {
            return offsetx;
        }

        public double getOffsety() {
            return offsety;
        }
    }

    private static Particle[] particles = new Particle[] {
            new Particle(-.2, -.13),
            new Particle(-.1, -.2),
            new Particle(0, 0),
            new Particle(.2, 0.24),
            new Particle(.1, .1)
    };

    @Override
    public void renderTileEntityAt(BoilerTE te, double x, double y, double z, float partialTicks, int destroyStage) {
        this.bindTexture(ParticleRenderer.particles);

        if (te.getTimer() > 0) {
            float multiplier = Math.min(20.0f, (float) te.getTimer())/20.0f;
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GlStateManager.disableAlpha();


            //        GlStateManager.enableBlend();
            //        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            //        GlStateManager.disableAlpha();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);

            SteamEngineRenderer.rotateFacing(te);

            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

            for (Particle particle : particles) {
                long time = System.currentTimeMillis();

                double ox = particle.getOffsetx();
                double oy = (particle.getOffsety() + (((double) time / 2000.0))) % .4 - .1;
                double oz = .55;

                double scale = 0.2;
                int uu = (int) ((time / 200) % 4);
                double u1 = (uu * 32) / 128.0;
                double u2 = ((uu + 1) * 32) / 128.0;
                double v1 = 0;
                double v2 = 32.0 / 128;
                int b1 = 240;
                int b2 = 240;
                float r = 0.5f * multiplier;
                float g = 0.5f * multiplier;
                float b = 0.5f * multiplier;
                float a = 0.1f;
                buffer.pos(ox + scale, oy - scale, oz).tex(u1, v1).lightmap(b1, b2).color(r, g, b, a).endVertex();
                buffer.pos(ox + scale, oy + scale, oz).tex(u1, v2).lightmap(b1, b2).color(r, g, b, a).endVertex();
                buffer.pos(ox - scale, oy + scale, oz).tex(u2, v2).lightmap(b1, b2).color(r, g, b, a).endVertex();
                buffer.pos(ox - scale, oy - scale, oz).tex(u2, v1).lightmap(b1, b2).color(r, g, b, a).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();

            GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.depthMask(true);

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

}
