package dte.employme.job.service;

import java.util.Optional;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface JobService 
{
	Inventory getCreationInventory(Player employer);
	Inventory getDeletionInventory(Player employer);
	
	Optional<Conversation> buildMoneyJobConversation(Player employer);
	Optional<Conversation> buildItemsJobConversation(Player employer);
}