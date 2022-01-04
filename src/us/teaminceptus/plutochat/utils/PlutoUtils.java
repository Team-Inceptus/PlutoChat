package us.teaminceptus.plutochat.utils;

import org.bukkit.entity.Player;

import us.teaminceptus.plutochat.PlutoChat;

public class PlutoUtils {
	
	public static boolean isMuted(Player p) {
		return PlutoChat.getInfo(p).getBoolean("muted");
	}
}
