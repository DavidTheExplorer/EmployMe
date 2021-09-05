package dte.employme.job.service;

import java.util.Optional;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface JobService 
{
	Inventory getContainerOf(Player player);
	
	Inventory getCreationInventory();
	
	Optional<Conversation> buildMoneyJobConversation(Player employer);
	Optional<Conversation> buildItemsJobConversation(Player employer);
}
