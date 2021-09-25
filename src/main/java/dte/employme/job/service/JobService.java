package dte.employme.job.service;

import java.util.Optional;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import dte.employme.job.Job;

public interface JobService 
{
	void onComplete(Job job, Player completer);
	boolean hasFinished(Job job, Player player);
	
	Inventory getCreationInventory(Player employer);
	Inventory getDeletionInventory(Player employer);
	
	Optional<Conversation> buildMoneyJobConversation(Player employer);
	Optional<Conversation> buildItemsJobConversation(Player employer);
}