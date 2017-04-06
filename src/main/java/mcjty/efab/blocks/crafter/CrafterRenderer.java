package mcjty.efab.blocks.crafter;

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

public class CrafterRenderer extends TileEntitySpecialRenderer<CrafterTE> {

    private IModel wheelModel;
    private IBakedModel bakedWheelModel;

    private IBakedModel getBakedWheelModel() {
        // Since we cannot bake in preInit() we do lazy baking of the model as soon as we need it
        // for rendering
        if (bakedWheelModel == null) {
            try {
                wheelModel = ModelLoaderRegistry.getModel(new ResourceLocation(EFab.MODID, "block/wheel.obj"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bakedWheelModel = wheelModel.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM,
                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedWheelModel;
    }


    private IModel pipeModel;
    private IBakedModel bakedPipeModel;

    private IBakedModel getBakedPipeModel() {
        // Since we cannot bake in preInit() we do lazy baking of the model as soon as we need it
        // for rendering
        if (bakedPipeModel == null) {
            try {
                pipeModel = ModelLoaderRegistry.getModel(new ResourceLocation(EFab.MODID, "block/pipe.obj"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bakedPipeModel = pipeModel.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM,
                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedPipeModel;
    }



    @Override
    public void renderTileEntityAt(CrafterTE te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + .5, y, z + .5);
        GlStateManager.disableRescaleNormal();

        rotateFacing(te);
        renderWheel(te);

        GlStateManager.popMatrix();

    }

    public static void rotateFacing(TileEntity tileEntity) {
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

    private float cnt = 0;

    private static final int SPEEDCYCLE = 2000;
    private static final int SPEEDCYCLE2 = 2500;

    protected void renderWheel(CrafterTE tileEntity) {

        float t = ((long)(2000.0f * 2.0f * (((int)cnt) % SPEEDCYCLE)) / SPEEDCYCLE) % 2000;
        cnt += tileEntity.getSpeed();
        if (cnt > SPEEDCYCLE) {
            cnt -= SPEEDCYCLE;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, .5, 0);
        GlStateManager.rotate(360.0f * t / 2000.0f, 0, 0, 1);
        GlStateManager.translate(0, -.5, 0);

        GlStateManager.translate(-tileEntity.getPos().getX()-.5, -tileEntity.getPos().getY() - 1, -tileEntity.getPos().getZ() + .2);

        renderModel(tileEntity, getBakedWheelModel());

        GlStateManager.popMatrix();


        int t2 = (2000 * 2 * (((int)cnt) % SPEEDCYCLE) / SPEEDCYCLE) % 2000;
        float offs;
        if (t2 <= 1000) {
            offs = t2 / 2000.0f;
        } else {
            offs = (2000-t2) / 2000.0f;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(-tileEntity.getPos().getX()-.5, -tileEntity.getPos().getY() - 1 + offs, -tileEntity.getPos().getZ() - .4);

        renderModel(tileEntity, getBakedPipeModel());

        GlStateManager.popMatrix();


        t2 = (2000 * 2 * (((int)(cnt * 1.3f)) % SPEEDCYCLE) / SPEEDCYCLE) % 2000;
        if (t2 <= 1000) {
            offs = t2 / 2000.0f;
        } else {
            offs = (2000-t2) / 2000.0f;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(-tileEntity.getPos().getX(), -tileEntity.getPos().getY() - 1 + offs, -tileEntity.getPos().getZ() - .4);

        renderModel(tileEntity, getBakedPipeModel());

        GlStateManager.popMatrix();
    }

    private void renderModel(CrafterTE tileEntity, IBakedModel model) {
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
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, model, world.getBlockState(tileEntity.getPos()),
                tileEntity.getPos().up(),       // To fix chest lid lighting on the underside
                Tessellator.getInstance().getBuffer(), true);
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
    }


}
