package us.teaminceptus.plutochat.commands.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import us.teaminceptus.plutochat.PlutoChat;
import us.teaminceptus.plutochat.PlutoChat.PlutoError;

public class Config implements TabExecutor {

	protected PlutoChat plugin;
	
	public Config(PlutoChat plugin){
		this.plugin = plugin;
		plugin.getCommand("plutoconfiguration").setExecutor(this);
		plugin.getCommand("plutoconfiguration").setTabCompleter(this);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> suggestions = new ArrayList<>();
		
		FileConfiguration config = plugin.getConfig();
		
		switch (args.length) {
			case 1: {
				suggestions.addAll(Arrays.asList("get", "set", "reload"));
				break;
			}
			case 2: {
				if (!(args[0].equalsIgnoreCase("reload")))
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
			PlutoChat.sendError(sender, PlutoError.ARGS);
			return false;
		}

		FileConfiguration config = plugin.getConfig();

		switch (args[0].toLowerCase()) {
			case "get": {
				if (args.length < 2) {
					PlutoChat.sendError(sender, PlutoError.ARGS);
					return false;
				}
				
				List<String> keys = config.getKeys(false).stream().filter(key -> !(config.isConfigurationSection(key))).toList();

				if (!(keys.contains(args[1]))) {
					PlutoChat.sendError(sender, PlutoError.ARGS_INVALID);
					return false;
				}

				String chosen = config.get(args[1]).toString();
				sender.sendMessage(ChatColor.GREEN + "The value of " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is: \"" + ChatColor.WHITE + chosen + ChatColor.GREEN + "\"");
				break;
			}
			case "set": {
				if (args.length < 2) {
					PlutoChat.sendError(sender, PlutoError.ARGS);
					return false;
				}
				
				List<String> keys = config.getKeys(false).stream().filter(key -> !(config.isConfigurationSection(key))).toList();

				if (!(keys.contains(args[1]))) {
					PlutoChat.sendError(sender, PlutoError.ARGS_INVALID);
					return false;
				}

				if (args.length < 3) {
					PlutoChat.sendError(sender, PlutoError.ARGS_VALUE);
					return false;
				}

				if (config.isBoolean(args[1]) && !(args[2].equalsIgnoreCase("true")) && !(args[2].equalsIgnoreCase("false"))) {
					PlutoChat.sendError(sender, PlutoError.ARGS_BOOLEAN);
					return false;
				}
				
				List<String> valueArgs = new ArrayList<>();
				for (int i = 2; i < args.length; i++) {
					valueArgs.add(args[i]);
				}
				
				String value = String.join(" ", valueArgs);
				
				try {
					if (config.isBoolean(args[1])) config.set(args[1], Boolean.parseBoolean(value));
					else if (config.isInt(args[1])) config.set(args[1], Integer.parseInt(value));
					else if (config.isDouble(args[1])) config.set(args[1], Double.parseDouble(value));
					else config.set(args[1], value);
					
					sender.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to \"" + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', value) + ChatColor.GREEN + "\"");
					try {
						PlutoChat.getPlayersConfig().save(PlutoChat.getPlayersFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (NumberFormatException e) {
					if (config.isInt(args[1])) {
						PlutoChat.sendError(sender, PlutoError.ARGS_INT);
					} else {
						PlutoChat.sendError(sender, PlutoError.ARGS);
					}
					return false;
				}
				
				PlutoChat.checkConfigs();
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (config.getBoolean("TopTabEnabled")) {
						p.setPlayerListHeader("\n" + ChatColor.translateAlternateColorCodes('&', config.getString("TopTab")) + "\n");
					} else p.setPlayerListHeader("");
					
					if (config.getBoolean("BottomTabEnabled")) {
						p.setPlayerListFooter("\n" + ChatColor.translateAlternateColorCodes('&', config.getString("BottomTab")) + "\n");
					} else p.setPlayerListFooter("");
				}
				
				break;
			}
			case "reload": {
				plugin.reloadConfig();
				plugin.saveConfig();
				PlutoChat.checkConfigs();
				try {
					PlutoChat.getPlayersConfig().save(PlutoChat.getPlayersFile());
				} catch (IOException e) {
					plugin.getLogger().info("Error reloading config");
					e.printStackTrace();
				}
				
				sender.sendMessage(ChatColor.GREEN + "Successfully Reloaded Configuration!");
				break;
			}
			default: {
				PlutoChat.sendError(sender, "Please provide valid arguments.");
				return false;
			}
		}
		plugin.saveConfig();
		return true;
	}

}
