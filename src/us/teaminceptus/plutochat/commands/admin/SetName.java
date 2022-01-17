package us.teaminceptus.plutochat.commands.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import us.teaminceptus.plutochat.PlutoChat;
import us.teaminceptus.plutochat.PlutoChat.PlutoError;
import us.teaminceptus.plutochat.utils.PlutoUtils;

public class SetName implements TabExecutor {
	
	protected PlutoChat plugin;
	
	public SetName(PlutoChat plugin) {
		this.plugin = plugin;
		plugin.getCommand("setname").setExecutor(this);
		plugin.getCommand("setname").setTabCompleter(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			PlutoChat.sendError(sender, PlutoError.ARGS_PLAYER);
			return false;
		}
		
		if (Bukkit.getOfflinePlayer(PlutoUtils.nameToUUID(args[0])) == null) {
			PlutoChat.sendError(sender, PlutoError.ARGS_PLAYER);
			return false;
		}
		
		OfflinePlayer target = Bukkit.getOfflinePlayer(PlutoUtils.nameToUUID(args[0]));
		
		if (args.length < 2) {
			PlutoChat.sendError(sender, "Please provide tab, display, both, or reset.");
			return false;
		}
		
		
		if (!(args[1].equalsIgnoreCase("tab")) && !(args[1].equalsIgnoreCase("display")) && !(args[1].equalsIgnoreCase("both")) && !(args[1].equalsIgnoreCase("reset"))) {
			PlutoChat.sendError(sender, "Please provide tab, display, both, or reset.");
			return false;
		}
		
		if (args.length < 3) {
			PlutoChat.sendError(sender, "Please provide a name.");
			return false;
		}
		
		
		List<String> nameArgs = new ArrayList<String>();
		for (int i = 2; i < args.length; i++) {
			nameArgs.add(args[i]);
		}
		String name = String.join(" ", nameArgs);
		
		
		if (target.isOnline()) {
			Player ot = target.getPlayer();
			if (args[1].equalsIgnoreCase("tab")) {
				ot.setPlayerListName(ChatColor.translateAlternateColorCodes('&', name) + ChatColor.RESET);
				PlutoChat.getInfo(ot).set("tabname", name);
			} else if (args[1].equalsIgnoreCase("display")) {
				ot.setDisplayName(ChatColor.translateAlternateColorCodes('&', name) + ChatColor.RESET);
				PlutoChat.getInfo(ot).set("tabname", name);
				PlutoChat.getInfo(ot).set("displayname", name);
			} else if (args[1].equalsIgnoreCase("both")) {
				ot.setPlayerListName(ChatColor.translateAlternateColorCodes('&', name) + ChatColor.RESET);
				ot.setDisplayName(ChatColor.translateAlternateColorCodes('&', name) + ChatColor.RESET);
				PlutoChat.getInfo(ot).set("tabname", name);
				PlutoChat.getInfo(ot).set("displayname", name);
			} else if (args[1].equalsIgnoreCase("reset")) {
				ot.setPlayerListName(ot.getName());
				ot.setDisplayName(ot.getName());
				PlutoChat.getInfo(ot).set("tabname", ot.getName());
				PlutoChat.getInfo(ot).set("displayname", ot.getName());
			}
		} else {
			PlutoChat.sendError(sender, ChatColor.RED + "This player is not online.");
			return false;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Succesfully set " + ChatColor.GOLD + args[1].toLowerCase() + ChatColor.GREEN + " to: \"" + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', name) + ChatColor.GREEN +  "\"");
		try {
			PlutoChat.getPlayersConfig().save(PlutoChat.getPlayersFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> suggestions = new ArrayList<>();
		
		switch (args.length) {
			case 1: {
				for (Player p : Bukkit.getOnlinePlayers()) suggestions.add(p.getName());
				return suggestions;
			}
			case 2: {
				suggestions.addAll(Arrays.asList("tab", "display", "both", "reset"));
				return suggestions;
			}
		}
		
		return suggestions;
	}

}
