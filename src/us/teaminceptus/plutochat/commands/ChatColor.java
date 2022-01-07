package us.teaminceptus.plutochat.commands;

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

		if (args.length < 2) {
			try {
				org.bukkit.ChatColor c = org.bukkit.ChatColor.valueOf(args[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				PlutoChat.sendError(p, "Please provide a valid color code.");
				return false;
			}
		} else {
			
		}
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