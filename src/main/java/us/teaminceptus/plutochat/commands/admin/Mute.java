package us.teaminceptus.plutochat.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.plutochat.PlutoChat;
import us.teaminceptus.plutochat.utils.PlutoUtils;

@CommandPermission("plutochat.admin.mute")
public final class Mute {

	protected final PlutoChat plugin;
	
	public Mute(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getHandler().register(this);
	}

	@Command("setmute")
	@Description("Ability to mute and unmute players")
	@Usage("/setmute <player> [mute]")
	public void mute(CommandSender sender, OfflinePlayer target, @Default("true") boolean mute) {
		PlutoUtils.setMuted(target, mute);
		if (mute) sender.sendMessage(String.format(PlutoChat.getConstant("response.mute"), target.getName()));
		else sender.sendMessage(String.format(PlutoChat.getConstant("response.unmute"), target.getName()));
	}

	@Command({"mute", "silence", "shut"})
	@Description("Mutes a Player.")
	@Usage("/mute <player>")
	public void mute(CommandSender sender, OfflinePlayer target) {
		mute(sender, target, true);
	}

	@Command({"unmute", "unsilence", "unshut"})
	@Description("Unmutes a Player.")
	@Usage("/unmute <player>")
	public void unmute(CommandSender sender, OfflinePlayer target) {
		mute(sender, target, false);
	}
}
