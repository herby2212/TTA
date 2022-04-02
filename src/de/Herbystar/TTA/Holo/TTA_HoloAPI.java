package de.Herbystar.TTA.Holo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import de.Herbystar.TTA.Utils.Reflection;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;

public class TTA_HoloAPI {
 
    private List<Object> destroyCache;
    private List<Object> spawnCache;
    private List<UUID> players;
    private List<String> lines;
    private Location loc;
 
    private static final double ABS = 0.23D;
    private static String path;
    private static String version;
    private static Class<?> iChatBaseComponentClass;
    private static Class<?> craftChatMessageClass;
    private static Class<?> armorStandClass;
    private static Class<?> worldClass;
    private static Class<?> entityClass;
    private static Class<?> craftWorldClass;
    private static Class<?> packetPlayOutSpawnEntityLivingClass;
    private static Class<?> entityLivingClass;
    private static Constructor<?> armorStandConstructor;
    private static Class<?> packetPlayOutEntityDestroyClass;
    private static Constructor<?> packetPlayOutEntityDestroyConstructor;
    private static Class<?> packetClass;
 
 
    static {
        path = Bukkit.getServer().getClass().getPackage().getName();
        version = path.substring(path.lastIndexOf(".")+1, path.length());
     
        try {
        	if(TTA_BukkitVersion.isVersion("1.17", 2)) {
        		updateToMC17Classes();
        	} else {
	        	craftChatMessageClass = Reflection.getCraftClass("util.CraftChatMessage");
	        	iChatBaseComponentClass = Reflection.getNMSClass("IChatBaseComponent");
	        	armorStandClass = Reflection.getNMSClass("EntityArmorStand");
	            worldClass = Reflection.getNMSClass("World");
	            entityClass = Reflection.getNMSClass("Entity");
	            craftWorldClass = Reflection.getCraftClass("CraftWorld");
	            packetPlayOutSpawnEntityLivingClass = Reflection.getNMSClass("PacketPlayOutSpawnEntityLiving");
	            entityLivingClass = Reflection.getNMSClass("EntityLiving");
	            packetPlayOutEntityDestroyClass = Reflection.getNMSClass("PacketPlayOutEntityDestroy");
	            packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(int[].class);
	           
	            packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
        	}
            if(TTA_BukkitVersion.getVersionAsInt(2) >= 114) {
            	armorStandConstructor = armorStandClass.getConstructor(new Class[] { worldClass, double.class, double.class, double.class });
            } else {
            	armorStandConstructor = armorStandClass.getConstructor(new Class[] { worldClass });
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
            System.err.println("Error - Classes not initialized!");
            System.err.println("Hologramm is not supported in 1.7!");
            ex.printStackTrace();
        }
    }
   
    public void createHolo(Location loc, List<String> lines) {
        this.lines = lines;
        this.loc = loc;
        this.players = new ArrayList<>();
        this.spawnCache = new ArrayList<>();
        this.destroyCache = new ArrayList<>();
       
        // Init
        Location displayLoc = loc.clone().add(0, (ABS * lines.size()) - 1.97D, 0);
        for (int i = 0; i < lines.size(); i++) {
        	if(TTA_BukkitVersion.getVersionAsInt(2) >= 115) {
        		return;
        		/*
            	this.createLine(this.loc.getWorld(), displayLoc.getX(), displayLoc.getY(), displayLoc.getZ(), this.lines.get(i));
            	displayLoc.add(0, ABS * (-1), 0);
            	*/
        	} else {
                Object packet = this.getPacket(this.loc.getWorld(), displayLoc.getX(), displayLoc.getY(), displayLoc.getZ(), this.lines.get(i));
                this.spawnCache.add(packet);
                try {
                    Field field = packetPlayOutSpawnEntityLivingClass.getDeclaredField("a");
                    field.setAccessible(true);
                    this.destroyCache.add(this.getDestroyPacket(new int[] { (int) field.get(packet) }));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                displayLoc.add(0, ABS * (-1), 0);
        	}
        }
    }
 
    public boolean displayHolo(Player p) {
        for (int i = 0; i < spawnCache.size(); i++) {
            this.sendPacket(p, spawnCache.get(i));
        }
     
        this.players.add(p.getUniqueId());
        return true;
    }
    
    public boolean destroyHolo(Player p) {
        if (this.players.contains(p.getUniqueId())) {
            for (int i = 0; i < this.destroyCache.size(); i++) {
                this.sendPacket(p, this.destroyCache.get(i));
            }
            this.players.remove(p.getUniqueId());
            return true;
        }
        return false;
    }
    
    private void createLine(World w, double x, double y, double z, String text) {
        ArmorStand a = (ArmorStand) w.spawnEntity(new Location(w, x, y, z), EntityType.ARMOR_STAND);
        a.setVisible(false);
        a.setCollidable(false);
        a.setCustomNameVisible(true);
        a.setGravity(false);
        a.setInvulnerable(true);
        a.setMarker(true);
        
        a.setCustomName(text);
    }
    
    private Object getPacket(World w, double x, double y, double z, String text) {
        try {
            Object craftWorldObj = craftWorldClass.cast(w);
            Method getHandleMethod = craftWorldObj.getClass().getMethod("getHandle", new Class<?>[0]);
            Object entityObject = null;
            if(TTA_BukkitVersion.getVersionAsInt(2) >= 114) {
            	entityObject = armorStandConstructor.newInstance(new Object[] { getHandleMethod.invoke(craftWorldObj, new Object[0]), 0.0, 0.0, 0.0 });
            } else {
                entityObject = armorStandConstructor.newInstance(new Object[] { getHandleMethod.invoke(craftWorldObj, new Object[0]) });
            }
            if(TTA_BukkitVersion.getVersionAsInt(2) >= 113) {
                Method setCustomName = entityObject.getClass().getMethod("setCustomName", new Class<?>[] { iChatBaseComponentClass });
                Method fromStringOrNull = craftChatMessageClass.getMethod("fromStringOrNull", new Class[] { String.class });
                setCustomName.invoke(entityObject, fromStringOrNull.invoke(craftChatMessageClass, new Object[] {text}));
            } else {
                Method setCustomName = entityObject.getClass().getMethod("setCustomName", new Class<?>[] { String.class });
                setCustomName.invoke(entityObject, new Object[] { text });
            }
            Method setCustomNameVisible = entityClass.getMethod("setCustomNameVisible", new Class[] { boolean.class });
            setCustomNameVisible.invoke(entityObject, new Object[] { true });
            Method setGravity = null;
            if(TTA_BukkitVersion.getVersionAsInt(2) >= 110) {
	            setGravity = entityObject.getClass().getMethod("setNoGravity", new Class<?>[] { boolean.class });
	            setGravity.invoke(entityObject, new Object[] { false });
			} else {
	            setGravity = entityObject.getClass().getMethod("setGravity", new Class<?>[] { boolean.class });
	            setGravity.invoke(entityObject, new Object[] { false });
			}
            Method setLocation = entityObject.getClass().getMethod("setLocation", new Class<?>[] { double.class, double.class, double.class, float.class, float.class });
            setLocation.invoke(entityObject, new Object[] { x, y, z, 0.0F, 0.0F });
            Method setInvisible = entityObject.getClass().getMethod("setInvisible", new Class<?>[] { boolean.class });
            setInvisible.invoke(entityObject, new Object[] { true });
            Constructor<?> cw = packetPlayOutSpawnEntityLivingClass.getConstructor(new Class<?>[] { entityLivingClass });
            Object packetObject = cw.newInstance(new Object[] { entityObject });
            return packetObject;         
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
   
    private Object getDestroyPacket(int... id) {
        try {
            return packetPlayOutEntityDestroyConstructor.newInstance(id);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
 
    /*
    private void sendPacket(Player p, Object packet) {
        try {
           Method getHandle = p.getClass().getMethod("getHandle");
           Object entityPlayer = getHandle.invoke(p);        
           Object pConnection;
           if(TTA_BukkitVersion.isVersion("1.17", 2)) {
        	   pConnection = entityPlayer.getClass().getField("b").get(entityPlayer);
			} else {
				pConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			}
           Method sendMethod = pConnection.getClass().getMethod("sendPacket", new Class[] { packetClass });
           sendMethod.invoke(pConnection, new Object[] { packet });
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    */
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
    
    private static void updateToMC17Classes() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
    	packetClass = Class.forName("net.minecraft.network.protocol.Packet");
    	craftChatMessageClass = Reflection.getCraftClass("util.CraftChatMessage");
    	iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
        armorStandClass = Class.forName("net.minecraft.world.entity.decoration.EntityArmorStand");
        worldClass = Class.forName("net.minecraft.world.level.World");
        entityClass = Class.forName("net.minecraft.world.entity.Entity");
        craftWorldClass = Reflection.getCraftClass("CraftWorld");
        packetPlayOutSpawnEntityLivingClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving");
        entityLivingClass = Class.forName("net.minecraft.world.entity.EntityLiving");
        packetPlayOutEntityDestroyClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
        packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(int[].class);
    }
    
    
    /*
     * Thanks to JanHektor for the basic Construct.
     */

}