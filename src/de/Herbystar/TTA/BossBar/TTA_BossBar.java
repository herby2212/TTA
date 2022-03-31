package de.Herbystar.TTA.BossBar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;


public class TTA_BossBar {
	
	private BossBar b;
 	
	public void createBossBar(Player player, String text, Double progress, BarStyle barstyle, BarColor barcolor, BarFlag barflag, boolean visible) {
		b = Bukkit.createBossBar(text, BarColor.BLUE, BarStyle.SOLID, BarFlag.CREATE_FOG);
		b.setProgress(progress);
		b.setStyle(barstyle);
		b.setColor(barcolor);
		if(barflag instanceof BarFlag) {
			b.addFlag(barflag);
		}
		b.setVisible(visible);
		b.addPlayer(player);
	}
	
	
	/*
	 * Methods to edit the before created bar in detail afterwards
	 */
	
	public void setBarTitle(String text) {
		b.setTitle(text);
	}
	
	public void setBarProgress(Double progress) {
		b.setProgress(progress);
	}
	
	public void setBarColor(BarColor color) {
		b.setColor(color);
	}
	
	public void setBarStyle(BarStyle style) {
		b.setStyle(style);
	}
	
	public void addBarFlag(BarFlag flag) {
		b.addFlag(flag);
	}
	
	public void setBarVisibility(boolean visible) {
		b.setVisible(visible);
	}
	
	public boolean hasPlayer(Player player) {
		try {
			if(b.getPlayers().contains(player)) {
				return true;
			}
		} catch(NoClassDefFoundError ex) {
			return false;
		}
		return false;
	}
	
	
	/*
	 * Remove a the barflag from the bar - The barflag need to equal the one that was used upon creation or 
	 * that was added later on though the .setBarFlag() method
	 */
	
	public void removeBarFlag(BarFlag flag) {
		b.removeFlag(flag);
	}	
	
	
	/*
	 * Add/Remove the player from the before created bar - Does not delete the bar (remove)
	 */
	
	public void addPlayer(Player player) {
		b.addPlayer(player);
	}
	
	public void removePlayer(Player player) {
		b.removePlayer(player);
	}
}
