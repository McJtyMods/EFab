package mcjty.commands;

import mcjty.efab.proxy.CommonProxy;
import mcjty.efab.recipes.StandardRecipes;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class CmdSaveDefaults extends CommandBase {
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
        ITextComponent component = new TextComponentString("Saved default recipes to 'efab_recipes.json'");
        if (sender instanceof EntityPlayer) {
            ((EntityPlayer) sender).sendStatusMessage(component, false);
        } else {
            sender.sendMessage(component);
        }
    }
}
