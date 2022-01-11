package us.teaminceptus.plutochat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import us.teaminceptus.plutochat.PlutoChat;

public class ChatColor implements TabExecutor {

	protected PlutoChat plugin;

	public ChatColor(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getCommand("chatcolor").setExecutor(this);
		plugin.getCommand("chatcolor").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player p)) return false;

		if (args.length < 1) {
			PlutoChat.sendError(p, "Please provide a valid color code.");
			return false;
		}
		
		try {
			org.bukkit.ChatColor c = org.bukkit.ChatColor.valueOf(args[0].toUpperCase());

			if (!(c.isColor())) {
				PlutoChat.sendError(p, "Please provide a valid color code.");
				return false;
			}
			
			PlutoChat.getInfo(p).set("chatcolor", c.name());
			p.sendMessage(org.bukkit.ChatColor.GREEN + "Successfully set ChatColor to " + c + c.name().toLowerCase() + org.bukkit.ChatColor.GREEN + ".");
		} catch (IllegalArgumentException e) {
			PlutoChat.sendError(p, "Please provide a valid color code.");
			return false;
		}

		if (!(args.length < 2)) {
			try {
				if (args[1].equalsIgnoreCase("regular")) {
					PlutoChat.getInfo(p).set("chatformat", null);
					p.sendMessage(org.bukkit.ChatColor.GREEN + "Successfully reset ChatFormat.");
					return true;
				}
				org.bukkit.ChatColor c = org.bukkit.ChatColor.valueOf(args[1].toUpperCase());

				if (!(c.isFormat())) {
					PlutoChat.sendError(p, "Please provide a valid format code.");
					return false;
				}
				
				PlutoChat.getInfo(p).set("chatformat", c.name());
				p.sendMessage(org.bukkit.ChatColor.GREEN + "Successfully set ChatFormat to " + c + c.name().toLowerCase() + org.bukkit.ChatColor.GREEN + ".");
			} catch (IllegalArgumentException e) {
				PlutoChat.sendError(p, "Please provide a valid format code.");
				return false;
			}
		}
		PlutoChat.checkConfigs();
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player p)) return null;

		List<String> suggestions = new ArrayList<>();
		
		switch (args.length) {
			case 1: {
				for (org.bukkit.ChatColor c : org.bukkit.ChatColor.values()) {
					if (c.isColor()) {
						suggestions.add(c.name().toLowerCase());
					}
				}
				return suggestions;
			}
			case 2: {
				for (org.bukkit.ChatColor c : org.bukkit.ChatColor.values()) {
					if (c.isFormat()) {
						suggestions.add(c.name().toLowerCase());
					}
				}
				suggestions.add("regular");
				return suggestions;
			}
			default: {
				return suggestions;
			}
		}
	}
}