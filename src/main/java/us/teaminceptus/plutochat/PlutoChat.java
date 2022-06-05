package us.teaminceptus.plutochat;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.exception.CommandErrorException;
import us.teaminceptus.plutochat.commands.ChatColorCommand;
import us.teaminceptus.plutochat.commands.Help;
import us.teaminceptus.plutochat.commands.admin.Config;
import us.teaminceptus.plutochat.commands.admin.Mute;
import us.teaminceptus.plutochat.commands.admin.SetName;
import us.teaminceptus.plutochat.language.Language;
import us.teaminceptus.plutochat.listeners.PlayerListener;
import us.teaminceptus.plutochat.utils.PlutoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlutoChat extends JavaPlugin {
	
	private static File playerDir;

	private BukkitCommandHandler handler;

	public static PlutoChat getPlugin() {
		return JavaPlugin.getPlugin(PlutoChat.class);
	}

	public static String getConstant(String key) {
		String lang = getPlugin().getConfig().getString("Language", "en");
		Language l = Language.getById(lang);
		return l.getMessage(key);
	}

	public static String getMessage(String key) {
		return getConstant("plugin.prefix") + getConstant(key);
	}

	public BukkitCommandHandler getHandler() {
		return this.handler;
	}

	private void setupLamp() {
		handler.registerValueResolver(OfflinePlayer.class, ctx -> {
			if (PlutoUtils.nameToUUID(ctx.popForParameter()) == null) throw new CommandErrorException(getMessage("arguments.player"));
			return Bukkit.getOfflinePlayer(PlutoUtils.nameToUUID(ctx.popForParameter()));
		});

		List<String> set = new ArrayList<String>() {{
			for (OfflinePlayer p : Bukkit.getOfflinePlayers()) add(p.getName());
		}};

		handler.getAutoCompleter().registerParameterSuggestions(OfflinePlayer.class, SuggestionProvider.of(set));

		new Help(this);
		new ChatColorCommand(this);

		new Mute(this);
		new Config(this);
		new SetName(this);

		handler.registerBrigadier();
	}

	public void onEnable() {
		getLogger().info("Loading Configuration...");
		saveDefaultConfig();
		saveConfig();
		playerDir = new File(this.getDataFolder(), "players");
		if (!playerDir.exists()) playerDir.mkdir();

		for (Language l : Language.values()) {
			String name =  "plutochat" + (l.getIdentifier().length() > 0 ? "_" + l.getIdentifier() : "") + ".properties";
			File f = new File(getDataFolder(), name);
			if (!f.exists()) saveResource(name, false);
		}

		getLogger().info("Loading Classes...");

		handler = BukkitCommandHandler.create(this);
		setupLamp();
		new PlayerListener(this);

		new UpdateChecker(this, UpdateCheckSource.SPIGOT, "99282")
				.setDownloadLink("https://www.spigotmc.org/resources/plutochat.99282/")
				.setNotifyOpsOnJoin(true)
				.setChangelogLink("https://github.com/Team-Inceptus/PlutoChat/releases/")
				.setUserAgent("Java 8 PlutoChat User Agent")
				.checkEveryXHours(1)
				.checkNow();

		new Metrics(this, BSTATS_ID);

		checkConfigs();
		saveConfig();

		getLogger().info("Done!");
	}

	private static final int BSTATS_ID = 15392;
	
	public static void checkConfigs() {
		PlutoChat plugin = PlutoChat.getPlugin();
		try {
			// Config
			FileConfiguration config = plugin.getConfig();

			if (!(config.isString("Language"))) config.set("Language", "en");
			if (!(config.isBoolean("TopTabEnabled"))) config.set("TopTabEnabled", true);
			if (!(config.isBoolean("BottomTabEnabled"))) config.set("BottomTabEnabled", true);
			if (!(config.isString("TopTab"))) config.set("TopTab", "  Welcome to my server!  ");
			if (!(config.isString("BottomTab"))) config.set("BottomTab", "Tab by PlutoChat");

			if (!(config.isBoolean("ColorChat"))) {
				config.set("ColorChat", true);
			}

			if (!(config.isString("ChatPrefix"))) {
				config.set("ChatPrefix", "<");
			}

			if (!(config.isString("ChatSuffix"))) {
				config.set("ChatSuffix", ">");
			}

			if (!(config.isString("JoinMessage"))) {
				config.set("JoinMessage", "&e%player% has joined the game.");
			}

			if (!(config.isString("LeaveMessage"))) {
				config.set("LeaveMessage", "&e%player% has left the game.");
			}

			plugin.saveConfig();
			// Players Config
			for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
				String uid = p.getUniqueId().toString();

				File f = new File(playerDir, uid + ".yml");
				if (!f.exists()) f.createNewFile();

				FileConfiguration pInfo = YamlConfiguration.loadConfiguration(f);
				String name = p.getName();

				if (!(pInfo.isString("name"))) pInfo.set("name", name);
				if (!(pInfo.isBoolean("op"))) pInfo.set("op", p.isOp());
				if (!(pInfo.isConfigurationSection("information"))) pInfo.createSection("information");

				ConfigurationSection status = pInfo.getConfigurationSection("information");

				if (!(status.isString("displayname"))) status.set("displayname", name);
				if (!(status.isString("tabname"))) status.set("tabname", name);
				if (!(status.isBoolean("muted"))) status.set("muted", false);
				if (!(status.isString("chatcolor"))) status.set("chatcolor", "WHITE");
				if (!(status.isString("chatformat"))) status.set("chatformat", "REGULAR");


				// Setting
				if (p.isOnline()) {
					Player op = p.getPlayer();

					if (config.getBoolean("TopTabEnabled"))
						op.setPlayerListHeader("\n" + ChatColor.translateAlternateColorCodes('&', config.getString("TopTab")) + "\n");
					if (config.getBoolean("BottomTabEnabled"))
						op.setPlayerListFooter("\n" + ChatColor.translateAlternateColorCodes('&', config.getString("BottomTab")) + "\n");

					op.setDisplayName(ChatColor.translateAlternateColorCodes('&', status.getString("displayname")));
					op.setPlayerListName(ChatColor.translateAlternateColorCodes('&', status.getString("tabname")));
				}

				try {
					pInfo.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			plugin.getLogger().severe("Error checking configuration");
			e.printStackTrace();
		}
	}
	
	public static File getPlayersDir() {
		return playerDir;
	}
	
	public static FileConfiguration getInfo(OfflinePlayer p) {
		UUID uuid = (Bukkit.getOnlineMode() ? p.getUniqueId() : UUID.nameUUIDFromBytes(("OfflinePlayer:" + p.getName()).getBytes()));
		
		return YamlConfiguration.loadConfiguration(getFile(p));
	}

	public static File getFile(OfflinePlayer p) {
		try {
			File f = new File(playerDir, p.getUniqueId() + ".yml");
			if (!(f.exists())) f.createNewFile();
			return f;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
