package de.Herbystar.TTA.Title;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.Herbystar.TTA.Main;

public class NMS_Title {
	
	Main plugin;
	public NMS_Title(Main main) {
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
		
	
	@SuppressWarnings("unused")
	private Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void sendTitle(Player p, String title, int fadeint, int stayt, int fadeoutt, String subtitle, int fadeinst, int stayst, int fadeoutst) {
		if(title == null) {
			title = "";
		}
		if(subtitle == null) {
			subtitle = "";
		}
		
		try {
			if(title != null) {
		        Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object)null);
		        Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + title + "\"}" });
		        Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
		        Object titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle, fadeint, stayt, fadeoutt });
		        this.sendPacket(p, titlePacket);

		        e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get((Object)null);
		        chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + title + "\"}" });
		        subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent") });
		        titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle });
		        this.sendPacket(p, titlePacket);
			}
			
			if(subtitle != null) {
		        Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object)null);
		        Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + title + "\"}" });
		        Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
		        Object subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeinst, stayst, fadeoutst });
		        sendPacket(p, subtitlePacket);

		        e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get((Object)null);
		        chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
		        subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
		        subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeinst, stayst, fadeoutst });
		        sendPacket(p, subtitlePacket);
			}	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
