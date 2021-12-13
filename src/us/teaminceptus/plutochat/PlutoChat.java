package us.teaminceptus.plutochat;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import us.teaminceptus.plutochat.commands.Help;
import us.teaminceptus.plutochat.commands.admin.Mute;
import us.teaminceptus.plutochat.listeners.PlayerListener;

public class PlutoChat extends JavaPlugin {
	
	private static File playersFile = new File(JavaPlugin.getPlugin(PlutoChat.class).getDataFolder(), "players.yml");
	private static FileConfiguration playersConfig = YamlConfiguration.loadConfiguration(playersFile);
	
	public void onEnable() {
		saveDefaultConfig();
		saveConfig();
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
	
	
}