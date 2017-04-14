package mcjty.commands;

import mcjty.efab.proxy.CommonProxy;
import mcjty.efab.recipes.StandardRecipes;
import mcjty.lib.compat.CompatCommandBase;
import mcjty.lib.tools.ChatTools;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class CmdSaveDefaults extends CompatCommandBase {
    @Override
    public String getName() {
        return "efab_savedefaults";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "efab_savedefaults";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        File file = new File(CommonProxy.modConfigDir.getPath(), "efab_recipes.json");
        StandardRecipes.writeDefaults(file);
        ChatTools.addChatMessage(sender, new TextComponentString("Saved default recipes to 'efab_recipes.json'"));
    }
}
