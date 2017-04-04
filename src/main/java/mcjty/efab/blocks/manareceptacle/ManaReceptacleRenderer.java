package mcjty.efab.blocks.manareceptacle;

import mcjty.efab.EFab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

public class ManaReceptacleRenderer extends TileEntitySpecialRenderer<ManaReceptacleTE> {

    private IModel lidModel;
    private IBakedModel bakedLidModel;

    private IBakedModel getBakedLidModel() {
        // Since we cannot bake in preInit() we do lazy baking of the model as soon as we need it
        // for rendering
        if (bakedLidModel == null) {
            try {
                lidModel = ModelLoaderRegistry.getModel(new ResourceLocation(EFab.MODID, "block/manareceptacle.obj"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bakedLidModel = lidModel.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM,
                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedLidModel;
    }



    @Override
    public void renderTileEntityAt(ManaReceptacleTE te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + .5, y, z + .5);
        GlStateManager.disableRescaleNormal();

        renderWheel(te);

        GlStateManager.popMatrix();

    }

    private float cnt = 0;
    private float cnt2 = 0;

    private static final int SPEEDCYCLE = 2000;
    private static final int SPEEDCYCLE2 = 1500;

    protected void renderWheel(ManaReceptacleTE tileEntity) {
        GlStateManager.pushMatrix();

        GlStateManager.translate(0, .5, 0);

        float t = ((long)(2000.0f * 2.0f * (((int)cnt) % SPEEDCYCLE)) / SPEEDCYCLE) % 2000;
        cnt += tileEntity.getSpeed();
        if (cnt > SPEEDCYCLE) {
            cnt -= SPEEDCYCLE;
        }
        GlStateManager.rotate(360.0f * t / 2000.0f, 0, 0, 1);

        t = ((long)(1500.0f * 2.0f * (((int)cnt2) % SPEEDCYCLE)) / SPEEDCYCLE) % 1500;
        cnt2 += tileEntity.getSpeed();
        if (cnt2 > SPEEDCYCLE2) {
            cnt2 -= SPEEDCYCLE2;
        }
        GlStateManager.rotate(360.0f * t / 1500.0f, 1, 0, 0);

        GlStateManager.translate(0, -.5, 0);

        GlStateManager.translate(-tileEntity.getPos().getX()-.5, -tileEntity.getPos().getY() - 1, -tileEntity.getPos().getZ()-.5);


        RenderHelper.disableStandardItemLighting();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        World world = tileEntity.getWorld();
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, getBakedLidModel(), world.getBlockState(tileEntity.getPos()),
                tileEntity.getPos().up(),       // To fix chest lid lighting on the underside
                Tessellator.getInstance().getBuffer(), true);
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

}
