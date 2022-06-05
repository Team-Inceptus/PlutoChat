package us.teaminceptus.plutochat.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.plutochat.PlutoChat;

public final class ChatColorCommand {

	protected final PlutoChat plugin;

	public ChatColorCommand(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getHandler().register(this);

		plugin.getHandler().registerValueResolver(ChatColor.class, ctx -> {
			if (ctx.popForParameter().equalsIgnoreCase("regular")) return ChatColor.RESET;
			return ChatColor.valueOf(ctx.popForParameter().toUpperCase());
		});
		plugin.getHandler().getAutoCompleter().registerParameterSuggestions(ChatColor.class, SuggestionProvider.of(new ArrayList<String>() {{
			for (ChatColor c : ChatColor.values()) {
				if (c == ChatColor.RESET) {
					add("regular");
					continue;
				}
				add(c.name().toLowerCase());
			}
		}}));
	}

	@Command({"chatcolor", "chatc", "chatformat", "chatf", "cc", "cf"})
	@Description("Set ChatColor and ChatFormat for speaking in chat.")
	@Usage("/chatcolor <color> [format]")
	@CommandPermission("plutochat.chat.color")
	public void chatcolor(Player p, ChatColor code, @Optional ChatColor format) {
		FileConfiguration config = PlutoChat.getInfo(p);

		if (!code.isColor()) {
			p.sendMessage(PlutoChat.getMessage("arguments.color_code"));
			return;
		}

		config.set("chatcolor", code.name());
		p.sendMessage(String.format(PlutoChat.getMessage("response.success.set_chatcolor"), code, code.name().toLowerCase()));

		if (format != null) {
			if (format == ChatColor.RESET) {
				config.set("chatformat", null);
				p.sendMessage(PlutoChat.getMessage("response.success.reset_format"));
				return;
			}

			if (!format.isFormat()) {
				p.sendMessage(PlutoChat.getMessage("arguments.format_code"));
				return;
			}

			config.set("chatformat", format.name());
			p.sendMessage(String.format(PlutoChat.getMessage("response.success.set_chatformat"), format, format.name().toLowerCase()));
		}

		try {
			config.save(PlutoChat.getFile(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}