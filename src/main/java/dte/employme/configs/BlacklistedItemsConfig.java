package dte.employme.configs;

import static dte.spigotconfiguration.converter.ValueConverter.MATERIAL;

import org.bukkit.Material;
import org.bukkit.World;

import dte.employme.EmployMe;
import dte.spigotconfiguration.SpigotConfig;

public class BlacklistedItemsConfig extends SpigotConfig
{
	public BlacklistedItemsConfig() 
	{
		super(new Builder(EmployMe.getInstance())
				.fromInternalResource("blacklisted items"));
	}
	
	public boolean isBlacklistedAt(World world, Material material)
	{
		//check if the item is globally blacklisted
		if(getList("Blacklisted Items", MATERIAL).contains(material))
			return true;

		//check if the item is blacklisted at the specified world
		return getList("Worlds." + world.getName(), MATERIAL).contains(material);
	}
}