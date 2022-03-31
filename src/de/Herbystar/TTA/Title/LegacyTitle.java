package de.Herbystar.TTA.Title;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.server.v1_7_R4.ChatSerializer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;
import org.spigotmc.ProtocolInjector.PacketTitle;

import de.Herbystar.TTA.Main;

public class LegacyTitle {
	
	Main plugin;
	public LegacyTitle(Main main) {
		plugin = main;
	}
	
	private static int VERSION = 47;

	public void sendTitle(Player p, String title) {
		if (!(((CraftPlayer)p).getHandle().playerConnection.networkManager.getVersion() >= VERSION)) return;
	    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.TITLE, ChatSerializer.a("{\"text\": \"\"}").a(title)));
	}

	public void sendSubTitle(Player p, String subtitle) {
		if (!(((CraftPlayer)p).getHandle().playerConnection.networkManager.getVersion() >= VERSION)) return;
	    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.SUBTITLE, ChatSerializer.a("{\"text\": \"\"}").a(subtitle)));
	}

	public void sendTimings(Player p, int fadeIn, int stay, int fadeOut) {
		if (!(((CraftPlayer)p).getHandle().playerConnection.networkManager.getVersion() >= VERSION)) return;
		try {
			final Object handle = getHandle(p);
			final Object connection = getField(handle.getClass(), "playerConnection").get(handle);
			Object packet = PacketTitle.class.getConstructor(PacketTitle.Action.class, int.class, int.class, int.class).newInstance(PacketTitle.Action.TIMES, fadeIn, stay, fadeOut);
			getMethod(connection.getClass(), "sendPacket").invoke(connection, packet);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;
		if (l1.length != l2.length) {
			return false;
		}
		for (int i = 0; i < l1.length; i++) {
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		}
		return equal;
	}
	
	private Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Method getMethod(Class<?> clazz, String name, Class<?>... args) {
		for (Method m : clazz.getMethods()) {
			if ((m.getName().equals(name)) && ((args.length == 0) || (ClassListEqual(args, m.getParameterTypes())))) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}
	
	private Object getHandle(Object obj) {
		try {
			return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void reset(Player p) {
		if (!(((CraftPlayer)p).getHandle().playerConnection.networkManager.getVersion() >= VERSION)) return;
	    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.RESET));
	}

	public void clear(Player p) {
		if (!(((CraftPlayer)p).getHandle().playerConnection.networkManager.getVersion() >= VERSION)) return;
	    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.CLEAR));
	}
}
