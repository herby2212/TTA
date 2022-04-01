package de.Herbystar.TTA.BossBar;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMS_BossBar {
	
	/*
	 * 
	 * Interface class for BossBar version check
	 * 1.6, 1.7 = EnderDragon
	 * 1.8 = Wither
	 * 
	 */
	
	public static void setBossBar(Player player, String text, float health) {
		if(getServerVersion().contains("v1_7")) {
			if(clientVersion(player)) {
				NMS_BossBarWither.setBossBar(player, text, health);
			} else {
				NMS_BossBarDragon.setBossBar(player, text, health);
			}
		} else {
			NMS_BossBarWither.setBossBar(player, text, health);
		}
	}
	
	public static void removeBossBar(Player player) {
		if(getServerVersion().contains("v1_7")) {
			if(clientVersion(player)) {
				NMS_BossBarWither.removeBossBar(player);
			} else {
				NMS_BossBarDragon.removeBossBar(player);
			}
		} else {
			NMS_BossBarWither.removeBossBar(player);
		}
	}
	
	public static boolean hasBossBar(Player player) {
		if(getServerVersion().contains("v1_7")) {
			if(clientVersion(player)) {
				return NMS_BossBarWither.hasBossBar(player);
			} else {
				return NMS_BossBarDragon.hasBossBar(player);
			}
		} else {
			return NMS_BossBarWither.hasBossBar(player);
		}
	}
	
	private static String getServerVersion() {
		Pattern Version_Pattern = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");
		String pkg = Bukkit.getServer().getClass().getPackage().getName();
		String version1 = pkg.substring(pkg.lastIndexOf(".") + 1);
		if(!Version_Pattern.matcher(version1).matches()) {
			version1 = "";
		}
		
		String version = version1;
				return version = !version.isEmpty() ? version + "." : ""; 
	}
	
	private static boolean clientVersion(Player player) {
		if(((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47) {
			return true;
		} else {
			return false;
		}
	}

}
