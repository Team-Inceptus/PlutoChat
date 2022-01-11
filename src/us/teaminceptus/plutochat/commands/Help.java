package us.teaminceptus.plutochat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import us.teaminceptus.plutochat.PlutoChat;

public class Help implements CommandExecutor {

	protected PlutoChat plugin;

	public Help(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getCommand("plutohelp").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 		
		return true;
	}

}
