package mcjty.efab.blocks.boiler;

import mcjty.efab.blocks.steamengine.SteamEngineRenderer;
import mcjty.efab.render.ParticleRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BoilerRenderer extends TileEntitySpecialRenderer<BoilerTE> {

    private static ParticleRenderer.Particle[] particles = new ParticleRenderer.Particle[] {
            new ParticleRenderer.Particle(-.2, -.13),
            new ParticleRenderer.Particle(-.1, -.2),
            new ParticleRenderer.Particle(0, 0),
            new ParticleRenderer.Particle(.2, 0.24),
            new ParticleRenderer.Particle(.1, .1)
    };

    @Override
    public void render(BoilerTE te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
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
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

            for (ParticleRenderer.Particle particle : particles) {
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
