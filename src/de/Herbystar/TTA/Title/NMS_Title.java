package de.Herbystar.TTA.Title;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.bukkit.entity.Player;

import de.Herbystar.TTA.Main;
import de.Herbystar.TTA.Utils.Reflection;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;

public class NMS_Title {
	
	Main plugin;
	public NMS_Title(Main main) {
		plugin = main;
	}
	
	private static Class<?> packetClass;
			
	private static Class<?> iChatBaseComponentClass;
	
	private static Class<?> packetPlayOutTitleClass;
	private static Constructor<?> packetPlayOutTitleConstructorNormal;
	private static Constructor<?> packetPlayOutTitleConstructorReduced;
	
	private static Class<?> clientboundSetTitleTextPacketClass;
	private static Constructor<?> clientboundSetTitleTextPacketConstructor;
	private static Class<?> clientboundSetSubtitleTextPacketClass;
	private static Constructor<?> clientboundSetSubtitleTextPacketConstructor;
	private static Class<?> clientboundSetTitlesAnimationPacketClass;
	private static Constructor<?> clientboundSetTitlesAnimationPacketConstructor;
	@SuppressWarnings("unused")
	private static Class<?> clientboundClearTitlesPacketClass;
	
    static {    
        try {
        	if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.17", "1.18"), 2)) {
        		updateToMC17Classes();
        	} else {
            	packetClass = Reflection.getNMSClass("Packet");
	        	
            	iChatBaseComponentClass = Reflection.getNMSClass("IChatBaseComponent");
            	                    	
            	packetPlayOutTitleClass = Reflection.getNMSClass("PacketPlayOutTitle");
            	packetPlayOutTitleConstructorNormal = packetPlayOutTitleClass.getConstructor(new Class[] { packetPlayOutTitleClass.getDeclaredClasses()[0], iChatBaseComponentClass, Integer.TYPE, Integer.TYPE, Integer.TYPE });
            	packetPlayOutTitleConstructorReduced = packetPlayOutTitleClass.getConstructor(new Class[] { packetPlayOutTitleClass.getDeclaredClasses()[0], iChatBaseComponentClass });
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
			if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.17", "1.18"), 2)) {
			    playerConnection = handle.getClass().getField("b").get(handle);
			} else {
			    playerConnection = handle.getClass().getField("playerConnection").get(handle);
			}
			Method sPacket;
			if(TTA_BukkitVersion.isVersion("1.18", 2)) {
				sPacket = playerConnection.getClass().getMethod("a", packetClass);
			} else {
				sPacket = playerConnection.getClass().getMethod("sendPacket", packetClass);
			}
			sPacket.invoke(playerConnection, new Object[] { packet });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
			Object chatTitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + title + "\"}" });
			Object chatSubtitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });       
		
			if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.17", "1.18"), 2)) {
	    		if(title != null) {
	    			Object timesPacket = clientboundSetTitlesAnimationPacketConstructor.newInstance(new Object[] {fadeint, stayt, fadeoutt});
	    			this.sendPacket(p, timesPacket);
	    			
	    			Object titlePacket = clientboundSetTitleTextPacketConstructor.newInstance(new Object[] { chatTitle });
	    			this.sendPacket(p, titlePacket);
	    		}
	    		if(subtitle != null) {
	    			Object timesPacket = clientboundSetTitlesAnimationPacketConstructor.newInstance(new Object[] {fadeint, stayt, fadeoutt});
	    			this.sendPacket(p, timesPacket);
	    			
	    			Object subtitlePacket = clientboundSetSubtitleTextPacketConstructor.newInstance(new Object[] { chatSubtitle });
	    			this.sendPacket(p, subtitlePacket);
	    		}
	    	} else {
				if(title != null) {
			        Object e = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TIMES").get((Object)null);
			        Object titlePacket = packetPlayOutTitleConstructorNormal.newInstance(new Object[] { e, chatTitle, fadeint, stayt, fadeoutt });
			        this.sendPacket(p, titlePacket);

			        e = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TITLE").get((Object)null);
			        titlePacket = packetPlayOutTitleConstructorReduced.newInstance(new Object[] { e, chatTitle });
			        this.sendPacket(p, titlePacket);
				}
				
				if(subtitle != null) {
			        Object e = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TIMES").get((Object)null);
			        Object subtitlePacket = packetPlayOutTitleConstructorNormal.newInstance(new Object[] { e, chatSubtitle, fadeinst, stayst, fadeoutst });
			        sendPacket(p, subtitlePacket);

			        e = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("SUBTITLE").get((Object)null);
			        subtitlePacket = packetPlayOutTitleConstructorNormal.newInstance(new Object[] { e, chatSubtitle, fadeinst, stayst, fadeoutst });
			        sendPacket(p, subtitlePacket);
				}
	    	}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void updateToMC17Classes() {
    	try {
    		packetClass = Class.forName("net.minecraft.network.protocol.Packet");
    						    	
	    	iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");

        	clientboundSetTitleTextPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket");
        	clientboundSetTitleTextPacketConstructor = clientboundSetTitleTextPacketClass.getConstructor(iChatBaseComponentClass);
        	clientboundSetSubtitleTextPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket");
        	clientboundSetSubtitleTextPacketConstructor = clientboundSetSubtitleTextPacketClass.getConstructor(iChatBaseComponentClass);
        	clientboundSetTitlesAnimationPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket");
        	clientboundSetTitlesAnimationPacketConstructor = clientboundSetTitlesAnimationPacketClass.getConstructor(new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE});
        	clientboundClearTitlesPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundClearTitlesPacket");
        	
		} catch (SecurityException | ClassNotFoundException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
	}

}
