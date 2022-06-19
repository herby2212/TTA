package de.Herbystar.TTA;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import de.Herbystar.TTA.Glow.*;
import de.Herbystar.TTA.Holo.TTA_HoloAPI;
import de.Herbystar.TTA.Tablist.AnimatedTablist;
import de.Herbystar.TTA.Tablist.NMS_Tablist;
import de.Herbystar.TTA.Title.LegacyTitle;
import de.Herbystar.TTA.Title.NMS_Title;
import de.Herbystar.TTA.ActionBar.NMS_ActionBar;
import de.Herbystar.TTA.BossBar.NMS_BossBar;
import de.Herbystar.TTA.BossBar.TTA_BossBar;
import de.Herbystar.TTA.Utils.TPS;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;
import de.Herbystar.TTA.Utils.Ping;

public class TTA_Methods {
	
	static TTA_HoloAPI th = new TTA_HoloAPI();
	static TTA_BossBar bar = new TTA_BossBar();
	private static AnimatedTablist animatedTablist = new AnimatedTablist();
	private static NMS_Tablist tablist = new NMS_Tablist(Main.instance);
			
	//Tablist
	public static void sendTablist(Player player, String header, String footer) {
		if(header == null) {
			header = "";
		}
		if(footer == null) {
			footer = "";
		}
		
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			return;
		}
		
