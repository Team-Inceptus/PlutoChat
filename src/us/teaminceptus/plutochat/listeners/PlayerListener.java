package us.teaminceptus.plutochat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import us.teaminceptus.plutochat.PlutoChat;

public class PlayerListener implements Listener {
	
	protected PlutoChat plugin;
	
	public PlayerListener(PlutoChat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
	}

}
