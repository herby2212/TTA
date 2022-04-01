package de.Herbystar.TTA.Utils;

import java.util.List;

import org.bukkit.Bukkit;

public class TTA_BukkitVersion {
	
	public static String getVersion() {
		return Bukkit.getServer().getBukkitVersion();
	}
	
	public static String getVersion(boolean split, int parts) {
		if(split == true) {
			String cleanVersion = getVersion().split("-")[0];
			if(parts > 0) {
				String output = "";
				//Replacement with '-' as splitting on dot does not work for unknown reason on server startup 
				String[] s = cleanVersion.replace(".", "-").split("-");
				for(int i = 0; i < parts; i++) {
					if(i != 0) {
						output += ".";
					}
					output += s[i];
				}
				return output;
			}
			return cleanVersion;
		}
		return getVersion();
	}
	
	public static int getVersionAsInt() {
		String v = getVersion(true, 0).replace(".", "");
		return Integer.parseInt(v);
	}
	
	public static int getVersionAsInt(int parts) {
		String v = getVersion(true, parts).replace(".", "");
		return Integer.parseInt(v);
	}
	
	public static boolean isVersion(String version) {
		return version.equalsIgnoreCase(getVersion(true, 0));
	}
	
	public static boolean isVersion(String version, int parts) {
		return version.equalsIgnoreCase(getVersion(true, parts));
	}
	
	public static boolean matchVersion(List<String> versions) {
		return versions.contains(getVersion(true, 0));
	}
	
	public static boolean matchVersion(List<String> versions, int parts) {
		return versions.contains(getVersion(true, parts));
	}
	
	public static String getEngine() {
		String e = "Unknown Engine!";
		if(Bukkit.getVersion().contains("Paper")) {
			e = "Paper";
		}
		if(Bukkit.getVersion().contains("Spigot")) {
			e = "Spigot";
		}
		if(Bukkit.getVersion().contains("Bukkit")) {
			e = "Bukkit | CraftBukkit";
		}
		return e;
	}
}