		tablist.sendTablist(player, header, footer);
	}
	
	//AnimatedTablist
	public static void sendAnimatedTablist(Player player, List<String> headers, List<String> footers, int refreshRateInTicks) {
		if(refreshRateInTicks == 0) {
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§cSendAnimatedTablist: refreshRate needs to be higher then 0.");
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§cA minimum refresh rate of 10 ticks is recommended.");
			return;
		}
		
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			return;
		}
		
		animatedTablist.sendAnimatedTablist(player, headers, footers, refreshRateInTicks);
	}
	
	//ActionBar
	public static void sendActionBar(Player player, String msg) {
		if(msg == null) {
			msg = "";
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			return;
		}
		NMS_ActionBar m = new NMS_ActionBar(Main.instance);
		m.sendActionBar(player, msg);
	}
	
	public static void sendActionBar(Player player, String msg, int duration) {
		if(msg == null) {
			msg = "";
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			return;
		}
		NMS_ActionBar m = new NMS_ActionBar(Main.instance);
		m.sendActionBar(player, msg, duration);
	}
	
	//Titles
	public static void sendTitle(Player player, String title, int fadeint, int stayt, int fadeoutt, String subtitle, int fadeinst, int stayst, int fadeoutst) {
		if(title == null) {
			title = "";
		}
		if(subtitle == null) {
			subtitle = "";
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			LegacyTitle m = new LegacyTitle(Main.instance);
			m.sendTimings(player, fadeint, stayt, fadeoutt);
			m.sendTitle(player, title);
			m.sendTimings(player, fadeinst, stayst, fadeoutst);
			m.sendSubTitle(player, subtitle);
			
		} else if((fadeint == fadeinst && stayt == stayst && fadeoutt == fadeoutst) && 
				TTA_BukkitVersion.getVersionAsInt(2) >= 200) {
			player.sendTitle(title, subtitle, fadeint, stayt, fadeoutt);
		} else {
			NMS_Title m = new NMS_Title(Main.instance);
			m.sendTitle(player, title, fadeint, stayt, fadeoutt, subtitle, fadeinst, stayst, fadeoutst);
		}
	}	
	
	//ItemEnchantmentGlow
	public static ItemMeta createItemGlow(ItemMeta itemmeta) {
		ItemEnchant glow = new ItemEnchant(34);
		itemmeta.addEnchant(glow, 1, true);
		return itemmeta;		
	}
	
	
	//Entity Glow
	@Deprecated
	public static void addEntityGlow(Entity entity) {
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_9_R1.")) {
			EntityGlow_v1_9_R1 m = new EntityGlow_v1_9_R1(Main.instance);
			m.addEntityGlow(entity);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_9_R2.")) {
			EntityGlow_v1_9_R2 m = new EntityGlow_v1_9_R2(Main.instance);
			m.addEntityGlow(entity);
		}
		if(Bukkit.getVersion().contains("1.10")) {
			EntityGlow_v1_10_R1 m = new EntityGlow_v1_10_R1(Main.instance);
			m.addEntityGlow(entity);
		}
		if(Bukkit.getVersion().contains("1.11")) {
			EntityGlow_v1_11_R1 m = new EntityGlow_v1_11_R1(Main.instance);
			m.addEntityGlow(entity);
		}
		if(Bukkit.getVersion().contains("1.12")) {
			EntityGlow_v1_12_R1 m = new EntityGlow_v1_12_R1(Main.instance);
			m.addEntityGlow(entity);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_13_R1")) {
			EntityGlow_v1_13_R1 m = new EntityGlow_v1_13_R1(Main.instance);
			m.addEntityGlow(entity);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_13_R2")) {
			EntityGlow_v1_13_R2 m = new EntityGlow_v1_13_R2(Main.instance);
			m.addEntityGlow(entity);
		}
	}
	public static void addEntityGlow(Entity entity, ChatColor color) {
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_9_R1.")) {
			EntityGlow_v1_9_R1 m = new EntityGlow_v1_9_R1(Main.instance);
			m.addEntityGlow(entity, color);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_9_R2.")) {
			EntityGlow_v1_9_R2 m = new EntityGlow_v1_9_R2(Main.instance);
			m.addEntityGlow(entity, color);
		}
		if(Bukkit.getVersion().contains("1.10")) {
			EntityGlow_v1_10_R1 m = new EntityGlow_v1_10_R1(Main.instance);
			m.addEntityGlow(entity, color);
		}
		if(Bukkit.getVersion().contains("1.11")) {
			EntityGlow_v1_11_R1 m = new EntityGlow_v1_11_R1(Main.instance);
			m.addEntityGlow(entity, color);
		}
		if(Bukkit.getVersion().contains("1.12")) {
			EntityGlow_v1_12_R1 m = new EntityGlow_v1_12_R1(Main.instance);
			m.addEntityGlow(entity, color);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_13_R1")) {
			EntityGlow_v1_13_R1 m = new EntityGlow_v1_13_R1(Main.instance);
			m.addEntityGlow(entity, color);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_13_R2")) {
			EntityGlow_v1_13_R2 m = new EntityGlow_v1_13_R2(Main.instance);
			m.addEntityGlow(entity, color);
		}
	}
	
	public static void removeEntityGlow(Entity entity) {
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_9_R1.")) {
			EntityGlow_v1_9_R1 m = new EntityGlow_v1_9_R1(Main.instance);
			m.removeEntityGlow(entity);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_9_R2.")) {
			EntityGlow_v1_9_R2 m = new EntityGlow_v1_9_R2(Main.instance);
			m.removeEntityGlow(entity);
		}
		if(Bukkit.getVersion().contains("1.10")) {
			EntityGlow_v1_10_R1 m = new EntityGlow_v1_10_R1(Main.instance);
			m.removeEntityGlow(entity);
		}
		if(Bukkit.getVersion().contains("1.11")) {
			EntityGlow_v1_11_R1 m = new EntityGlow_v1_11_R1(Main.instance);
			m.removeEntityGlow(entity);
		}
		if(Bukkit.getVersion().contains("1.12")) {
			EntityGlow_v1_12_R1 m = new EntityGlow_v1_12_R1(Main.instance);
			m.removeEntityGlow(entity);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_13_R1")) {
			EntityGlow_v1_13_R1 m = new EntityGlow_v1_13_R1(Main.instance);
			m.removeEntityGlow(entity);
		}
		if(Main.instance.getServerVersion().equalsIgnoreCase("v1_13_R2")) {
			EntityGlow_v1_13_R2 m = new EntityGlow_v1_13_R2(Main.instance);
			m.removeEntityGlow(entity);
		}
	}
	
	
	//Ping
	public static int getPing(Player p) {
		int i = 0;
		Ping m = new Ping();
		i = m.getPingV2(p);
		return i;
	}
	
	//TPS
	public static double getTPS(int ticks) {
		return TPS.getTPS(ticks);
	}
	
	//HoloAPI
	public static void createHolo(Location location, List<String> lines) {
		if(!Main.instance.getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			if(lines.isEmpty() == true) {
				return;
			}
			th.createHolo(location, lines);
		}
	}
	
	public static void setHoloPlayers(Player players) {
		if(!Main.instance.getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			if(players == null) {
				return;
			}
			th.displayHolo(players);
		}
	}
	
	public static void removeHoloPlayers(Player players) {
		if(!Main.instance.getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			th.destroyHolo(players);
		}
	}
	
	
	//HeadAPI
	@SuppressWarnings("deprecation")
	public static void spawnHead(Location location, String owner) {	
		Location l = location;
		l.getWorld().getBlockAt(l).setType(Material.SKULL);
		Block s = l.getBlock();
		if(s.getType() == Material.SKULL) {
			Skull sk = (Skull) s.getState();
			sk.setSkullType(SkullType.PLAYER);
			if(TTA_BukkitVersion.getVersionAsInt(2) >= 110) {
				sk.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
			} else {
				sk.setOwner(owner);									
			}
			sk.update(true);
		}
	}
	
	/*
	 * BossBar
	 */
	public static void setBossBar(Player p, String text, float health, boolean fixViewDirectionUpdater) {
		NMS_BossBar.setBossBar(p, text, health);
		if(Main.instance.bossBarUpdater.containsKey(p.getUniqueId())) { Main.instance.bossBarUpdater.remove(p.getUniqueId()); }
		if(fixViewDirectionUpdater == true) {
			List<Object> data = new ArrayList<Object>();
			data.add(0, text);
			data.add(1, health);
			Main.instance.bossBarUpdater.put(p.getUniqueId(), data);
		}
	}
	
	public static void createBossBar(Player player, String text, Double progress, BarStyle barstyle, BarColor barcolor, BarFlag barflag, boolean visible) {
		if(progress > 1.0) {
			progress = 1.0;
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§cException: §fBarProgress must between 0.0 and 1.0!");
		}	
		try {
			bar.createBossBar(player, text, progress, barstyle, barcolor, barflag, visible);
		} catch(NoClassDefFoundError e) {
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§cException: §fThis method only works for 1.9 and above!");
		}
	}
	
	public static void setBarTitle(String text) {
		bar.setBarTitle(text);
	}
	
	public static void setBarProgress(Double progress) {
		if(progress > 1.0) {
			progress = 1.0;
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§cException: §fBarProgress must between 0.0 and 1.0!");
		}
		bar.setBarProgress(progress);
	}
	
	public static void setBarColor(BarColor color) {
		bar.setBarColor(color);
	}
	
	public static void setBarStyle(BarStyle style) {
		bar.setBarStyle(style);
	}
	
	public static void addBarFlag(BarFlag flag) {
		bar.addBarFlag(flag);
	}
	
	public static void setBarVisibility(boolean visible) {
		bar.setBarVisibility(visible);
	}
	
	public static void removeBarFlag(BarFlag flag) {
		bar.removeBarFlag(flag);
	}
	
	public static boolean hasBossBar(Player player) {
		if(bar.hasPlayer(player)) {
			return true;
		} else if(NMS_BossBar.hasBossBar(player)) {
			return true;
		}
		return false;
	}
	
	public static void addPlayerToBossBar(Player player) {
		if(bar.hasPlayer(player)) {
			return;
		}
		bar.addPlayer(player);
	}
	
	public static void removeBossBar(Player player) {
		if(bar.hasPlayer(player)) {
			bar.removePlayer(player);
		} else if(NMS_BossBar.hasBossBar(player)) {
			NMS_BossBar.removeBossBar(player);
			if(Main.instance.bossBarUpdater.containsKey(player.getUniqueId())) {
				Main.instance.bossBarUpdater.remove(player.getUniqueId());
			}
		}
	}
}
