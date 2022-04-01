package de.Herbystar.TTA.Glow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.Herbystar.TTA.Main;

public class GlowColor {
	
	private static Scoreboard cScoreboard;
			
	public static void initializeColorScoreboard() {
		cScoreboard = Main.instance.getServer().getScoreboardManager().getMainScoreboard();
		
		for(ChatColor c : ChatColor.values()) {
			Team t = cScoreboard.getTeam(c.toString());
			if(t == null) {
				t = cScoreboard.registerNewTeam(c.toString());
			}
			t.setPrefix(c.toString());
		}
	}
	
	public static void setGlowScoreboard(LivingEntity entity, ChatColor color) {
		try {
			Team t = cScoreboard.getTeam(color.toString());
			t.addEntry(entity.getName());
		} catch(Exception ex) {
			ex.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§cInternal Error - Could not select corresponding team for glow color!");
		}
		
	}
	
	public static void unloadColorScoreboard() {
		cScoreboard = Main.instance.getServer().getScoreboardManager().getMainScoreboard();
		
		for(ChatColor c : ChatColor.values()) {
			Team t = cScoreboard.getTeam(c.toString());
			if(t != null) {
				t.unregister();
			}
		}
	}
	
}
