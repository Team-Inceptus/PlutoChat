package us.teaminceptus.plutochat.utils;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import us.teaminceptus.plutochat.PlutoChat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlutoUtils {

	public static boolean isMuted(OfflinePlayer p) {
		return PlutoChat.getInfo(p).getBoolean("muted");
	}

	public static void setMuted(OfflinePlayer p, boolean muted) {
		PlutoChat.getInfo(p).set("muted", muted);
		PlutoChat.checkConfigs();
	}

	public static ChatColor getChatColor(OfflinePlayer p) {
		try {
			return ChatColor.valueOf(PlutoChat.getInfo(p).getString("chatcolor").toUpperCase()); 
		} catch (Exception e) {
			return ChatColor.WHITE;
		}	
	}

	public static ChatColor getChatFormat(OfflinePlayer p) {
		try {
			return ChatColor.valueOf(PlutoChat.getInfo(p).getString("chatformat").toUpperCase()); 
		} catch (Exception e) {
			return null;
		}
	}
	
	public static UUID nameToUUID(String name) {
		if (Bukkit.getOnlineMode()) {
			try {
				URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", "Java 8 PlutoChat Bot");

				int responseCode = connection.getResponseCode();

				if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED) {
					BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder builder = new StringBuilder();
					String inputLine;
					while((inputLine = input.readLine()) != null) builder.append(inputLine);

					Gson g = new Gson();
					return untrimUUID(g.fromJson(builder.toString(), APIPlayer.class).id);
				}
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
			}
    	return null;
	}

	public static UUID untrimUUID(String oldUUID) {
		String p1 = oldUUID.substring(0, 8);
		String p2 = oldUUID.substring(8, 12);
		String p3 = oldUUID.substring(12, 16);
		String p4 = oldUUID.substring(16, 20);
		String p5 = oldUUID.substring(20, 32);
		
		String newUUID = p1 + "-" + p2 + "-" + p3 + "-" + p4 + "-" + p5;
		
		return UUID.fromString(newUUID);
	}
}
