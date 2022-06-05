package us.teaminceptus.plutochat.commands;

import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.plutochat.PlutoChat;

public final class Help {

	public Help(PlutoChat plugin) {
		plugin.getHandler().register(this);
	}

	@Command({"phelp", "plutohelp", "plh"})
	@Description("Displays PlutoChat Help.")
	@Usage("/phelp")
	@CommandPermission("plutochat.help")
	public void help(CommandSender sender) {
 		sender.sendMessage(PlutoChat.getConstant("text.help"));
	}

}
