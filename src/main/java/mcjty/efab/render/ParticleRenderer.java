package mcjty.efab.render;

import mcjty.efab.EFab;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ParticleRenderer {

    public static ResourceLocation particles = new ResourceLocation(EFab.MODID, "textures/effects/particles.png");

    public static void renderParticleSystem(VertexBuffer buffer) {
        long time = System.currentTimeMillis();

        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;
//        for (IParticle particle : calculated.getParticles()) {
//            buffer.pos(ox - scale, oy-scale, oz).tex(u1, v1).lightmap(b1, b2).color(r, g, b, a).endVertex();
//            buffer.pos(ox - scale, oy+scale, oz).tex(u1, v2).lightmap(b1, b2).color(r, g, b, a).endVertex();
//            buffer.pos(ox + scale, oy+scale, oz).tex(u2, v2).lightmap(b1, b2).color(r, g, b, a).endVertex();
//            buffer.pos(ox + scale, oy-scale, oz).tex(u2, v1).lightmap(b1, b2).color(r, g, b, a).endVertex();
//        }
    }

    public static void renderSystem(float x, float y, float z) {
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

//        GlStateManager.enableAlpha();
        GlStateManager.disableAlpha();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
//        GlStateManager.scale(.1f, .1f, .1f);

//        this.bindTexture(blueSphereTexture);

        RenderTools.rotateToPlayer();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        renderParticleSystem(buffer);

        tessellator.draw();
        GlStateManager.popMatrix();

//        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
}
