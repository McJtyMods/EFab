package mcjty.efab.blocks.grid;

import mcjty.efab.EFab;
import mcjty.efab.blocks.network.EFabMessages;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GridGui extends GenericGuiContainer<GridTE> {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    private static final ResourceLocation mainBackground = new ResourceLocation(EFab.MODID, "textures/gui/grid.png");

    public GridGui(GridTE controller, GridContainer container) {
        super(EFab.instance, EFabMessages.INSTANCE, controller, container, 0, "grid");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setBackground(mainBackground);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x1, int x2) {
        drawWindow();
    }
}
