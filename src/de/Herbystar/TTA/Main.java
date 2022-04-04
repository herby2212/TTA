package de.Herbystar.TTA;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import de.Herbystar.TTA.Events.PlayerJoinEventHandler;
import de.Herbystar.TTA.Glow.GlowColor;
import de.Herbystar.TTA.Glow.ItemEnchant;
import de.Herbystar.TTA.Scoreboard.TTA_Scoreboards;
import de.Herbystar.TTA.BossBar.NMS_BossBar;
import de.Herbystar.TTA.Utils.TPS;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;

public class Main extends JavaPlugin {
	
	public String prefix = "§c[§6TTA§c] ";
	private String version;
	private Pattern Version_Pattern = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");
	public boolean UpdateAviable;
	public static Main instance;
	private boolean unsupportedVersion = false;
	public boolean internalDebug = false;
	public Team black;
	public HashMap<UUID, List<Object>> bossBarUpdater = new HashMap<UUID, List<Object>>();
	
	/**
	 * Scoreboard
	 */
	public static BukkitTask scoreboardcontent;
	public static BukkitTask scoreboardtitle;
	
	private int scoreboardcontentinterval = 20;
	private int scoreboardtitleinterval = 10;
	public static HashMap<Player, TTA_Scoreboards> boards = new HashMap<Player, TTA_Scoreboards>();
	public static List<TTA_Scoreboards> allboards = new ArrayList<TTA_Scoreboards>();

	
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
		ServerVersionHook();
		if(unsupportedVersion == true) {
			Bukkit.getServer().getConsoleSender().sendMessage("");
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§cMinecraft version currently not supported!");
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§cShutting now down!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		loadConfig();
		activateGlow();
		GlowColor.initializeColorScoreboard();
		startScoreboardsTitle();
//		startScoreboardsContent();
		/**
		 * Disabled as there is no updater currently included
		registerEvents();
		*/
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
	
	public void startScoreboardsTitle() {
		Main.scoreboardtitle = new BukkitRunnable() {
			
			@Override
			public void run() {
				for(TTA_Scoreboards p : boards.values()) {
					p.updateTitle();
				}
			}
		}.runTaskTimerAsynchronously(this, 0, scoreboardtitleinterval);	
	}
	
	public void startScoreboardsContent() {
		Main.scoreboardcontent = new BukkitRunnable() {
			
			@Override
			public void run() {
				try {
					for(TTA_Scoreboards p : boards.values()) {
						p.updateContent(null);
					}
				} catch(ConcurrentModificationException ex) {
					if(Main.instance.internalDebug == true) {
						ex.printStackTrace();
					}
				}

			}
		}.runTaskTimerAsynchronously(this, 0, scoreboardcontentinterval);
	}
		
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
	
	@SuppressWarnings("unused")
	private void registerEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(this), this);
	}	
	
	private void ServerVersionHook() {
		Bukkit.getConsoleSender().sendMessage(this.prefix + "§6Engine: §e" + TTA_BukkitVersion.getEngine());
		if(getServerVersion().contains("v1_6")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.6 Support §2enabled!");
			return;
		}
		if(getServerVersion().contains("v1_7")) {
			if(getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
				Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft Protocol Hack (1.7 & 1.8) Support §2enabled!");
			} else {
				Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.7 Support §2enabled!");
			}
			return;
		}
		if(getServerVersion().equalsIgnoreCase("v1_8_R1.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.8 Support §2enabled!");
			return;
		}
		if(getServerVersion().equalsIgnoreCase("v1_8_R2.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.8.3 Support §2enabled!");
			return;
		}
		if(getServerVersion().equalsIgnoreCase("v1_8_R3.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.8.4-1.8.9 Support §2enabled!");
			return;
		}
		if(getServerVersion().equalsIgnoreCase("v1_9_R1.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.9 Support §2enabled!");
			return;
		}
		if(getServerVersion().equalsIgnoreCase("v1_9_R2.")) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.9 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.10", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.10 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.11", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.11 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.12", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.12 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.13", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.13 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.14", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.14 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.15", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.15 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.16", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.16 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.17", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.17 Support §2enabled!");
			return;
		}
		if(TTA_BukkitVersion.isVersion("1.18", 2)) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.prefix + "§6Minecraft 1.18 Support §2enabled!");
			return;
		}
		
		this.unsupportedVersion = true;
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
		Bukkit.getConsoleSender().sendMessage("Engine: " + TTA_BukkitVersion.getEngine());
		Bukkit.getConsoleSender().sendMessage("Running version: " + TTA_BukkitVersion.getVersion());
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("Supported Methods:");
		Bukkit.getConsoleSender().sendMessage("");
		
		List<String> supMethods = new ArrayList<String>();
		//1.6 Base
		supMethods.add("getPing");
		supMethods.add("getTPS");
		supMethods.add("setBossBar");
		supMethods.add("hasBossBar");
		supMethods.add("removeBossBar");
		
		
		if(getServerVersion().equalsIgnoreCase("v1_7_R4.")) {
			supMethods.add("sendTitle");
		}		
		if(TTA_BukkitVersion.isVersion("1.8", 2)) {
			supMethods.add("sendTablist");
			supMethods.add("sendActionBar");
			supMethods.add("sendTitle");
			supMethods.add("createItemGlow");
			supMethods.add("createHolo");
			supMethods.add("setHoloPlayers");
			supMethods.add("removeHoloPlayers");
			supMethods.add("spawnHead");
			supMethods.add("GetSoundByName");
		}
		if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.9", "1.10", "1.11", "1.12"), 2)) {
			supMethods.remove("setBossBar");
			supMethods.add("sendTablist");
			supMethods.add("sendActionBar");
			supMethods.add("sendTitle");
			supMethods.add("createItemGlow");
			supMethods.add("addEntityGlow");
			supMethods.add("removeEntityGlow");
			supMethods.add("createHolo");
			supMethods.add("setHoloPlayers");
			supMethods.add("removeHoloPlayers");
			supMethods.add("spawnHead");
			supMethods.add("createBossBar");
			supMethods.add("setBarTitle");
			supMethods.add("setBarProgress");
			supMethods.add("setBarColor");
			supMethods.add("setBarStyle");
			supMethods.add("addBarFlag");
			supMethods.add("setBarVisibility");
			supMethods.add("removeBarFlag");
			supMethods.add("GetSoundByName");
		}	
		if(TTA_BukkitVersion.isVersion("1.13", 2)) {
			supMethods.remove("setBossBar");
			supMethods.add("sendTablist");
			supMethods.add("sendActionBar");
			supMethods.add("sendTitle");
			supMethods.add("addEntityGlow");
			supMethods.add("removeEntityGlow");
			supMethods.add("spawnHead");
			supMethods.add("createBossBar");
			supMethods.add("setBarTitle");
			supMethods.add("setBarProgress");
			supMethods.add("setBarColor");
			supMethods.add("setBarStyle");
			supMethods.add("addBarFlag");
			supMethods.add("setBarVisibility");
			supMethods.add("removeBarFlag");
			supMethods.add("GetSoundByName");
			supMethods.add("createHolo");
			supMethods.add("setHoloPlayers");
			supMethods.add("removeHoloPlayers");
		}
		if(TTA_BukkitVersion.isVersion("1.14", 2)) {
			supMethods.remove("setBossBar");
			supMethods.add("sendTablist");
			supMethods.add("sendActionBar");
			supMethods.add("sendTitle");
			supMethods.add("createBossBar");
			supMethods.add("setBarTitle");
			supMethods.add("setBarProgress");
			supMethods.add("setBarColor");
			supMethods.add("setBarStyle");
			supMethods.add("addBarFlag");
			supMethods.add("setBarVisibility");
			supMethods.add("removeBarFlag");
			supMethods.add("GetSoundByName");
			supMethods.add("createHolo");
			supMethods.add("setHoloPlayers");
			supMethods.add("removeHoloPlayers");
		}
		if(TTA_BukkitVersion.matchVersion(Arrays.asList("1.15", "1.16", "1.17", "1.18"), 2)) {
			supMethods.remove("setBossBar");
			supMethods.add("sendTablist");
			supMethods.add("sendActionBar");
			supMethods.add("sendTitle");
			supMethods.add("createBossBar");
			supMethods.add("setBarTitle");
			supMethods.add("setBarProgress");
			supMethods.add("setBarColor");
			supMethods.add("setBarStyle");
			supMethods.add("addBarFlag");
			supMethods.add("setBarVisibility");
			supMethods.add("removeBarFlag");
			supMethods.add("GetSoundByName");
		}
		for(String method : supMethods) {
			Bukkit.getConsoleSender().sendMessage(method);
		}
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("TTA Debug Mode finished!");
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("");

	}

}
