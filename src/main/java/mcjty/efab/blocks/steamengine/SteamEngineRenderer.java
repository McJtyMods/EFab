package mcjty.efab.blocks.steamengine;

import mcjty.efab.EFab;
import mcjty.lib.container.GenericBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

public class SteamEngineRenderer extends TileEntitySpecialRenderer<SteamEngineTE> {

    private IModel lidModel;
    private IBakedModel bakedLidModel;

    private IBakedModel getBakedLidModel() {
        // Since we cannot bake in preInit() we do lazy baking of the model as soon as we need it
        // for rendering
        if (bakedLidModel == null) {
            try {
                lidModel = ModelLoaderRegistry.getModel(new ResourceLocation(EFab.MODID, "block/wheel.obj"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bakedLidModel = lidModel.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM,
                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedLidModel;
    }



    @Override
    public void renderTileEntityAt(SteamEngineTE te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + .5, y, z + .5);
        GlStateManager.disableRescaleNormal();

        rotateFacing(te);
        renderWheel(te);

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

    private int cnt = 0;

    protected void renderWheel(SteamEngineTE tileEntity) {
        GlStateManager.pushMatrix();

        if (cnt >= 0) {
            float t = ((long)(2000.0f * 2.0f * tileEntity.getSpeed() * (float) (cnt % 1000)) / 1000) % 2000;
            cnt++;
            GlStateManager.translate(0, .5, 0);
            GlStateManager.rotate(360.0f * t / 2000.0f, 0, 0, 1);
            GlStateManager.translate(0, -.5, 0);
        }

        GlStateManager.translate(-tileEntity.getPos().getX()-.5, -tileEntity.getPos().getY() - 1, -tileEntity.getPos().getZ());


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
