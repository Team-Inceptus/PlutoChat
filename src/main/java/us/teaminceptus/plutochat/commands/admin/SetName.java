package us.teaminceptus.plutochat.commands.admin;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.plutochat.PlutoChat;

public final class SetName {
	
	protected final PlutoChat plugin;
	
	public SetName(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getHandler().register(this);

		plugin.getHandler().registerValueResolver(DisplayType.class, ctx -> DisplayType.valueOf(ctx.popForParameter().toUpperCase()));
		plugin.getHandler().getAutoCompleter().registerParameterSuggestions(DisplayType.class, SuggestionProvider.of(DisplayType.NAME_ARRAY));
	}

	public enum DisplayType {
		TAB,
		DISPLAY,
		BOTH,
		RESET;

		public static final String[] NAME_ARRAY = new String[] { "tab", "display", "both", "reset"};
	}

	@Command({"setname", "setn", "name"})
	@Description("Set the player's display or tab list name.")
	@Usage("/setname <target> <type> <name>")
	@CommandPermission("plutochat.admin.setname")
	public void setName(CommandSender sender, OfflinePlayer target, DisplayType type, String... nameArgs) {
		FileConfiguration info = PlutoChat.getInfo(target);
		String name = String.join(" ", nameArgs);
		String chatName = ChatColor.translateAlternateColorCodes('&', name) + ChatColor.RESET;

		if (target.isOnline()) {
			Player ot = target.getPlayer();

			switch (type) {
				case TAB: {
					ot.setPlayerListName(chatName);
					PlutoChat.getInfo(ot).set("tabname", name);
					break;
				}
				case DISPLAY: {
					ot.setDisplayName(chatName);
					PlutoChat.getInfo(ot).set("tabname", name);
					PlutoChat.getInfo(ot).set("displayname", name);
					break;
				}
				case BOTH: {
					ot.setPlayerListName(chatName);
					ot.setDisplayName(chatName);
					PlutoChat.getInfo(ot).set("tabname", name);
					PlutoChat.getInfo(ot).set("displayname", name);
					break;
				}
				case RESET: {
					ot.setPlayerListName(ot.getName());
					ot.setDisplayName(ot.getName());
					PlutoChat.getInfo(ot).set("tabname", ot.getName());
					PlutoChat.getInfo(ot).set("displayname", ot.getName());
					break;
				}
			}
		} else {
			sender.sendMessage(PlutoChat.getMessage("response.offline"));
			return;
		}
		sender.sendMessage(String.format(PlutoChat.getMessage("response.success.set_name"), chatName));
		try {
			info.save(PlutoChat.getFile(target));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
