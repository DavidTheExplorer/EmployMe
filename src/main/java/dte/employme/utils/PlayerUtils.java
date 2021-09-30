package dte.employme.utils;

import static org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import dte.employme.utils.java.ArrayUtils;

public class PlayerUtils
{
	//Container of static methods
	private PlayerUtils(){}

	private static final ItemStack AIR_ITEM = new ItemStack(Material.AIR);

	private static final String[] TON_OF_MESSAGES = Stream.generate(() -> " ")
			.limit(100)
			.toArray(String[]::new);

	public static void clearChat(Player player)
	{
		player.sendMessage(TON_OF_MESSAGES);
	}
	
	public static void giveOrDrop(Player player, ItemStack... items) 
	{
		Collection<ItemStack> remainingItems = player.getInventory().addItem(items).values();
		
		//drop nearby the player the items that couldn't be added
		remainingItems.forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
		
		player.updateInventory();
	}

	public boolean everJoined(UUID playerUUID)
	{
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
		
		return offlinePlayer.isOnline() ? true : offlinePlayer.hasPlayedBefore();
	}
	
	public static void setArmor(Player player, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots)
	{
		PlayerInventory playerInventory = player.getInventory();
		
		playerInventory.setHelmet(defaultIfNull(helmet, AIR_ITEM));
		playerInventory.setChestplate(defaultIfNull(chestplate, AIR_ITEM));
		playerInventory.setLeggings(defaultIfNull(leggings, AIR_ITEM));
		playerInventory.setBoots(defaultIfNull(boots, AIR_ITEM));
	}

	public static boolean hasClearedInventory(Player player, boolean armorCheck)
	{
		if(armorCheck && !wearingArmor(player))
			return false;
		
		return ArrayUtils.isEmpty(player.getInventory().getContents());
	}

	public static boolean wearingArmor(Player player) 
	{
		return ArrayUtils.isEmpty(player.getInventory().getArmorContents());
	}
}