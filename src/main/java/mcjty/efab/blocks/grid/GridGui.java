package mcjty.efab.blocks.grid;

import mcjty.efab.EFab;
import mcjty.efab.network.EFabMessages;
import mcjty.efab.network.PacketGetGridStatus;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

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
        EFabMessages.INSTANCE.sendToServer(new PacketGetGridStatus(tileEntity.getPos()));
        String errorState = tileEntity.getErrorState();
        if (!errorState.isEmpty()) {
            craftButton.setText("ERROR");
            craftButton.setTooltips(errorState);
            craftButton.setEnabled(false);
        } else {
            int ticks = tileEntity.getTicksRemaining();
            if (ticks < 0) {
                craftButton.setText("Start");
                craftButton.setTooltips("Start craft operation");
                craftButton.setEnabled(true);
            } else {
                craftButton.setTooltips("Craft operation in progress");
                craftButton.setEnabled(false);
                int total = tileEntity.getTotalTicks();
                if (total > 0) {
                    craftButton.setText((total-ticks) * 100 / total + "%");
                }
            }
        }
        drawWindow();
    }
}
