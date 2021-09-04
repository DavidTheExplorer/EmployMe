package dte.employme.job.service;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface JobService 
{
	Inventory getContainerOf(Player player);
}
