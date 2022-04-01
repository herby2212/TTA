package de.Herbystar.TTA.BossBar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.Herbystar.TTA.Utils.Position;
import de.Herbystar.TTA.Utils.Reflection;

public class NMS_BossBarWither {	
		
    private static Class<?> worldClass;
    private static Class<?> entityWitherClass;
    private static Class<?> entityLivingClass;
    private static Constructor<?> witherConstructor;
    private static Class<?> packetPlayOutSpawnEntityLivingClass;
    private static Constructor<?> packetPlayOutSpawnEntityLivingConstructor;
    
    private static Class<?> destroyPacketClass;
    private static Constructor<?> destroyPacketConstructor;
    
    private static Method setLocation;
    private static Method setCustomName;
    private static Method setHealth;
    private static Method setInvisible;
    
    private static Class<?> dataWatcher;
    private static Method getDatawatcher;
    private static Method a;
    private static Field d;
    
    private static HashMap<UUID, Object> playerWithers = new HashMap<UUID, Object>();
    private static HashMap<UUID, Object> destroyCache = new HashMap<UUID, Object>();
 
    static {    
        try {
        	worldClass = Reflection.getNMSClass("World");        	
        	entityWitherClass = Reflection.getNMSClass("EntityWither");
        	entityLivingClass = Reflection.getNMSClass("EntityLiving");
			witherConstructor = entityWitherClass.getConstructor(worldClass);
			
	        setLocation = entityWitherClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class); 
	        setCustomName = entityWitherClass.getMethod("setCustomName", new Class<?>[] { String.class });
	        setHealth = entityWitherClass.getMethod("setHealth", new Class<?>[] { float.class });
	        setInvisible = entityWitherClass.getMethod("setInvisible", new Class<?>[] { boolean.class });
	        	        
	        packetPlayOutSpawnEntityLivingClass = Reflection.getNMSClass("PacketPlayOutSpawnEntityLiving");
	        packetPlayOutSpawnEntityLivingConstructor = packetPlayOutSpawnEntityLivingClass.getConstructor(entityLivingClass);
	        
	        dataWatcher = Reflection.getNMSClass("DataWatcher");
	        getDatawatcher = entityWitherClass.getMethod("getDataWatcher");
	        a = dataWatcher.getMethod("a", int.class, Object.class);
	        d = dataWatcher.getDeclaredField("d");
            d.setAccessible(true);
	        
            destroyPacketClass = Reflection.getNMSClass("PacketPlayOutEntityDestroy");
            destroyPacketConstructor = destroyPacketClass.getConstructor(int[].class);
            
        } catch (NoSuchMethodException | SecurityException ex) {
            System.err.println("Error - Classes not initialized!");
            System.err.println("This BossBar class is designed for 1.8 use only!");
        } catch (NoSuchFieldException e) {
        	System.err.println("Error - Field of BossBar Class could not be found!");
			e.printStackTrace();
		}
    }
   
	 private static Object getWither(Player p) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
         if (playerWithers.containsKey(p.getUniqueId())) {
                 return playerWithers.get(p.getUniqueId());
         } else {
        	 Object nms_world = Reflection.getHandle(p.getWorld());
             playerWithers.put(p.getUniqueId(), witherConstructor.newInstance(nms_world));
             return getWither(p);
         }
	 }
    
    public static void setBossBar(Player player, String text, float health) {
		try {
			Location loc = Position.getPlayerLocWither(player);
			
			int xr = ThreadLocalRandom.current().nextInt(0,2);
    	    int xr2 = ThreadLocalRandom.current().nextInt(0,2);
	        
    	    Object wither = getWither(player);
	        
            setLocation.invoke(wither, loc.getX()+xr, loc.getY()-3, loc.getZ()+xr2, 0F, 0F);
	        setCustomName.invoke(wither, text);
	        //health hardcoded, else visible wither shield if health <= 50 //1.8 specific TODO
	        setHealth.invoke(wither, 100);
	        setInvisible.invoke(wither, true);
	        
	        changeWatcher(wither, text, health);
	        
	        Object packetObject = packetPlayOutSpawnEntityLivingConstructor.newInstance(wither);
	        
	        Field field = packetPlayOutSpawnEntityLivingClass.getDeclaredField("a");
            field.setAccessible(true);
            
            
			Field f = packetPlayOutSpawnEntityLivingClass.getDeclaredField("f");
	        f.setAccessible(true);
			f.set(packetObject, (byte) 0);
			
			Field g = packetPlayOutSpawnEntityLivingClass.getDeclaredField("g");
	        g.setAccessible(true);
			g.set(packetObject, (byte) 0);
			
			Field h = packetPlayOutSpawnEntityLivingClass.getDeclaredField("h");
	        h.setAccessible(true);
			h.set(packetObject, (byte) 0);
			
			Field i = packetPlayOutSpawnEntityLivingClass.getDeclaredField("i");
	        i.setAccessible(true);
			i.set(packetObject, (byte) 0);
			
			Field j = packetPlayOutSpawnEntityLivingClass.getDeclaredField("j");
	        j.setAccessible(true);
			j.set(packetObject, (byte) 0);
			
			Field k = packetPlayOutSpawnEntityLivingClass.getDeclaredField("k");
	        k.setAccessible(true);
			k.set(packetObject, (byte) 0);
            
            
            destroyCache.put(player.getUniqueId(), getDestroyPacket(new int[] { (int) field.get(packetObject) }));
	        
	        Reflection.sendPacket(player, packetObject);
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    private static void changeWatcher(Object nms_entity, String text, float health) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object nms_watcher = getDatawatcher.invoke(nms_entity);
        Map<?, ?> map = (Map<?, ?>) d.get(nms_watcher);
        map.remove(10);
        a.invoke(nms_watcher, 10, text);
        //a.invoke(nms_watcher, 11, (byte) 1);
        //a.invoke(nms_watcher, 0, (byte) 0x20); //Flags, 0x20 = invisible
		//a.invoke(nms_watcher, 6, health*3);
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
    		playerWithers.remove(player.getUniqueId());
    	}
    }
    
    public static boolean hasBossBar(Player player) {
    	if(playerWithers.containsKey(player.getUniqueId())) {
    		return true;
    	}
    	return false;
    }
}
