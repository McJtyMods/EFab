package mcjty.efab.blocks.poweroptimizer;

import mcjty.efab.EFab;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PowerOptimizerRenderer extends TileEntitySpecialRenderer<PowerOptimizerTE> {

    private static ResourceLocation glow = new ResourceLocation(EFab.MODID, "textures/effects/glow.png");

    @Override
    public void render(PowerOptimizerTE te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        this.bindTexture(glow);
        long time = System.currentTimeMillis();
        long scaleL = Math.abs(time % 1000);
        if ((time / 1000) % 2 == 0) {
            scaleL = 1000-scaleL;
        }
        float scale = (scaleL / 1000.0f) * .1f + 0.3f;

        RenderHelper.renderBillboardQuadBright(scale);
        GlStateManager.popMatrix();

        GlStateManager.enableLighting();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
}
