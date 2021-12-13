package us.teaminceptus.plutochat;

import org.bukkit.plugin.java.JavaPlugin;

import us.teaminceptus.plutochat.commands.Help;
import us.teaminceptus.plutochat.commands.admin.Mute;
import us.teaminceptus.plutochat.listeners.PlayerListener;

public class PlutoChat extends JavaPlugin {

	public void onEnable() {
		saveDefaultConfig();
		saveConfig();
		// Commands & Listeners
		new Help(this);
		new Mute(this);
		new PlayerListener(this);
		// Config
		
		
		saveConfig();
	}
	
}
