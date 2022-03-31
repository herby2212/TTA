package de.Herbystar.TTA.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.Herbystar.TTA.Main;

public class PlayerJoinEventHandler implements Listener {
	
	Main plugin;
	public PlayerJoinEventHandler(Main main) {
		plugin = main;
	}
	
	@EventHandler
	public void OnPlayerJoinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.isOp()) {
			if(plugin.UpdateAviable == true) {
				p.sendMessage(plugin.prefix + "§a-=> Update is available! <=-");
			}
		}
	}

}
