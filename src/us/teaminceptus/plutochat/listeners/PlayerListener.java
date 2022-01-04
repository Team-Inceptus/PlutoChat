package us.teaminceptus.plutochat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;
import us.teaminceptus.plutochat.PlutoChat;

public class PlayerListener implements Listener {
	
	protected PlutoChat plugin;
	
	public PlayerListener(PlutoChat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		FileConfiguration config = plugin.getConfig();
		
		p.setPlayerListHeader(config.getString("TopList"));
		p.setPlayerListFooter(config.getString("BottomList"));
		
		ConfigurationSection info = PlutoChat.getInfo(p).getConfigurationSection("information");
		
		p.setDisplayName(ChatColor.translateAlternateColorCodes('&', info.getString("displayname")));
		p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', info.getString("tabname")));
	}

}
