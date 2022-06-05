package us.teaminceptus.plutochat.listeners;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import us.teaminceptus.plutochat.PlutoChat;
import us.teaminceptus.plutochat.utils.PlutoUtils;

public class PlayerListener implements Listener {
	
	protected final PlutoChat plugin;
	
	public PlayerListener(PlutoChat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		FileConfiguration config = plugin.getConfig();
		FileConfiguration pConfig = PlutoChat.getInfo(p);
		ConfigurationSection info = pConfig.getConfigurationSection("information");
		
		if (config.getBoolean("TopTabEnabled")) p.setPlayerListHeader("\n" + ChatColor.translateAlternateColorCodes('&', config.getString("TopTab")) + "\n");
		else p.setPlayerListHeader("");
		
		if (config.getBoolean("BottomTabEnabled")) p.setPlayerListFooter("\n" + ChatColor.translateAlternateColorCodes('&', config.getString("BottomTab")) + "\n");
		else p.setPlayerListFooter("");
		
		p.setDisplayName(ChatColor.translateAlternateColorCodes('&', info.getString("displayname")) + ChatColor.RESET);
		p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', info.getString("tabname")) + ChatColor.RESET);

		e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', String.format(config.getString("JoinMessage"), p.getDisplayName() == null || p.getDisplayName().length() == 0 ? p.getName() : p.getDisplayName() )));
		try {
			pConfig.save(PlutoChat.getFile(p));
		} catch (IOException err) {
			plugin.getLogger().info("Error Saving Configuration");
			err.printStackTrace();
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) return;
		Player p = e.getPlayer();
		FileConfiguration config = plugin.getConfig();

		if (PlutoUtils.isMuted(p)) {
			e.setCancelled(true);
			p.sendMessage(PlutoChat.getConstant("response.muted"));
		}

		String prefix = config.getString("ChatPrefix", "<");
		String suffix = config.getString("ChatSuffix", ">");

		String format = PlutoUtils.getChatFormat(p) == null ? "" : PlutoUtils.getChatFormat(p) + "";
		String color = PlutoUtils.getChatColor(p) == null ? "" : PlutoUtils.getChatColor(p) + "";

		e.setFormat(prefix + "%s" + suffix + " " + (config.getBoolean("ColorChat") ? color + format + "%s" : "%s"));
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		FileConfiguration config = plugin.getConfig();

		e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', String.format(config.getString("JoinMessage"), p.getDisplayName() == null || p.getDisplayName().length() == 0 ? p.getName() : p.getDisplayName() )));
	}

}
