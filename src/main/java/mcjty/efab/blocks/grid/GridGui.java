package mcjty.efab.blocks.grid;

import mcjty.efab.EFab;
import mcjty.efab.network.EFabMessages;
import mcjty.efab.network.PacketGetGridStatus;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GridGui extends GenericGuiContainer<GridTE> {

    public static final int WIDTH = 171;
    public static final int HEIGHT = 176;

    private Button craftButton;
    private Button leftArrow;
    private Button rightArrow;
    private Label timeLeftLabel;

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

        leftArrow = new Button(mc, this)
                .setText("<")
                .setLayoutHint(new PositionalLayout.PositionalHint(82, 45, 13, 18))
                .setVisible(false)
                .addButtonEvent(parent -> left());
        rightArrow = new Button(mc, this)
                .setText(">")
                .setLayoutHint(new PositionalLayout.PositionalHint(112, 45, 13, 18))
                .setVisible(false)
                .addButtonEvent(parent -> right());
        timeLeftLabel = new Label<>(mc, this)
                .setText("")
                .setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT)
                .setLayoutHint(new PositionalLayout.PositionalHint(88, 11, 40, 14));
        craftButton = new Button(mc, this)
                .setText("Start")
                .setLayoutHint(new PositionalLayout.PositionalHint(84, 24, 40, 16))
                .addButtonEvent(parent -> craft());

        toplevel.addChild(craftButton).addChild(leftArrow).addChild(rightArrow).addChild(timeLeftLabel);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private void craft() {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            sendServerCommand(EFabMessages.INSTANCE, GridTE.CMD_CRAFT_REPEAT);
        } else {
            sendServerCommand(EFabMessages.INSTANCE, GridTE.CMD_CRAFT);
        }
    }

    private void left() {
        sendServerCommand(EFabMessages.INSTANCE, GridTE.CMD_LEFT);
    }

    private void right() {
        sendServerCommand(EFabMessages.INSTANCE, GridTE.CMD_RIGHT);
    }

    private static DecimalFormat fmt = new DecimalFormat("#.##");
    private static DecimalFormat shortFmt = new DecimalFormat("#.0");

    public static String getTime(int ticks, boolean shortForm) {
        float seconds = ticks / 20.0f;
        DecimalFormat format = shortForm ? shortFmt : GridGui.fmt;
        if (seconds >= 60) {
            float minutes = ticks / 20.0f / 60.0f;
            if (minutes >= 60) {
                float hours = ticks / 20.0f / 3600.0f;
                if (Math.abs(hours-1.0f) < 0.01) {
                    return shortForm ? "1h" : "1 hour";
                } else {
                    return format.format(hours) + (shortForm ? "h" : " hours");
                }
            } else {
                if (Math.abs(minutes-1.0f) < 0.01) {
                    return shortForm ? "1m" : "1 minute";
                } else {
                    return format.format(minutes) + (shortForm ? "m" : " minutes");
                }
            }
        } else {
            if (Math.abs(seconds-1.0f) < 0.01) {
                return shortForm ? "1s" : "1 second";
            } else {
                return format.format(seconds) + (shortForm ? "s" : " seconds");
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x1, int x2) {
        EFabMessages.INSTANCE.sendToServer(new PacketGetGridStatus(tileEntity.getPos()));
        List<String> errorState = tileEntity.getErrorState();
        if (!errorState.isEmpty()) {
            timeLeftLabel.setText("");
            craftButton.setText("ERROR");
            craftButton.setTooltips(errorState.toArray(new String[errorState.size()]));
            craftButton.setEnabled(false);
        } else {
            int ticks = tileEntity.getTicksRemaining();
            if (ticks < 0) {
                timeLeftLabel.setText("");
                craftButton.setText("Start");
                List<String> tooltip = new ArrayList<>();
                tooltip.add("Start craft operation");
                tooltip.add("Duration " + getTime(tileEntity.getTotalTicks(), false));
                tooltip.add(TextFormatting.GRAY + "Shift-click to autorepeat");

                List<String> usage = tileEntity.getUsage();
                if (!usage.isEmpty()) {
                    tooltip.add("");
                    tooltip.addAll(usage);
                }

                craftButton.setTooltips(tooltip.toArray(new String[tooltip.size()]));
                craftButton.setEnabled(true);
            } else {
                timeLeftLabel.setText(getTime(ticks, true));
                craftButton.setTooltips("Craft operation in progress");
                craftButton.setEnabled(false);
                int total = tileEntity.getTotalTicks();
                if (total > 0) {
                    craftButton.setText((total-ticks) * 100 / total + "%");
                }
            }
        }

        List<ItemStack> outputs = tileEntity.getOutputs();
        leftArrow.setVisible(outputs.size() > 1);
        rightArrow.setVisible(outputs.size() > 1);

        drawWindow();
    }
}
