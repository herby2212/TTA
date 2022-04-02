package de.Herbystar.TTA.Tablist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import de.Herbystar.TTA.Main;
import de.Herbystar.TTA.Utils.Reflection;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;

public class NMS_Tablist {
	
	Main plugin;
	public NMS_Tablist(Main main) {
		plugin = main;
	}

	private static Class<?> packetClass;
	
	private static Class<?> chatComponentTextClass;
	private static Constructor<?> chatComponentTextConstructor;
		
	private static Class<?> iChatBaseComponentClass;
	
	private static Class<?> packetPlayOutPlayerListHeaderFooterClass;
	private static Constructor<?> packetPlayOutPlayerListHeaderFooterConstructor;
	
    static {    
        try {
    		if(TTA_BukkitVersion.isVersion("1.17", 2)) {
	    		  updateToMC17Classes();
    		} else {
            	packetClass = Reflection.getNMSClass("Packet");

            	chatComponentTextClass = Reflection.getNMSClass("ChatComponentText");
            	chatComponentTextConstructor = chatComponentTextClass.getConstructor(new Class[] { String.class });
            	        	
            	iChatBaseComponentClass = Reflection.getNMSClass("IChatBaseComponent");
            	                    	
            	packetPlayOutPlayerListHeaderFooterClass = Reflection.getNMSClass("PacketPlayOutPlayerListHeaderFooter");
            	
            	if(TTA_BukkitVersion.getVersionAsInt(2) >= 112) {
            		packetPlayOutPlayerListHeaderFooterConstructor = packetPlayOutPlayerListHeaderFooterClass.getConstructor(new Class[0]);
            	} else {
                	packetPlayOutPlayerListHeaderFooterConstructor = packetPlayOutPlayerListHeaderFooterClass.getConstructor(new Class[] { iChatBaseComponentClass });
            	}
    		}
        } catch (NoSuchMethodException | SecurityException ex) {
            System.err.println("Error - Classes not initialized!");
			ex.printStackTrace();
        }
    }
	
	private void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection;
			if(TTA_BukkitVersion.isVersion("1.17", 2)) {
			    playerConnection = handle.getClass().getField("b").get(handle);
			} else {
			    playerConnection = handle.getClass().getField("playerConnection").get(handle);
			}
		    playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, new Object[] { packet });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	public void sendTablist(Player p, String header, String footer) {
		if(header == null) {
			header = "";
		}
		if(footer == null) {
			footer = "";
		}
		
		try {
			if(TTA_BukkitVersion.getVersionAsInt(2) >= 112 && TTA_BukkitVersion.getVersionAsInt(2) < 117) {
			    Object tabheader = chatComponentTextConstructor.newInstance(new Object[] { header });
			    Object tabfooter = chatComponentTextConstructor.newInstance(new Object[] { footer });

			    Object THpacket = packetPlayOutPlayerListHeaderFooterConstructor.newInstance(new Object[0]);
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
			} else if(TTA_BukkitVersion.getVersionAsInt(2) >= 117) {
				Object tabheader = chatComponentTextConstructor.newInstance(new Object[] { header });
			    Object tabfooter = chatComponentTextConstructor.newInstance(new Object[] { footer });
			    
			    Object THpacket = packetPlayOutPlayerListHeaderFooterConstructor.newInstance(new Object[] { tabheader, tabfooter });
				this.sendPacket(p, THpacket);
			} else {
			    Object tabheader = chatComponentTextConstructor.newInstance(new Object[] { header });
			    Object tabfooter = chatComponentTextConstructor.newInstance(new Object[] { footer });
			    Object THpacket = packetPlayOutPlayerListHeaderFooterConstructor.newInstance(new Object[] { tabheader });
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
			Object tabheader = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class}).invoke(null, new Object[] { "{\"text\": \"" + header + "\"}" });
		    Object tabfooter = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + footer + "\"}" });
		    Object THpacket = packetPlayOutPlayerListHeaderFooterConstructor.newInstance(new Object[] { tabheader });
			Field f = THpacket.getClass().getDeclaredField("b");
			f.setAccessible(true);
			f.set(THpacket, tabfooter);
			this.sendPacket(p, THpacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void updateToMC17Classes() {
    	try {
    		packetClass = Class.forName("net.minecraft.network.protocol.Packet");
    		
    		chatComponentTextClass = Class.forName("net.minecraft.network.chat.ChatComponentText");
			chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);
				    	
	    	iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");

	    	packetPlayOutPlayerListHeaderFooterClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter");
	    	packetPlayOutPlayerListHeaderFooterConstructor = packetPlayOutPlayerListHeaderFooterClass.getConstructor(iChatBaseComponentClass, iChatBaseComponentClass);

		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

}
