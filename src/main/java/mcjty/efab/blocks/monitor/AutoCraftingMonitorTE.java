package mcjty.efab.blocks.monitor;

import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class AutoCraftingMonitorTE extends AbstractMonitorTE {

    public void setCraftStatus(List<String> crafterStatus) {
        getMessages();
        for (int i = 1 ; i <= 8 ; i++) {
            if (i <= crafterStatus.size()) {
                messages.set(i, crafterStatus.get(i-1));
            } else {
                messages.set(i, "");
            }
        }
    }


    @Override
    protected void getDefaultMessages(List<String> messages) {
        messages.add(TextFormatting.DARK_GREEN + "Crafters:");
        messages.add("");
        messages.add("");
        messages.add("");
        messages.add("");
        messages.add("");
        messages.add("");
        messages.add("");
        messages.add("");
    }

}
