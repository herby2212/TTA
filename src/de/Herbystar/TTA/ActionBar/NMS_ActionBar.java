package de.Herbystar.TTA.ActionBar;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.Herbystar.TTA.Main;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;

public class NMS_ActionBar {
	
	Main plugin;
	public NMS_ActionBar(Main main) {
		plugin = main;
	}
	
	final static HashMap<UUID, Integer> Count = new HashMap<UUID, Integer>();
			
	private void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
		    Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
		    playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private Class<?> getNMSClass(String class_name) {
	    String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	    try {
	    	return Class.forName("net.minecraft.server." + version + "." + class_name);
	    } catch (ClassNotFoundException ex) {
	    	ex.printStackTrace();
	    }
	    return null;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public void sendActionBar(Player p, String msg) {
		if(msg == null) {
			msg = "";
		}		
		try {
			if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.12", "1.13", "1.14", "1.15"), 2)) {
			      Object ab = this.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { msg });
			      Object acm = this.getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
			      Constructor ac = this.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { this.getNMSClass("IChatBaseComponent"), this.getNMSClass("ChatMessageType") });
			      Object abPacket = ac.newInstance(new Object[] { ab, acm });
			      this.sendPacket(p, abPacket);
		      } else if(TTA_BukkitVersion.getVersionAsInt(2) >= 116) {
			      Object ab = this.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { msg });
			      Object acm = this.getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
			      Constructor ac = this.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { this.getNMSClass("IChatBaseComponent"), this.getNMSClass("ChatMessageType"), UUID.class });
			      Object abPacket = ac.newInstance(new Object[] { ab, acm, p.getUniqueId() });
			      this.sendPacket(p, abPacket);
		      } else {
			      Object ab = this.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { msg });
			      Constructor ac = this.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { this.getNMSClass("IChatBaseComponent"), Byte.TYPE });
			      Object abPacket = ac.newInstance(new Object[] { ab, Byte.valueOf((byte) 2) });
			      this.sendPacket(p, abPacket);
		      }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	@Deprecated
	public void sendOldActionBar(Player p, String msg) {
		if(msg == null) {
			msg = "";
		}
		
		try {
			Object ab = this.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class}).invoke(null, new Object[] { "{\"text\": \"" + msg + "\"}" });
			Constructor actionbar = this.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { getNMSClass("IChatBaseComponent"), Byte.TYPE });
			Object Abpacket = actionbar.newInstance(new Object[] { ab, Byte.valueOf((byte) 2) });
			this.sendPacket(p, Abpacket);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendActionBar(final Player p, final String msg, final int duration) {
		if(!Count.containsKey(p.getUniqueId())) {
			sendActionBar(p, msg);
		}
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				sendActionBar(p, msg);
				if(!Count.containsKey(p.getUniqueId())) {
					Count.put(p.getUniqueId(), 0);
				}
				int c = Count.get(p.getUniqueId());
				c = c+20;
				Count.put(p.getUniqueId(), c);
				if(c < duration-20) {
					switchActionBar(p, msg, duration);
				} else {
					Count.remove(p.getUniqueId());
				}
			}			
		}, 10);
	}
	
	public void switchActionBar(final Player p, final String msg, final int duration) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				sendActionBar(p, msg, duration);
			}			
		}, 10);
	}
}
