package us.teaminceptus.plutochat.commands.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import revxrsal.commands.annotation.*;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.plutochat.PlutoChat;

@Command({"pconfig", "pconfiguration", "plutoconfig", "plutoconfiguration", "plutoc"})
@Description("Get, Set, and Reload PlutoChat Configuration values")
@Usage("/pconfig <get|set|reload> [args...]")
@CommandPermission("plutochat.admin.config")
public final class Config {

	protected final PlutoChat plugin;
	
	public Config(PlutoChat plugin){
		this.plugin = plugin;
		plugin.getHandler().register(this);

		plugin.getHandler().registerValueResolver(SetType.class, ctx -> SetType.valueOf(ctx.popForParameter().toUpperCase()));
		plugin.getHandler().getAutoCompleter().registerParameterSuggestions(SetType.class, SuggestionProvider.of(SetType.ARGS));
		
		plugin.getHandler().getAutoCompleter().registerSuggestion("config", plugin.getConfig().getKeys(false));
	}

	public enum SetType {
		GET,
		SET;

		public static final String[] ARGS = new String[] { "get", "set" };
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
		sender.sendMessage(String.format(PlutoChat.getMessage("response.config_value"), chosen));
	}

	@Subcommand("set")
	@AutoComplete("@config *")
	public void setConfig(CommandSender sender, String key, String... valueArr) {
		FileConfiguration config = plugin.getConfig();
		Set<String> keys = config.getKeys(false).stream().filter(k -> !config.isConfigurationSection(k)).collect(Collectors.toSet());

		if (!(keys.contains(key))) {
			sender.sendMessage(PlutoChat.getMessage("arguments.inexistent"));
			return;
		}

		String value = String.join(" ", valueArr);

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
		sender.sendMessage(PlutoChat.getMessage("response.success.reload"));
	}


}
