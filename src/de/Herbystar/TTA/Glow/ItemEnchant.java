package de.Herbystar.TTA.Glow;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class ItemEnchant extends Enchantment {
		
	public ItemEnchant(int id) {
		super(id);
	}
	
	@Override
	public boolean canEnchantItem(ItemStack arg0) {
		return false;
	}
	
	@Override
	public boolean conflictsWith(Enchantment arg0) {
		return false;
	}
	
	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}
	
	@Override
	public int getMaxLevel() {
		return 10;
	}
	
	@Override
	public String getName() {
		return "Glow";
	}
	
	@Override
	public int getStartLevel() {
		return 1;
	}

}
