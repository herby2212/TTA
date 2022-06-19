package de.Herbystar.TTA.Utils;

import java.lang.reflect.Field;
import org.bukkit.entity.Player;

public class Ping {
	    
    private Object getNMSPlayer(Player player) {
    	try {
    		return player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return null;
    }
    
    /**
     * @deprecated
     * 
     * @param player
     * @return int
     */
	public int getPingV1(Player player) {
		try {
			int ping = 0;
			Object o = Reflection.getCraftClass("entity.CraftPlayer").cast(player);
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
	
	/**
	 * 
	 * @param player
	 * @return int
	 */
	public int getPingV2(Player player) {
		int ping = 0;
		Object nmsPlayer = getNMSPlayer(player);
		try {
			Field f;
			if(TTA_BukkitVersion.getVersionAsInt(2) >= 117) {
				f = nmsPlayer.getClass().getField("e");
			} else {
				f = nmsPlayer.getClass().getField("ping");
			}
			f.setAccessible(true);
			ping = f.getInt(nmsPlayer);
			return ping;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
