package de.Herbystar.TTA.ActionBar;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.Herbystar.TTA.Main;
import de.Herbystar.TTA.Utils.Reflection;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;

public class NMS_ActionBar {
	
	Main plugin;
	public NMS_ActionBar(Main main) {
		plugin = main;
	}
		
	final static HashMap<UUID, Integer> Count = new HashMap<UUID, Integer>();
	
	private static Class<?> chatComponentTextClass;
	private static Constructor<?> chatComponentTextConstructor;
	
	private static Class<?> chatMessageTypeClass;
	
	private static Class<?> iChatBaseComponentClass;
	
	private static Class<?> packetPlayOutChatClass;
	private static Constructor<?> packetPlayOutChatConstructor;
	
	private static Class<?> clientboundSetActionBarTextPacketClass;
	private static Constructor<?> clientboundSetActionBarTextPacketConstructor;
	
    static {    
        try {
        	//1.17 & 1.18 Support
	    	if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.17", "1.18", "1.19"), 2)) {
	    		updateToNewClassStructure();
	    	} else {
	        	chatComponentTextClass = Reflection.getNMSClass("ChatComponentText");
	        	chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);
	        	
	        	chatMessageTypeClass = Reflection.getNMSClass("ChatMessageType");
	        	
	        	iChatBaseComponentClass = Reflection.getNMSClass("IChatBaseComponent");
	        	                    	
	        	packetPlayOutChatClass = Reflection.getNMSClass("PacketPlayOutChat");
	        	if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.12", "1.13", "1.14", "1.15"), 2)) {
	        		packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass);
	        	} else if(TTA_BukkitVersion.isVersion("1.16", 2)) {
	        		packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass, UUID.class);
	        	} else {
	        		packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE);
	        	}

	    	}
        } catch (NoSuchMethodException | SecurityException ex) {
            System.err.println("Error - Classes not initialized!");
			ex.printStackTrace();
        }
    }
	
	public void sendActionBar(Player p, String msg) {
		if(msg == null) {
			msg = "";
		}
		
		try {
			Object ab = chatComponentTextConstructor.newInstance(new Object[] { msg });
			if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.12", "1.13", "1.14", "1.15"), 2)) {
			      
			      Object acm = chatMessageTypeClass.getField("GAME_INFO").get(null);
			      Object abPacket = packetPlayOutChatConstructor.newInstance(new Object[] { ab, acm });
			      Reflection.sendPacket(p, abPacket);
			  } else if(TTA_BukkitVersion.isVersion("1.16", 2)) {
				  Object acm = chatMessageTypeClass.getField("GAME_INFO").get(null);
				  Object abPacket = packetPlayOutChatConstructor.newInstance(new Object[] { ab, acm, p.getUniqueId() });
			      Reflection.sendPacket(p, abPacket);
		      } else if(TTA_BukkitVersion.getVersionAsInt(2) >= 117) {
			      Object abPacket = clientboundSetActionBarTextPacketConstructor.newInstance(new Object[] { ab });
			      Reflection.sendPacket(p, abPacket);
		      } else {  	  
			      Object abPacket = packetPlayOutChatConstructor.newInstance(new Object[] { ab, Byte.valueOf((byte) 2) });
			      Reflection.sendPacket(p, abPacket);
		      }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	@Deprecated
	public void sendOldActionBar(Player p, String msg) {
		if(msg == null) {
			msg = "";
		}
		
		try {
			Object ab = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\": \"" + msg + "\"}" });
			Object Abpacket = packetPlayOutChatConstructor.newInstance(new Object[] { ab, Byte.valueOf((byte) 2) });
			Reflection.sendPacket(p, Abpacket);
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
	
	private static void updateToNewClassStructure() {
    	try {
    		chatComponentTextClass = Class.forName("net.minecraft.network.chat.ChatComponentText");
			chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);
			
	    	chatMessageTypeClass = Class.forName("net.minecraft.network.chat.ChatMessageType");
	    	
	    	iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");

	    	packetPlayOutChatClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutChat");
	    	packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass, UUID.class);
	    	
	    	clientboundSetActionBarTextPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket");
	    	clientboundSetActionBarTextPacketConstructor = clientboundSetActionBarTextPacketClass.getConstructor(iChatBaseComponentClass);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
}
