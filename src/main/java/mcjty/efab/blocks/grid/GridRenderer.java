package mcjty.efab.blocks.grid;

import mcjty.efab.blocks.ModBlocks;
import mcjty.lib.blocks.BaseBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class GridRenderer extends TileEntitySpecialRenderer<GridTE> {
    @Override
    public void render(GridTE te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        if (te.getWorld().isAirBlock(te.getPos())) {
            return;
        }

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + .5, y, z + .5);
        GlStateManager.disableRescaleNormal();

        rotateFacing(te);
        renderHandles(te);

        GlStateManager.popMatrix();

    }

    private static void rotateFacing(TileEntity tileEntity) {
        IBlockState state = tileEntity.getWorld().getBlockState(tileEntity.getPos());
        EnumFacing orientation = BaseBlock.getFrontDirection(ModBlocks.gridBlock.getRotationType(), state);
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

    private void renderHandles(GridTE tileEntity) {
//        double distanceSq = MinecraftTools.getPlayer(Minecraft.getMinecraft()).getDistanceSq(tileEntity.getPos());

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        boolean half = GridBlock.isHalfBlock(tileEntity.getWorld(), tileEntity.getPos());

        for (int x = 0 ; x < 3 ; x++) {
            for (int y = 0 ; y < 3 ; y++) {
                renderHandle(tileEntity, x, y, half);
            }
        }
    }

    private static void renderHandle(GridTE te, int x, int y, boolean half) {
        ItemStack stackInSlot = te.getStackInSlot(y * 3 + x);
        if (!stackInSlot.isEmpty()) {
            renderItemStackInWorld(new Vec3d(x * .3-.3, .1 + (half ? 0.2 : 1), y * .3-.3), stackInSlot);
        }
    }

    private static void renderItemStackInWorld(Vec3d offset, ItemStack stack) {
        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(offset.x, offset.y, offset.z);
        renderItemCustom(stack, 0, 0.1f);
        GlStateManager.translate(-offset.x, -offset.y, -offset.z);
    }

    public static void renderItemCustom(ItemStack is, int rotation, float scale) {
        if (!is.isEmpty()) {
            GlStateManager.pushMatrix();

            GlStateManager.scale(scale, scale, scale);
            if (rotation != 0) {
                GlStateManager.rotate(rotation, 0F, 1F, 0F);
            }

            customRenderItem(is);

            GlStateManager.popMatrix();
        }
    }

    public static void customRenderItem(ItemStack is) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

//        IBakedModel ibakedmodel = renderItem.getItemModelMesher().getItemModel(is);
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        IBakedModel ibakedmodel = renderItem.getItemModelWithOverrides(is, player.getEntityWorld(), player);

        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        preTransform(renderItem, is);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.pushMatrix();
        ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.NONE, false);

        renderItem.renderItem(is, ibakedmodel);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();

        GlStateManager.disableBlend();

        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private static void preTransform(RenderItem renderItem, ItemStack stack) {
        IBakedModel ibakedmodel = renderItem.getItemModelMesher().getItemModel(stack);
        Item item = stack.getItem();

        if (item != null) {
            boolean flag = ibakedmodel.isGui3d();

            if (!flag) {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

}
