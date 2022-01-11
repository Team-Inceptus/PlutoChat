package us.teaminceptus.plutochat.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import us.teaminceptus.plutochat.PlutoChat;
import us.teaminceptus.plutochat.PlutoChat.PlutoError;
import us.teaminceptus.plutochat.utils.PlutoUtils;

public class Mute implements CommandExecutor {

	protected PlutoChat plugin;
	
	public Mute(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getCommand("mute").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			PlutoChat.sendError(sender, PlutoError.ARGS_PLAYER);
			return false;
		}

		if (Bukkit.getOfflinePlayer(PlutoUtils.nameToUUID(args[0])) == null) {
			PlutoChat.sendError(sender, PlutoError.ARGS_PLAYER);
			return false;
		}

		
		
		return true;
	}
}
