package us.teaminceptus.plutochat.commands.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;

import us.teaminceptus.plutochat.PlutoChat;

public class Config implements TabExecutor {

	protected PlutoChat plugin;
	
	public Config(PlutoChat plugin){
		this.plugin = plugin;
		plugin.getCommand("configuration").setExecutor(this);
		plugin.getCommand("configuration").setTabCompleter(this);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> suggestions = new ArrayList<>();
		
		FileConfiguration config = plugin.getConfig();
		
		switch (args.length) {
			case 1: {
				suggestions.addAll(Arrays.asList("get", "set"));
				break;
			}
			case 2: {
				suggestions.addAll(config.getKeys(false).stream().filter(key -> !(config.isConfigurationSection(key))).toList());
				break;
			}
			case 3: {
				if (args[0].equalsIgnoreCase("set")) {
					if (config.isBoolean(args[1])) {
						suggestions.addAll(Arrays.asList("true", "false"));
					}
				}
				break;
			}
		}
		
		return suggestions;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			PlutoChat.sendError(sender, Error.ARGS);
			return false;
		}

		FileConfiguration config = plugin.getConfig();

		switch (args[0].toLowerCase()) {
			case "get": {
				if (args.length < 2) {
					PlutoChat.sendError(sender, Error.ARGS);
					return false;
				}
				
				Set<String> keys = config.getKeys(false).stream().filter(key -> !(config.isConfigurationSection(key))).toList();

				if (!(keys.contains(args[1]))) {
					PlutoChat.sendError(sender, Error.ARGS_INVALID);
					return false;
				}

				String chosen = config.get(args[1]).toString();
				sender.sendMessage(ChatColor.GREEN + "The value of " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is: \"" + ChatColor.WHITE + chosen + ChatColor.GREEN + "\"");
				break;
			}
			case "set": {
				if (args.length < 2) {
					PlutoChat.sendError(sender, Error.ARGS);
					return false;
				}
				
				Set<String> keys = config.getKeys(false).stream().filter(key -> !(config.isConfigurationSection(key))).toList();

				if (!(keys.contains(args[1]))) {
					PlutoChat.sendError(sender, Error.ARGS_INVALID);
					return false;
				}

				if (args.length < 3) {
					PlutoChat.sendError(sender, Error.ARGS_VALUE);
					return false;
				}

				if (config.isBoolean(args[1]) && !(args[2].equalsIgnoreCase("true")) && !(args[2].equalsIgnoreCase("false"))) {
					PlutoChat.sendError(sender, Error.ARGS_BOOLEAN);
					return false;
				}

				try {
					if (config.isBoolean(args[1])) config.set(args[1], Boolean.parseBoolean(args[2]));
					else if (config.isInt(args[1])) config.set(args[1], Integer.parseInt(args[2]));
					else if (config.isDouble(args[1])) config.set(args[1], Double.parseDouble(args[2]));
					else config.set(args[1], args[2]);
				} catch (NumberFormatException e) {
					if (config.isInt(args[1])) {
						PlutoChat.sendError(sender, Error.ARGS_INT);
					} else {
						PlutoChat.sendError(sender, Error.ARGS);
					}
					return false;
				}
				
				break;
			}
			default: {
				PlutoChat.sendError(sender, "Please provide valid arguments.");
				return false;
			}
		}
		return true;
	}

}
