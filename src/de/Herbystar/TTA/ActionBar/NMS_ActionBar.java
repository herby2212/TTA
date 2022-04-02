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
	
	private static Class<?> packetClass;
	
	private static Class<?> chatComponentTextClass;
	private static Constructor<?> chatComponentTextConstructor;
	
	private static Class<?> chatMessageTypeClass;
	
	private static Class<?> iChatBaseComponentClass;
	
	private static Class<?> packetPlayOutChatClass;
	private static Constructor<?> packetPlayOutChatConstructor;
	
    static {    
        try {
        	
        	packetClass = Reflection.getNMSClass("Packet");

        	chatComponentTextClass = Reflection.getNMSClass("ChatComponentText");
        	chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);
        	
        	chatMessageTypeClass = Reflection.getNMSClass("ChatMessageType");
        	
        	iChatBaseComponentClass = Reflection.getNMSClass("IChatBaseComponent");
        	                    	
        	packetPlayOutChatClass = Reflection.getNMSClass("PacketPlayOutChat");
        	packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass);

        } catch (NoSuchMethodException | SecurityException ex) {
            System.err.println("Error - Classes not initialized!");
			ex.printStackTrace();
        }
    }
			
	private void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
		    Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
		    playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, new Object[] { packet });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendActionBar(Player p, String msg) {
		if(msg == null) {
			msg = "";
		}		
		try {
			if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.12", "1.13", "1.14", "1.15"), 2)) {
			      Object ab = chatComponentTextConstructor.newInstance(new Object[] { msg });
			      Object acm = chatMessageTypeClass.getField("GAME_INFO").get(null);
			      Object abPacket = packetPlayOutChatConstructor.newInstance(new Object[] { ab, acm });
			      this.sendPacket(p, abPacket);
		      } else if(TTA_BukkitVersion.getVersionAsInt(2) >= 116) {
		    	  packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass, UUID.class);
		    	  
		    	  //1.17 Support
		    	  if(TTA_BukkitVersion.isVersion("1.17", 2)) {
		    		  this.updateToMC17Classes();
		    	  }
		    	  
			      Object ab = chatComponentTextConstructor.newInstance(new Object[] { msg });
			      Object acm = chatMessageTypeClass.getField("GAME_INFO").get(null);
			      Object abPacket = packetPlayOutChatConstructor.newInstance(new Object[] { ab, acm, p.getUniqueId() });
			      this.sendPacket(p, abPacket);
		      } else {
		    	  packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE);
		    	  
			      Object ab = chatComponentTextConstructor.newInstance(new Object[] { msg });
			      Object abPacket = packetPlayOutChatConstructor.newInstance(new Object[] { ab, Byte.valueOf((byte) 2) });
			      this.sendPacket(p, abPacket);
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
			packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE);
			
			Object ab = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\": \"" + msg + "\"}" });
			Object Abpacket = packetPlayOutChatConstructor.newInstance(new Object[] { ab, Byte.valueOf((byte) 2) });
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
	
	private void updateToMC17Classes() {
    	try {
    		packetClass = Class.forName("net.minecraft.network.protocol.Packet");
    		
    		chatComponentTextClass = Class.forName("net.minecraft.network.chat.ChatComponentText");
			chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);
			
	    	chatMessageTypeClass = Class.forName("net.minecraft.network.chat.ChatMessageType");
	    	
	    	iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");

	    	packetPlayOutChatClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutChat");
	    	packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass, UUID.class);

		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}

	}
}
