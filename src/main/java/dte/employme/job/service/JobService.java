package dte.employme.job.service;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.Job;

public interface JobService 
{
	void onComplete(Job job, Player completer);
	boolean hasFinished(Job job, Player player);
	
	//menus
	Inventory getCreationInventory(Player employer);
	Inventory getDeletionInventory(Player employer);
	
	//containers
	Inventory getItemsContainer(UUID playerUUID);
	Inventory getRewardsContainer(UUID playerUUID);
	
	//conversations
	Conversation buildMoneyJobConversation(Player employer);
	Conversation buildItemsJobConversation(Player employer, Collection<ItemStack> offeredItems);
}