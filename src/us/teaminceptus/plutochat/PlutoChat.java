package us.teaminceptus.plutochat;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import us.teaminceptus.plutochat.commands.Help;
import us.teaminceptus.plutochat.commands.admin.Config;
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

	public static enum PlutoError {
		ARGS("Please provide valid arguments."),
		NO_PERMS("You do not have permission to use this command."),
		ARGS_BOOLEAN("Please provide true or false."),
		ARGS_INT("Please provide a valid integer."),
		ARGS_INVALID("This value does not exist."),
		ARGS_VALUE("Please provide a valid value."),
		ARGS_PLAYER("Please provide a valid player."),
		;
		private final String message;
		
		private PlutoError(String message) {
			this.message = message;
		}

		public final String getMessage() {
			return this.message;
		}
	}
	
	public static void sendError(CommandSender sender, PlutoError err) {
		sendError(sender, err.getMessage());
	}
	
	public void onEnable() {
		playersFile = new File(this.getDataFolder(), "players.yml");
		playersConfig = YamlConfiguration.loadConfiguration(playersFile);
		// Commands & Listeners
		new Help(this);
		new us.teaminceptus.plutochat.commands.ChatColor(this);
		
		new Mute(this);
		new Config(this);
		
		new PlayerListener(this);
		checkConfigs();
		saveConfig();
	}
	
	public static void checkConfigs() {
		PlutoChat plugin = JavaPlugin.getPlugin(PlutoChat.class);

		// Config
		FileConfiguration config = plugin.getConfig();
		
		if (!(config.isBoolean("TopTabEnabled"))) {
			config.set("TopTabEnabled", true);
		}
		
		if (!(config.isBoolean("BottomTabEnabled"))) {
			config.set("BottomTabEnabled", true);
		}
		
		if (!(config.isString("TopTab"))) {
			config.set("TopTab", "  Welcome to my server!  ");
		}
		
		if (!(config.isString("BottomTab"))) {
			config.set("BottomTab", "Tab by PlutoChat");
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
			
			// Setting
			if (p.isOnline()) {
				Player op = p.getPlayer();
				
				if (config.getBoolean("TopTabEnabled")) {
					op.setPlayerListHeader("\n" + config.getString("TopTab") + "\n");
				}
				
				if (config.getBoolean("BottomTabEnabled")) {
					op.setPlayerListFooter("\n" + config.getString("BottomTab") + "\n");
				}
				
				op.setDisplayName(ChatColor.translateAlternateColorCodes('&', status.getString("displayname")));
				op.setPlayerListName(ChatColor.translateAlternateColorCodes('&', status.getString("tabname")));
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
		return getPlayersConfig().getConfigurationSection(p.getUniqueId().toString()).getConfigurationSection("information");
	}
	
	
}
