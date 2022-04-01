package de.Herbystar.TTA.Economy;

import java.util.UUID;

import org.bukkit.entity.Player;

public class TTA_Economy {
	
	@Deprecated
	public static void createAccount(Player player, double startBalance) {
		
	}
	
	public static void createAccount(UUID uuid, double startBalance) {
		
	}
	
	@Deprecated
	public static boolean hasAccount(Player player) {
		return false;		
	}
	
	public static boolean hasAccount(UUID uuid) {
		return false;
	}
	
	@Deprecated
	public static void transfer(Player fromPlayer, Player toPlayer, double amount) {
		
	}
	
	public static void transfer(UUID fromUUID, UUID toUUID, double amount) {
		
	}
	
	@Deprecated
	public static void deposit(Player player, double amount) {
		
	}
	
	public static void deposit(UUID uuid, double amount) {
		
	}
	
	@Deprecated
	public static void setBalance(Player player, double amount) {
		
	}
	
	public static void setBalance(UUID uuid, double amount) {
		
	}
	
	@Deprecated
	public static void take(Player player, double amount) {
		
	}
	
	public static void take(UUID uuid, double amount) {
		
	}
	
	@Deprecated
	public static void getBalance(Player player) {
		
	}
	
	public static void getBalance(UUID uuid) {
		
	}

}
