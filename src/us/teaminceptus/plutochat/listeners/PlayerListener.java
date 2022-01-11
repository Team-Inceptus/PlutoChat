package us.teaminceptus.plutochat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import us.teaminceptus.plutochat.PlutoChat;
import us.teaminceptus.plutochat.utils.PlutoUtils;

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
		
		if (config.getBoolean("TopTabEnabled")) {
			p.setPlayerListHeader("\n" + config.getString("TopTab") + "\n");
		}
		
		if (config.getBoolean("BottomTabEnabled")) {
			p.setPlayerListFooter("\n" + config.getString("BottomTab") + "\n");
		}
		
		ConfigurationSection info = PlutoChat.getInfo(p).getConfigurationSection("information");
		
		p.setDisplayName(ChatColor.translateAlternateColorCodes('&', info.getString("displayname")));
		p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', info.getString("tabname")));
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) return;
		Player p = e.getPlayer();
		FileConfiguration config = plugin.getConfig();

		if (PlutoUtils.isMuted(p)) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You are currently muted.");
		}

		String prefix = config.getString("ChatPrefix");
		String suffix = config.getString("ChatSuffix");

		e.setFormat(prefix + "%s" + suffix + " " + (config.getBoolean("ColorChat") ? PlutoUtils.getChatColor(p) + (PlutoUtils.getChatFormat(p) == null ? "" : PlutoUtils.getChatFormat(p) + "") + "%s" : "%s"));
	}

}
