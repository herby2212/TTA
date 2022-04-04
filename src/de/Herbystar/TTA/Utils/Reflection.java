package de.Herbystar.TTA.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Reflection {
	
	private static Class<?> packetClass;
	
	static {    
        try {
        	if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.17", "1.18"), 2)) {
        		packetClass = Class.forName("net.minecraft.network.protocol.Packet");
        	} else {
            	packetClass = getNMSClass("Packet");
        	}
        } catch (SecurityException | ClassNotFoundException ex) {
            System.err.println("Error - Classes not initialized!");
			ex.printStackTrace();
        }
    }
	
	/*
	 * get a class from net.minecraft.server
	 * 
	 * @name: the name of the class you want to get
	 * @returns: the class if it exists, else null
	 */
	public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
        String className = "net.minecraft.server." + version + "." + name;
        Class<?> c = null;
        
        try {
            c = Class.forName(className);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return c;
    }
	
	/*
	 * get a class from craftbukkit
	 * 
	 * @name: the name of the class you want to get
	 * @returns: the class if it exists, else null
	 */
	public static Class<?> getCraftClass(String name) {
		String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
		String className = "org.bukkit.craftbukkit." + version + "." + name;
		Class<?> c = null;
		
		try {
			c = Class.forName(className);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	/*
	 * send a package to a list of players
	 * 
	 * @players: the players you want to send it to
	 * @packet: the packet you want to send
	 */
	// arraylists are faster when using for(...)
    public static void sendPacket(ArrayList<Player> players, Object packet) {
        for(Player p : players) {
            sendPacket(p , packet);
        }
    }
    
    public static void sendPacket(Player player, Object packet) {
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
   
    /*
     * sender a package to a player
     * 
     * @p: the player you want to send it to
     * @packet: the packet you want to send
     */
    /*
    public static void sendPacket(Player p, Object packet) {
        try {
        	// get EntityPlayer
            Object nmsPlayer = getHandle(p);
            // get the playerConnection field
            Field conField = nmsPlayer.getClass().getField("playerConnection");
            // get the value of the field
            Object playerCon = conField.get(nmsPlayer);
            // get the sendPacket method
            Method sendMethod = getMethod(playerCon.getClass(), "sendPacket");
            // invoke the method from playerConnection
            sendMethod.invoke(playerCon, packet);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
    }
    */
   
    /*
     * get the entity nms class
     * 
     * @entity: the entity you want to get the nsm class of
     * @returns: the nms entity class of the input entity
     */
    public static Object getHandle(Entity entity) {
        Object nmsEntity = null;
        Method entityGetHandle = getMethod(entity.getClass(), "getHandle");
    
        try {
            nmsEntity = entityGetHandle.invoke(entity);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        return nmsEntity;
    }
   
    /*
     * get the world nms class
     * 
     * @world: the world you want to get the nsm class of
     * @returns: the nms world class of the input world
     */
    public static Object getHandle(World world) {
        Object nmsWntity = null;
        Method worldGetHandle = getMethod(world.getClass(), "getHandle");
     
        try {
            nmsWntity = worldGetHandle.invoke(world);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        return nmsWntity;
    }
   
    /*
     * get a field in a class
     * 
     * @cl: the class you want to get a field of
     * @field: the name of the field you want to get
     * @returns: the field if it exists, else null
     */
    public static Field getField(Class<?> cl, String field) {
        try {
            Field f = cl.getDeclaredField(field);
            return f;
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    /*
     * get a method in a class
     * 
     * @cl: the class you want to get a method of
     * @method: the name of the method you want to get
     * @args: the arguments the method uses
     * @returns: the method if it exists, else null
     */
    public static Method getMethod(Class<?> cl, String method, Class<?>[] args) {
        for(Method m : cl.getMethods()) {
            if(m.getName().equals(method) && classListsEqual(args, m.getParameterTypes())) {
                return m;
            }
        }
        return null;
    }
   
    /*
     * get a method in a class
     * 
     * @cl: the class you want to get a method of
     * @method: the name of the method you want to get
     * @returns: the method if it exists, else null
     */
    public static Method getMethod(Class<?> cl, String method) {
        for(Method m : cl.getMethods()) {
            if(m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }
    
    /*
     * check if two classlists are equal (e.g. class arguments of two methods)
     * 
     * @list1: the first list
     * @list2: the second list
     * @returns: true if they're equal, else false
     */
    public static boolean classListsEqual(Class<?>[] list1, Class<?>[] list2) {
        if(list1.length != list2.length) {
            return false;
        }
        
        for(int i = 0; i < list1.length; i++) {
            if(list1[i] != list2[i]) {
                return false;
            }
        }
        return true;
    }
}