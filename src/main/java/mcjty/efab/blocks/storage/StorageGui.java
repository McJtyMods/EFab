package mcjty.efab.blocks.storage;

import mcjty.efab.EFab;
import mcjty.efab.network.EFabMessages;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class StorageGui extends GenericGuiContainer<StorageTE> {

    public static final int WIDTH = 171;
    public static final int HEIGHT = 176;

    private static final ResourceLocation mainBackground = new ResourceLocation(EFab.MODID, "textures/gui/storage.png");

    public StorageGui(StorageTE controller, StorageContainer container) {
        super(EFab.instance, EFabMessages.INSTANCE, controller, container, 0, "storage");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setBackground(mainBackground);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        TextField nameField = new TextField(mc, this)
                .setName("name")
                .setTooltips("If you give this storage a name", "it can only be used by crafters", "with the same name")
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 76, 161, 16));

        toplevel.addChild(nameField);

        window = new Window(this, toplevel);

        window.bind(EFabMessages.INSTANCE, "name", tileEntity, StorageTE.VALUE_CRAFTING_NAME.getName());
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x1, int x2) {
        drawWindow();
    }
}
