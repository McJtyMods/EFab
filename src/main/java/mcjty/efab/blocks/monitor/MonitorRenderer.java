package mcjty.efab.blocks.monitor;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class MonitorRenderer extends TileEntitySpecialRenderer<MonitorTE> {

    @Override
    public void render(MonitorTE te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        if (te.getWorld().isAirBlock(te.getPos())) {
            return;
        }
        MonitorRenderHelper.renderHud(te, x, y, z, 0.5f, false);
    }

    public static void register() {
        ClientRegistry.bindTileEntitySpecialRenderer(MonitorTE.class, new MonitorRenderer());
    }
}
