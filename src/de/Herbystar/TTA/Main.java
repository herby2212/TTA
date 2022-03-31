package de.Herbystar.TTA;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import de.Herbystar.TTA.Events.PlayerJoinEventHandler;
import de.Herbystar.TTA.Glow.GlowColor;
import de.Herbystar.TTA.Glow.ItemEnchant;
import de.Herbystar.TTA.BossBar.NMS_BossBar;
import de.Herbystar.TTA.Utils.TPS;

public class Main extends JavaPlugin {
	
	public String prefix = "§c[§6TTA§c] ";
	private String version;
	private Pattern Version_Pattern = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");
	public boolean UpdateAviable;
	public static Main instance;
	public Team black;
	public HashMap<UUID, List<Object>> bossBarUpdater = new HashMap<UUID, List<Object>>();
	
	public void onEnable() {
		instance = this;
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("§c=========>[§6TTA Terms§c]<=========");		
		Bukkit.getConsoleSender().sendMessage("§c-> You are not permitted to claim this plugin as your own!");
		Bukkit.getConsoleSender().sendMessage("§c-> You are not permitted to decompiling the plugin's sourcecode!");
		Bukkit.getConsoleSender().sendMessage("§c-> You are not permitted to modify the code or the plugin and call it your own!");
		Bukkit.getConsoleSender().sendMessage("§c-> You are not permitted to redistributing this plugin as your own!");
		Bukkit.getConsoleSender().sendMessage("§c======>[§aTerms Accepted!§c]<======");
		Bukkit.getConsoleSender().sendMessage("");
		loadConfig();
		activateGlow();
		GlowColor.initializeColorScoreboard();
		registerEvents();
		ServerVersionHook();
		//StartMetrics();
		startBossBarUpdater();
		Bukkit.getConsoleSender().sendMessage(this.prefix + "§3Version: " + this.getDescription().getVersion() + " §2by " + "§4" + this.getDescription().getAuthors() + "§2 enabled!");

		if(this.getConfig().getBoolean("debug") == true) {
			Debug();
		}
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPS(), 100L, 1L);		
	}
	
	public void onDisable() {
		GlowColor.unloadColorScoreboard();
		Bukkit.getConsoleSender().sendMessage(this.prefix + "§3Version: " + this.getDescription().getVersion() + " §2by " + "§4" + this.getDescription().getAuthors() + "§4 disabled!");
	}
	
	private void loadConfig() {
		this.getConfig().options().copyDefaults(true);
		saveConfig();		
	}
	
	private void startBossBarUpdater() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Player players : Bukkit.getOnlinePlayers()) {
					if(bossBarUpdater.containsKey(players.getUniqueId())) {
						if(NMS_BossBar.hasBossBar(players)) {
							NMS_BossBar.removeBossBar(players);
						}
						List<Object> data = bossBarUpdater.get(players.getUniqueId());
						String text = (String) data.get(0);
						float health = (float) data.get(1);
						NMS_BossBar.setBossBar(players, text, health);
					}
				}
			}
		}.runTaskTimer(this, 0, 40);
	}
	
	/*
	public void StartMetrics() {
		try {
			Metrics m = new Metrics(this);
			m.start();
			Bukkit.getConsoleSender().sendMessage(this.prefix + "§2Started §6Metrics §2successful!");
		} catch (IOException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(this.prefix + "§4Failed to start the §6Metrics§4!");
		}
	}
	*/
	
	public int getVersionNumber(String version) {
		return Integer.parseInt(version.replace(".", ""));
	}
	
	
	public String getServerVersion() {
		if(version != null) {
			return version;
		}
		String pkg = Bukkit.getServer().getClass().getPackage().getName();
		String version1 = pkg.substring(pkg.lastIndexOf(".") + 1);
		if(!Version_Pattern.matcher(version1).matches()) {
			version1 = "";
		}
		String version = version1;
				return version = !version.isEmpty() ? version + "." : ""; 
	}
	
	private void registerEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(this), this);
	}	
	
	private void ServerVersionHook() {
		Bukkit.getConsoleSender().sendMessage(this.prefix + "§6Engine: §e" + TTA_Methods.getEngine());
		if(getServerVersion().contains("v1_6")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.6 Support §2enabled!");
		}
		if(getServerVersion().contains("v1_7")) {
			if(getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
				Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft Protocol Hack (1.7 & 1.8) Support §2enabled!");
			} else {
				Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.7 Support §2enabled!");
			}
		}
		if(getServerVersion().equalsIgnoreCase("v1_8_R1.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.8 Support §2enabled!");
		}
		if(getServerVersion().equalsIgnoreCase("v1_8_R2.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.8.3 Support §2enabled!");
		}
		if(getServerVersion().equalsIgnoreCase("v1_8_R3.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.8.4-1.8.9 Support §2enabled!");
		}
		if(getServerVersion().equalsIgnoreCase("v1_9_R1.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.9 Support §2enabled!");
		}
		if(getServerVersion().equalsIgnoreCase("v1_9_R2.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.9 Support §2enabled!");
		}
		if(Bukkit.getVersion().contains("1.10")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.10 Support §2enabled!");
		}
		if(Bukkit.getVersion().contains("1.11")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.11 Support §2enabled!");
		}
		if(Bukkit.getVersion().contains("1.12")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.12 Support §2enabled!");
		}
		if(Bukkit.getVersion().contains("1.13")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.13 Support §2enabled!");
		}
		if(Bukkit.getVersion().contains("1.14")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.14 Support §2enabled!");
		}
		if(Bukkit.getVersion().contains("1.15")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.15 Support §2enabled!");
		}
		if(Bukkit.getVersion().contains("1.16")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.16 Support §2enabled!");
		}
	}
	
	private void activateGlow() {
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			ItemEnchant glow = new ItemEnchant(70);
			Enchantment.registerEnchantment(glow);
		} catch(IllegalArgumentException e) {
			
		} catch(Exception e) {
			e.printStackTrace();
		} catch(NoSuchMethodError e) {
			
		}
	}
	
	public void Debug() {
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("TTA Debug Mode started!");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("Engine: " + TTA_Methods.getEngine());
		Bukkit.getConsoleSender().sendMessage("Running version: " + Bukkit.getServer().getBukkitVersion());
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("Supported Methods:");
		Bukkit.getConsoleSender().sendMessage("");
		if(getServerVersion().contains("v1_6")) {
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("setBossBar");
			Bukkit.getConsoleSender().sendMessage("hasBossBar");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
		}
		if(getServerVersion().contains("v1_7")) {
			if(getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
				Bukkit.getConsoleSender().sendMessage("sendTitle");
				Bukkit.getConsoleSender().sendMessage("getPing");
				Bukkit.getConsoleSender().sendMessage("getTPS");
				Bukkit.getConsoleSender().sendMessage("setBossBar");
				Bukkit.getConsoleSender().sendMessage("hasBossBar");
				Bukkit.getConsoleSender().sendMessage("removeBossBar");
			} else {
				Bukkit.getConsoleSender().sendMessage("getPing");
				Bukkit.getConsoleSender().sendMessage("getTPS");
				Bukkit.getConsoleSender().sendMessage("setBossBar");
				Bukkit.getConsoleSender().sendMessage("hasBossBar");
				Bukkit.getConsoleSender().sendMessage("removeBossBar");
			}
		}		
		if(getServerVersion().equalsIgnoreCase("v1_8_R1.")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("createItemGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
			Bukkit.getConsoleSender().sendMessage("setBossBar");
			Bukkit.getConsoleSender().sendMessage("hasBossBar");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
		}
		if(getServerVersion().equalsIgnoreCase("v1_8_R2.")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("createItemGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
			Bukkit.getConsoleSender().sendMessage("setBossBar");
			Bukkit.getConsoleSender().sendMessage("hasBossBar");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
		}
		if(getServerVersion().equalsIgnoreCase("v1_8_R3.")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("createItemGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
			Bukkit.getConsoleSender().sendMessage("setBossBar");
			Bukkit.getConsoleSender().sendMessage("hasBossBar");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
		}
		if(getServerVersion().equalsIgnoreCase("v1_9_R1.")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("createItemGlow");
			Bukkit.getConsoleSender().sendMessage("addEntityGlow");
			Bukkit.getConsoleSender().sendMessage("removeEntityGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");			
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
		}
		if(getServerVersion().equalsIgnoreCase("v1_9_R2.")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("createItemGlow");
			Bukkit.getConsoleSender().sendMessage("addEntityGlow");
			Bukkit.getConsoleSender().sendMessage("removeEntityGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
		}
		if(Bukkit.getVersion().contains("1.10")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("createItemGlow");
			Bukkit.getConsoleSender().sendMessage("addEntityGlow");
			Bukkit.getConsoleSender().sendMessage("removeEntityGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
		}
		if(Bukkit.getVersion().contains("1.11")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("createItemGlow");
			Bukkit.getConsoleSender().sendMessage("addEntityGlow");
			Bukkit.getConsoleSender().sendMessage("removeEntityGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
		}
		if(Bukkit.getVersion().contains("1.12")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("createItemGlow");
			Bukkit.getConsoleSender().sendMessage("addEntityGlow");
			Bukkit.getConsoleSender().sendMessage("removeEntityGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
		}		
		if(Bukkit.getVersion().contains("1.13")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("addEntityGlow");
			Bukkit.getConsoleSender().sendMessage("removeEntityGlow");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
		}
		if(Bukkit.getVersion().contains("1.14")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
		}
		if(Bukkit.getVersion().contains("1.15")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
		}
		if(Bukkit.getVersion().contains("1.16")) {
			Bukkit.getConsoleSender().sendMessage("sendTablist");
			Bukkit.getConsoleSender().sendMessage("sendActionBar");
			Bukkit.getConsoleSender().sendMessage("sendTitle");
			Bukkit.getConsoleSender().sendMessage("getPing");
			Bukkit.getConsoleSender().sendMessage("spawnHead");
			Bukkit.getConsoleSender().sendMessage("getTPS");
			Bukkit.getConsoleSender().sendMessage("createBossBar");
			Bukkit.getConsoleSender().sendMessage("setBarTitle");
			Bukkit.getConsoleSender().sendMessage("setBarProgress");
			Bukkit.getConsoleSender().sendMessage("setBarColor");
			Bukkit.getConsoleSender().sendMessage("setBarStyle");
			Bukkit.getConsoleSender().sendMessage("addBarFlag");
			Bukkit.getConsoleSender().sendMessage("setBarVisibility");
			Bukkit.getConsoleSender().sendMessage("removeBarFlag");
			Bukkit.getConsoleSender().sendMessage("removeBossBar");
			Bukkit.getConsoleSender().sendMessage("GetSoundByName");
			Bukkit.getConsoleSender().sendMessage("createHolo");
			Bukkit.getConsoleSender().sendMessage("setHoloPlayers");
			Bukkit.getConsoleSender().sendMessage("removeHoloPlayers");
		}
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("TTA Debug Mode finished!");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");

	}

}
