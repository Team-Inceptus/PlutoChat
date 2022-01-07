package us.teaminceptus.plutochat;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import us.teaminceptus.plutochat.commands.Help;
import us.teaminceptus.plutochat.commands.admin.Mute;
import us.teaminceptus.plutochat.listeners.PlayerListener;

public class PlutoChat extends JavaPlugin {
	
	private static File playersFile;
	private static FileConfiguration playersConfig;

	public static void sendPluginMessage(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "PlutoChat" + ChatColor.BLUE + "] " + ChatColor.YELLOW + msg);
	}

	public static void sendError(CommandSender sender, String msg) {
		sendPluginMessage(sender, ChatColor.RED + msg);
	}

	public static enum Erorr {
		ARGS("Please provide valid arguments."),
		NO_PERMS("You do not have permission to use this command."),
		ARGS_BOOLEAN("Please provide true or false."),
		ARGS_INT("Please provide a valid integer."),
		ARGS_INVALID("This value does not exist."),
		ARGS_VALUE("Please provide a valid value."),
		ARGS_PLAYER("Please provide a valid player."),
		;
		private final String message;
		
		private Error(String message) {
			this.message = message;
		}

		public final String getMessage() {
			return this.message;
		}
	}
	
	public static void sendError(CommandSender sender, Error err) {
		sendError(sender, err.getMessage());
	}
	
	public void onEnable() {
		playersFile = new File(this.getDataFolder(), "players.yml");
		playersConfig = YamlConfiguration.loadConfiguration(playersFile);
		// Commands & Listeners
		new Help(this);
		new Mute(this);
		new PlayerListener(this);
		checkConfigs();
		saveConfig();
	}
	
	public static void checkConfigs() {
		PlutoChat plugin = JavaPlugin.getPlugin(PlutoChat.class);

		// Config
		FileConfiguration config = plugin.getConfig();
		
		if (!(config.isString("TopList"))) {
			config.set("TopTab", "Welcome to my server!");
		}
		
		if (!(config.isString("BottomList"))) {
			config.set("BottomTab", "");
		}
		
		if (!(config.isBoolean("ColorChat"))) {
			config.set("ColorChat", true);
		}

		if (!(config.isString("ChatPrefix"))) {
			config.set("ChatPrefix", "<");
		}

		if (!(config.isString("ChatSuffix"))) {
			config.set("ChatSuffix", ">");
		}
		
		plugin.saveConfig();
		// Players Config
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if (!(playersConfig.isConfigurationSection(p.getUniqueId().toString()))) {
				playersConfig.createSection(p.getUniqueId().toString());
			}
			
			ConfigurationSection pInfo = playersConfig.getConfigurationSection(p.getUniqueId().toString());
			
			if (!(pInfo.isString("name"))) {
				pInfo.set("name", p.getName());
			}
			
			if (!(pInfo.isBoolean("op"))) {
				pInfo.set("op", p.isOp());
			}
			
			if (!(pInfo.isConfigurationSection("information"))) {
				pInfo.createSection("information");
			}
			
			ConfigurationSection status = pInfo.getConfigurationSection("information");
			
			if (!(status.isString("displayname"))) {
				status.set("displayname", p.getName());
			}
			
			if (!(status.isString("tabname"))) {
				status.set("tabname", p.getName());
			}
			
			if (!(status.isBoolean("muted"))) {
				status.set("muted", false);
			}

			if (!(status.isString("chatcolor"))) {
				status.set("chatcolor", "WHITE");
			}

			if (!(status.isString("chatformat"))) {
				status.set("chatformat", "REGULAR");
			}
		}
		
		try {
			playersConfig.save(playersFile);
		} catch (IOException e) {
			plugin.getLogger().info("Error checking configuration");
			e.printStackTrace();
		}
	}
	
	public static File getPlayersFile() {
		return playersFile;
	}
	
	public static FileConfiguration getPlayersConfig() {
		return playersConfig;
	}
	
	public static ConfigurationSection getInfo(OfflinePlayer p) {
		return getPlayersConfig().getConfigurationSection(p.getUniqueId().toString());
	}
	
	
}
