package de.Herbystar.TTA.Utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Ping {
	
    public Class<?> getNMSClass(String name) {
    	String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    	try {
			return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public Object getNMSPlayer(Player player) {
    	try {
    		return player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return null;
    }
    
	public int getPingV1(Player p) {
		try {
			int ping = 0;
			Object o = getNMSClass("entity.CraftPlayer").cast(p);
			Object entityplayer = o.getClass().getMethod("getHandle", new Class[0]).invoke(o, new Object[0]);
			Field f = entityplayer.getClass().getField("ping");
			ping = (int) Math.round(f.getDouble(entityplayer));
			//ping = f.getInt(entityplayer);
			return ping;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int getPingV2(Player p) {
		int ping = 0;
		Object nmsPlayer = getNMSPlayer(p);
		try {
			Field f = nmsPlayer.getClass().getField("ping");
			f.setAccessible(true);
			ping = f.getInt(nmsPlayer);
			return ping;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
