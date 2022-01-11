package us.teaminceptus.plutochat.commands.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import us.teaminceptus.plutochat.PlutoChat;
import us.teaminceptus.plutochat.PlutoChat.PlutoError;
import us.teaminceptus.plutochat.utils.PlutoUtils;

public class SetName implements TabExecutor {
	
	protected PlutoChat plugin;
	
	public SetName(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getCommand("setname").setExecutor(this);
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
		
		OfflinePlayer target = Bukkit.getOfflinePlayer(PlutoUtils.nameToUUID(args[0]));
		
		if (args.length < 2) {
			PlutoChat.sendError(sender, "Please provide tab or display.");
			return false;
		}
		
		
		if (!(args[1].equalsIgnoreCase("tab")) && !(args[1].equalsIgnoreCase("display"))) {
			PlutoChat.sendError(sender, "Please provide tab or display.");
			return false;
		}
		
		if (args.length < 3) {
			PlutoChat.sendError(sender, "Please provide a name.");
			return false;
		}
		
		
		List<String> nameArgs = new ArrayList<String>();
		for (int i = 2; i < args.length; i++) {
			nameArgs.add(args[i]);
		}
		String name = String.join(" ", nameArgs);
		
		PlutoChat.getInfo(target).set(args[1].toLowerCase() + "name", name);
		if (target.isOnline()) {
			Player ot = target.getPlayer();
			if (args[1].equalsIgnoreCase("tab")) ot.setPlayerListName(ChatColor.translateAlternateColorCodes('&', name));
			else if (args[1].equalsIgnoreCase("display")) ot.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		}
		
		sender.sendMessage(ChatColor.GREEN + "Succesfully set " + ChatColor.GOLD + args[1].toLowerCase() + "name" + ChatColor.GREEN + " to: \"" + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', name) + ChatColor.GREEN +  "\"");
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> suggestions = new ArrayList<>();
		
		switch (args.length) {
			case 1: {
				for (Player p : Bukkit.getOnlinePlayers()) suggestions.add(p.getName());
				return suggestions;
			}
			case 2: {
				suggestions.addAll(Arrays.asList("tab", "display"));
				return suggestions;
			}
		}
		
		return suggestions;
	}

}
