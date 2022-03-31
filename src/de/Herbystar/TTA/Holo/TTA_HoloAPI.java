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
import org.bukkit.entity.Player;
import de.Herbystar.TTA.Utils.Reflection;

public class TTA_HoloAPI {
 
    private List<Object> destroyCache;
    private List<Object> spawnCache;
    private List<UUID> players;
    private List<String> lines;
    private Location loc;
 
    private static final double ABS = 0.23D;
    private static String path;
    private static String version;
    private static Class<?> IChatBaseComponent;
    private static Class<?> CraftChatMessage;
    private static Class<?> armorStand;
    private static Class<?> worldClass;
    private static Class<?> nmsEntity;
    private static Class<?> craftWorld;
    private static Class<?> packetClass;
    private static Class<?> entityLivingClass;
    private static Constructor<?> armorStandConstructor;
    private static Class<?> destroyPacketClass;
    private static Constructor<?> destroyPacketConstructor;
    private static Class<?> nmsPacket;
 
 
    static {
        path = Bukkit.getServer().getClass().getPackage().getName();
        version = path.substring(path.lastIndexOf(".")+1, path.length());
     
        try {
        	CraftChatMessage = Reflection.getCraftClass("util.CraftChatMessage");
        	IChatBaseComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
            armorStand = Class.forName("net.minecraft.server." + version + ".EntityArmorStand");
            worldClass = Class.forName("net.minecraft.server." + version + ".World");
            nmsEntity = Class.forName("net.minecraft.server." + version + ".Entity");
            craftWorld = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
            packetClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutSpawnEntityLiving");
            entityLivingClass = Class.forName("net.minecraft.server." + version + ".EntityLiving");
            if(Bukkit.getVersion().contains("1.14") | Bukkit.getVersion().contains("1.15") | Bukkit.getVersion().contains("1.16")) {
            	armorStandConstructor = armorStand.getConstructor(new Class[] { worldClass, double.class, double.class, double.class });
            } else {
            	armorStandConstructor = armorStand.getConstructor(new Class[] { worldClass });
            }
            
         
            destroyPacketClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityDestroy");
            destroyPacketConstructor = destroyPacketClass.getConstructor(int[].class);
           
            nmsPacket = Class.forName("net.minecraft.server." + version + ".Packet");
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
            Object packet = this.getPacket(this.loc.getWorld(), displayLoc.getX(), displayLoc.getY(), displayLoc.getZ(), this.lines.get(i));
            this.spawnCache.add(packet);
            try {
                Field field = packetClass.getDeclaredField("a");
                field.setAccessible(true);
                this.destroyCache.add(this.getDestroyPacket(new int[] { (int) field.get(packet) }));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            displayLoc.add(0, ABS * (-1), 0);
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
 
    private Object getPacket(World w, double x, double y, double z, String text) {
        try {
            Object craftWorldObj = craftWorld.cast(w);
            Method getHandleMethod = craftWorldObj.getClass().getMethod("getHandle", new Class<?>[0]);
            Object entityObject = null;
            if(Bukkit.getVersion().contains("1.14") | Bukkit.getVersion().contains("1.15") | Bukkit.getVersion().contains("1.16")) {
            	entityObject = armorStandConstructor.newInstance(new Object[] { getHandleMethod.invoke(craftWorldObj, new Object[0]), 0.0, 0.0, 0.0 });
            } else {
                entityObject = armorStandConstructor.newInstance(new Object[] { getHandleMethod.invoke(craftWorldObj, new Object[0]) });
            }
            if(Bukkit.getVersion().contains("1.13") | Bukkit.getVersion().contains("1.14") | Bukkit.getVersion().contains("1.15") | Bukkit.getVersion().contains("1.16")) {
                Method setCustomName = entityObject.getClass().getMethod("setCustomName", new Class<?>[] { IChatBaseComponent });
                Method fromStringOrNull = CraftChatMessage.getMethod("fromStringOrNull", new Class[] { String.class });
                setCustomName.invoke(entityObject, fromStringOrNull.invoke(CraftChatMessage, new Object[] {text}));
            } else {
                Method setCustomName = entityObject.getClass().getMethod("setCustomName", new Class<?>[] { String.class });
                setCustomName.invoke(entityObject, new Object[] { text });
            }
            Method setCustomNameVisible = nmsEntity.getMethod("setCustomNameVisible", new Class[] { boolean.class });
            setCustomNameVisible.invoke(entityObject, new Object[] { true });
            Method setGravity = null;
            if(Bukkit.getVersion().contains("1.10") | Bukkit.getVersion().contains("1.11") | Bukkit.getVersion().contains("1.12") | Bukkit.getVersion().contains("1.13") | Bukkit.getVersion().contains("1.14") | Bukkit.getVersion().contains("1.15") | Bukkit.getVersion().contains("1.16")) {
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
            Constructor<?> cw = packetClass.getConstructor(new Class<?>[] { entityLivingClass });
            Object packetObject = cw.newInstance(new Object[] { entityObject });
            return packetObject;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
   
    private Object getDestroyPacket(int... id) {
        try {
            return destroyPacketConstructor.newInstance(id);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
 
    private void sendPacket(Player p, Object packet) {
        try {
           Method getHandle = p.getClass().getMethod("getHandle");
           Object entityPlayer = getHandle.invoke(p);
           Object pConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
           Method sendMethod = pConnection.getClass().getMethod("sendPacket", new Class[] { nmsPacket });
           sendMethod.invoke(pConnection, new Object[] { packet });
        } catch (Exception e) {
           e.printStackTrace();
        }
     }
    
    
    /*
     * Thanks to JanHektor for the basic Construct.
     */

}