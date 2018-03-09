package mcjty.efab.blocks.monitor;

import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class MonitorTE extends AbstractMonitorTE {

    public void setCraftStatus(String msg, String crafterMsg, String crafter2Msg) {
        getMessages();
        messages.set(1, msg);
        messages.set(3, crafterMsg);
        messages.set(4, crafter2Msg);
    }

    @Override
    protected void getDefaultMessages(List<String> messages) {
        messages.add(TextFormatting.DARK_GREEN + "Status:");
        messages.add("");
        messages.add(TextFormatting.DARK_GREEN + "Auto:");
        messages.add("");
        messages.add("");
    }


}
