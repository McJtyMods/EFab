package mcjty.efab.blocks.grid;

import mcjty.efab.EFab;
import mcjty.efab.network.EFabMessages;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;

public class GridGui extends GenericGuiContainer<GridTE> {

    public static final int WIDTH = 171;
    public static final int HEIGHT = 176;

    private Button craftButton;

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

        craftButton = new Button(mc, this)
                .setText("Start")
                .setLayoutHint(new PositionalLayout.PositionalHint(84, 30, 40, 16))
                .addButtonEvent(parent -> {
                    craft();
        });

        toplevel.addChild(craftButton);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private void craft() {
        sendServerCommand(EFabMessages.INSTANCE, GridTE.CMD_CRAFT);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x1, int x2) {
        tileEntity.requestProgressFromServer();
        int ticks = tileEntity.getTicksRemaining();
        if (ticks == 0) {
            craftButton.setText("Start");
        } else {
            switch ((ticks / 5) % 5) {
                case 0: craftButton.setText("."); break;
                case 1: craftButton.setText(".."); break;
                case 2: craftButton.setText("..."); break;
                case 3: craftButton.setText("...."); break;
                case 4: craftButton.setText("....."); break;
            }
        }
        drawWindow();
    }
}
