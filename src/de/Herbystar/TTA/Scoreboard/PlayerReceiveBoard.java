package de.Herbystar.TTA.Scoreboard;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerReceiveBoard extends Event implements Cancellable {
	

	private HandlerList handlerlist = new HandlerList();
	private boolean cancelled;
	private Player player;
	private List<String> content;
	private List<String> title;
	private Scoreboards scoreboard;
	
	public PlayerReceiveBoard(Player player, List<String> content, List<String> title, Scoreboards scoreboard) {
		this.player = player;
		this.content = content;
		this.title = title;
		this.scoreboard = scoreboard;
	}

	public Player getPlayer() {
		return this.player;
	}
	
	  public List<String> getContent() {
		    return this.content;
		 }
	
	public List<String> getTitle() {
		return this.title;
	}
	
	public Scoreboards getBoard() {
		return this.scoreboard;
	}
	
	public List<String> setContent(List<String> content) {
		this.content = content;
		return content;
	}
	
	public List<String> setTitle(List<String> title) {
		this.title = title;
		return title;
	}
	
	public Scoreboards setBoard(Scoreboards board) {
		this.scoreboard = board;
		return board;
	}
	
	public HandlerList getHandlers() {
		return this.handlerlist;
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}				
}
