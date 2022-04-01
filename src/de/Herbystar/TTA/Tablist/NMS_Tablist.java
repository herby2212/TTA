package de.Herbystar.TTA.Tablist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.Herbystar.TTA.Main;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;

public class NMS_Tablist {
	
	Main plugin;
	public NMS_Tablist(Main main) {
		plugin = main;
	}
		
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
		
	
	
	@SuppressWarnings("rawtypes")
	public void sendTablist(Player p, String header, String footer) {
		if(header == null) {
			header = "";
		}
		if(footer == null) {
			footer = "";
		}
		
		try {
			if(TTA_BukkitVersion.getVersionAsInt(2) >= 112) {
			    Object tabheader = this.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { header });
			    Object tabfooter = this.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { footer });

				Constructor tablist = this.getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[0]);
			    Object THpacket = tablist.newInstance(new Object[0]);
			    Field fa = null;
			    Field fb = null;
			    try {
				    fa = THpacket.getClass().getDeclaredField("a");
					fb = THpacket.getClass().getDeclaredField("b");
			    } catch(NoSuchFieldException e) {
				    fa = THpacket.getClass().getDeclaredField("header");
					fb = THpacket.getClass().getDeclaredField("footer");
			    }
			    fa.setAccessible(true);
			    fa.set(THpacket, tabheader);
				fb.setAccessible(true);
				fb.set(THpacket, tabfooter);
				this.sendPacket(p, THpacket);
			} else {
			    Object tabheader = this.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { header });
			    Object tabfooter = this.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { footer });
			    Constructor tablist = this.getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[] { getNMSClass("IChatBaseComponent") });
			    Object THpacket = tablist.newInstance(new Object[] { tabheader });
				Field f = THpacket.getClass().getDeclaredField("b");
				f.setAccessible(true);
				f.set(THpacket, tabfooter);
				this.sendPacket(p, THpacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Deprecated
	public void sendOldTablist(Player p, String header, String footer) {
		if(header == null) {
			header = "";
		}
		if(footer == null) {
			footer = "";
		}
				
		try {
			Object tabheader = this.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class}).invoke(null, new Object[] { "{\"text\": \"" + header + "\"}" });
		    Object tabfooter = this.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + footer + "\"}" });
		    @SuppressWarnings("rawtypes")
			Constructor tablist = this.getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[] { getNMSClass("IChatBaseComponent") });
		    Object THpacket = tablist.newInstance(new Object[] { tabheader });
			Field f = THpacket.getClass().getDeclaredField("b");
			f.setAccessible(true);
			f.set(THpacket, tabfooter);
			this.sendPacket(p, THpacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
