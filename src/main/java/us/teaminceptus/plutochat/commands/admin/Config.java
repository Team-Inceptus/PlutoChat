package us.teaminceptus.plutochat.commands.admin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import revxrsal.commands.annotation.*;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.command.ArgumentStack;
import us.teaminceptus.plutochat.PlutoChat;
import us.teaminceptus.plutochat.language.Language;

@Command({"pconfig", "pconfiguration", "plutoconfig", "plutoconfiguration", "plutoc"})
@Description("Get, Set, and Reload PlutoChat Configuration values")
@Usage("/pconfig <get|set|reload> [args...]")
@CommandPermission("plutochat.admin.config")
public final class Config {

	protected final PlutoChat plugin;
	
	public Config(PlutoChat plugin){
		this.plugin = plugin;
		plugin.getHandler().getAutoCompleter().registerSuggestion("config", plugin.getConfig().getKeys(false));
		plugin.getHandler().getAutoCompleter().registerSuggestion("configvalue", (args, sender, cmd) -> {
			String key = args.get(0);
			FileConfiguration config = plugin.getConfig();
			Set<String> suggestions = new HashSet<>();

			if (config.isBoolean(key)) suggestions.addAll(Arrays.asList("true", "false"));
			if (key.equals("Language")) {
				for (Language l : Language.values()) suggestions.add(l.getIdentifier());
				suggestions.add("en");
			}

			return suggestions;
		});
		plugin.getHandler().register(this);
	}

	@Subcommand("get")
	@AutoComplete("@config")
	public void getConfig(CommandSender sender, String key) {
		FileConfiguration config = plugin.getConfig();
		Set<String> keys = config.getKeys(false).stream().filter(k -> !config.isConfigurationSection(k)).collect(Collectors.toSet());

		if (!keys.contains(key)) {
			sender.sendMessage(PlutoChat.getMessage("arguments.inexistent"));
			return;
		}

		String chosen = config.get(key).toString();
		sender.sendMessage(String.format(PlutoChat.getMessage("response.config_value"), key, chosen));
	}

	@Subcommand("set")
	@AutoComplete("@config @configvalue")
	public void setConfig(CommandSender sender, String key, String value) {
		FileConfiguration config = plugin.getConfig();
		Set<String> keys = config.getKeys(false).stream().filter(k -> !config.isConfigurationSection(k)).collect(Collectors.toSet());

		if (!(keys.contains(key))) {
			sender.sendMessage(PlutoChat.getMessage("arguments.inexistent"));
			return;
		}

		if (config.isBoolean(key) && !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
			sender.sendMessage(PlutoChat.getMessage("arguments.bool"));
			return;
		}

		try {
			if (config.isBoolean(key)) config.set(key, Boolean.parseBoolean(value));
			else if (config.isInt(key)) config.set(key, Integer.parseInt(value));
			else if (config.isDouble(key)) config.set(key, Double.parseDouble(value));
			else config.set(key, value);

			sender.sendMessage(String.format(PlutoChat.getMessage("response.success.set"), key, ChatColor.translateAlternateColorCodes('&', value)));
			plugin.saveConfig();
			plugin.reloadConfig();
		} catch (NumberFormatException e) {
			if (config.isInt(key)) sender.sendMessage(PlutoChat.getMessage("arguments.integer"));
			else sender.sendMessage(PlutoChat.getMessage("arguments"));
			return;
		}

		PlutoChat.checkConfigs();

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (config.getBoolean("TopTabEnabled")) p.setPlayerListHeader("\n" + ChatColor.translateAlternateColorCodes('&', config.getString("TopTab")) + "\n");
			else p.setPlayerListHeader("");

			if (config.getBoolean("BottomTabEnabled")) p.setPlayerListFooter("\n" + ChatColor.translateAlternateColorCodes('&', config.getString("BottomTab")) + "\n");
			else p.setPlayerListFooter("");
		}
	}

	@Subcommand("reload")
	public void reloadConfig(CommandSender sender) {
		plugin.reloadConfig();
		PlutoChat.checkConfigs();

		for (Language l : Language.values()) {
			String name =  "plutochat" + (l.getIdentifier().length() > 0 ? "_" + l.getIdentifier() : "") + ".properties";
			File f = new File(plugin.getDataFolder(), name);
			if (!f.exists()) plugin.saveResource(name, false);
		}

		sender.sendMessage(PlutoChat.getMessage("response.success.reload"));
	}


}
