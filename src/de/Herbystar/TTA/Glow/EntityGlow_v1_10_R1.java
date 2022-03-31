package de.Herbystar.TTA.Glow;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.Herbystar.TTA.Main;

public class EntityGlow_v1_10_R1 {
	
	Main plugin;
	public EntityGlow_v1_10_R1(Main main) {
		plugin = main;
	}
		
	//Item/Entity Glow
	@Deprecated
	public void addEntityGlow(Entity entity) {
		if(entity == null) {
			return;
		}
		if(((CraftEntity) entity).isGlowing() == true) {
			return;
		}
		((CraftEntity) entity).setGlowing(true);		
	}
	
	public void removeEntityGlow(Entity entity) {
		LivingEntity living = (LivingEntity) entity;
		if(entity == null) {
			return;
		}
		if(((CraftEntity) entity).isGlowing() == false && !living.hasPotionEffect(PotionEffectType.GLOWING)) {
			return;
		}
		if(((CraftEntity) entity).isGlowing() == true) {
			((CraftEntity) entity).setGlowing(false);
		}
		if(living.hasPotionEffect(PotionEffectType.GLOWING)) {
			living.removePotionEffect(PotionEffectType.GLOWING);
		}
	}
	
	public void addEntityGlow(Entity entity, ChatColor color) {
		LivingEntity living = (LivingEntity) entity;
		if(entity == null) {
			return;
		}
		if(((CraftEntity) entity).isGlowing() == true | living.hasPotionEffect(PotionEffectType.GLOWING)) {
			return;
		}
		GlowColor.setGlowScoreboard(living, color);
		living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false), true);
		
	}	

}
