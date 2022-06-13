package de.Herbystar.TTA.Tablist;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import de.Herbystar.TTA.Main;
import de.Herbystar.TTA.TTA_Methods;

public class AnimatedTablist {
	
	private HashMap<Player, Integer> scheduler = new HashMap<Player, Integer>();
	private HashMap<Integer, HashMap<Player, ATInstance>> animatedTablistsV2 = new HashMap<Integer, HashMap<Player, ATInstance>>();
	
	public void sendAnimatedTablist(Player player, List<String> headers, List<String> footers, int refreshRateInTicks) {
		ATInstance atinstance = null;
		
		//Check if the refreshRate changed if so remove the tablist from the old scheduler
		if(scheduler.containsKey(player)) {
			int oldRefreshRate = scheduler.get(player);
			if(oldRefreshRate != refreshRateInTicks) {
				animatedTablistsV2.get(oldRefreshRate).remove(player);
			}
		}
		
		if(animatedTablistsV2.containsKey(refreshRateInTicks)) {
			HashMap<Player, ATInstance> animatedTablists = animatedTablistsV2.get(refreshRateInTicks);
			if(animatedTablists.containsKey(player)) {
				atinstance = animatedTablists.get(player);
				atinstance.updateHeaderAndFooter(headers, footers);
			} else {
				atinstance = new ATInstance(player, headers, footers);
				animatedTablists.put(player, atinstance);
			}
		} else {
			atinstance = new ATInstance(player, headers, footers);
			HashMap<Player, ATInstance> animatedTablists = new HashMap<Player, ATInstance>();
			animatedTablists.put(player, atinstance);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					for(ATInstance atinstance : animatedTablistsV2.get(refreshRateInTicks).values()) {
						atinstance.next();
					}
				}
			}.runTaskTimerAsynchronously(Main.instance, 0L, refreshRateInTicks);
			
			animatedTablistsV2.put(refreshRateInTicks, animatedTablists);
		}
		scheduler.put(player, refreshRateInTicks);
	}


	protected class ATInstance {
		private int headerNumber = 0;
		private int footerNumber = 0;
		
		private Player player;
		
		private List<String> headers;
		private List<String> footers;
		
		protected ATInstance(Player player, List<String> headers, List<String> footers) {
			this.player = player;
			this.headers = headers;
			this.footers = footers;
		}
		
		protected void next() {
			if(this.headerNumber > this.headers.size()) {
				this.headerNumber = 1;
			}
			if(this.footerNumber > this.footers.size()) {
				this.footerNumber = 1;
			}
			try {
				String header = this.headers.get(this.headerNumber - 1);
				String footer = this.footers.get(this.footerNumber - 1);						
				
				TTA_Methods.sendTablist(this.player, header, footer);
			} catch(IndexOutOfBoundsException ex) {
				if(Main.instance.internalDebug == true) {
					Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + " §cTablist Error: IndexOutOfBoundsException.");
					ex.printStackTrace();
				}
			}

			this.headerNumber += 1;
			this.footerNumber += 1;	
		}
		
		protected void updateHeaderAndFooter(List<String> headers, List<String> footers) {
			this.headers = headers;
			this.footers = footers;
			this.reset();
		}
		
		protected void reset() {
			this.headerNumber = 0;
			this.footerNumber = 0;
		}
		
		protected Player getPlayer() {
			return this.player;
		}
	}
}
