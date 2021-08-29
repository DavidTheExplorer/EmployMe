package com.pseudonova.employme.job.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SimpleJobService implements JobService
{
	private final Map<UUID, Inventory> playersContainers = new HashMap<>();
	
	@Override
	public Inventory getContainerOf(Player player) 
	{
		return this.playersContainers.get(player.getUniqueId());
	}
}