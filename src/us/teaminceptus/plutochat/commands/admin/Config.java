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
				suggestions.addAll(config.getKeys(false));
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
		
		return true;
	}

}
