package us.teaminceptus.plutochat.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import revxrsal.commands.annotation.*;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.exception.CommandErrorException;
import us.teaminceptus.plutochat.PlutoChat;

public final class ChatColorCommand {

	protected final PlutoChat plugin;

	public ChatColorCommand(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getHandler().registerValueResolver(ChatColor.class, ctx -> {
			String param = ctx.popForParameter();
			if (param.equalsIgnoreCase("regular")) return ChatColor.RESET;
			try {
				return ChatColor.valueOf(param.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new CommandErrorException(PlutoChat.getMessage("arguments.code"));
			}
		});

		plugin.getHandler().getAutoCompleter().registerSuggestion("color", (args, sender, cmd) -> {
			Set<String> sugg = new HashSet<>();
			for (ChatColor c : ChatColor.values()) if (c.isColor()) sugg.add(c.name().toLowerCase());
			return sugg;
		});

		plugin.getHandler().getAutoCompleter().registerSuggestion("format", (args, sender, cmd) -> {
			Set<String> sugg = new HashSet<>();
			sugg.add("regular");
			for (ChatColor c : ChatColor.values()) if (c.isFormat()) sugg.add(c.name().toLowerCase());
			return sugg;
		});

		plugin.getHandler().register(this);
	}

	@Command({"chatcolor", "chatc", "chatformat", "chatf", "cc", "cf"})
	@Description("Set ChatColor and ChatFormat for speaking in chat.")
	@Usage("/chatcolor <color> [format]")
	@CommandPermission("plutochat.chat.color")
	@AutoComplete("@color @format")
	public void chatcolor(Player p, ChatColor colorcode, @Optional ChatColor format) {
		ChatColor color = colorcode;
		FileConfiguration pConfig = PlutoChat.getInfo(p);
		ConfigurationSection info = pConfig.getConfigurationSection("information");

		if (!color.isColor()) {
			p.sendMessage(PlutoChat.getMessage("arguments.color_code"));
			return;
		}

		info.set("chatcolor", color.name());
		p.sendMessage(String.format(PlutoChat.getMessage("response.success.set_chatcolor"), color, color.name().toLowerCase()));

		if (format != null)
			if (format == ChatColor.RESET) {
				info.set("chatformat", "REGULAR");
				p.sendMessage(PlutoChat.getMessage("response.success.reset_format"));
			} else {
				if (!format.isFormat()) p.sendMessage(PlutoChat.getMessage("arguments.format_code"));
				else {
					info.set("chatformat", format.name());
					p.sendMessage(String.format(PlutoChat.getMessage("response.success.set_chatformat"), format, format.name().toLowerCase()));
				}
			}

		try {
			pConfig.save(PlutoChat.getFile(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}