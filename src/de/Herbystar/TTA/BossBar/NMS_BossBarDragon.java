package de.Herbystar.TTA.BossBar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.Herbystar.TTA.Utils.Position;
import de.Herbystar.TTA.Utils.Reflection;

public class NMS_BossBarDragon {	
		
    private static Class<?> worldClass;
    private static Class<?> entityEnderDragonClass;
    private static Class<?> entityLivingClass;
    private static Constructor<?> enderDragonConstructor;
    private static Class<?> packetPlayOutSpawnEntityLivingClass;
    private static Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
    
    private static Class<?> destroyPacketClass;
    private static Constructor<?> destroyPacketConstructor;
    
    private static Method setLocation;
    private static Method setCustomName;
    private static Method setHealth;
    private static Method setInvisible;
    
    private static HashMap<UUID, Object> playerDragons = new HashMap<UUID, Object>();
    private static HashMap<UUID, Object> destroyCache = new HashMap<UUID, Object>();
 
    static {    
        try {
        	worldClass = Reflection.getNMSClass("World");        	
        	entityEnderDragonClass = Reflection.getNMSClass("EntityEnderDragon");
        	entityLivingClass = Reflection.getNMSClass("EntityLiving");
			enderDragonConstructor = entityEnderDragonClass.getConstructor(worldClass);
			
	        setLocation = entityEnderDragonClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class); 
	        setCustomName = entityEnderDragonClass.getMethod("setCustomName", new Class<?>[] { String.class });
	        setHealth = entityEnderDragonClass.getMethod("setHealth", new Class<?>[] { float.class });
	        setInvisible = entityEnderDragonClass.getMethod("setInvisible", new Class<?>[] { boolean.class });
	        	        
	        packetPlayOutSpawnEntityLivingClass = Reflection.getNMSClass("PacketPlayOutSpawnEntityLiving");
	        packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
	        
            destroyPacketClass = Reflection.getNMSClass("PacketPlayOutEntityDestroy");
            destroyPacketConstructor = destroyPacketClass.getConstructor(int[].class);
            
        } catch (NoSuchMethodException | SecurityException ex) {
            System.err.println("Error - Classes not initialized!");
            System.err.println("This BossBar class is designed for 1.8 use only!");
        }
    }
   
	 private static Object getEnderDragon(Player p) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
         if (playerDragons.containsKey(p.getUniqueId())) {
                 return playerDragons.get(p.getUniqueId());
         } else {
        	 Object nms_world = Reflection.getHandle(p.getWorld());
             playerDragons.put(p.getUniqueId(), enderDragonConstructor.newInstance(nms_world));
             return getEnderDragon(p);
         }
	 }
    
    public static void setBossBar(Player player, String text, float health) {
		try {
			Location loc = Position.getPlayerLocEnderDragon(player);
	        Object dragon = getEnderDragon(player);
	        
	        setLocation.invoke(dragon, loc.getX(), loc.getY() - 200, loc.getZ(), 0F, 0F);
            //setLocation.invoke(dragon, loc.getX(), loc.getY(), loc.getZ(), 0F, 0F);
	        setCustomName.invoke(dragon, text);
	        setHealth.invoke(dragon, (health * 2));
	        setInvisible.invoke(dragon, true);
	        Object packetObject = packetPlayOutSpawnEntityLivingConstructor.newInstance(dragon);
	        
	        Field field = packetPlayOutSpawnEntityLivingClass.getDeclaredField("a");
            field.setAccessible(true);
            destroyCache.put(player.getUniqueId(), getDestroyPacket(new int[] { (int) field.get(packetObject) }));
	        
	        Reflection.sendPacket(player, packetObject);
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    private static Object getDestroyPacket(int... id) {
        try {
            return destroyPacketConstructor.newInstance(id);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void removeBossBar(Player player) {
    	if(destroyCache.containsKey(player.getUniqueId())) {
    		Reflection.sendPacket(player, destroyCache.get(player.getUniqueId()));
    		playerDragons.remove(player.getUniqueId());
    	}
    }
    
    public static boolean hasBossBar(Player player) {
    	if(playerDragons.containsKey(player.getUniqueId())) {
    		return true;
    	}
    	return false;
    }
}
