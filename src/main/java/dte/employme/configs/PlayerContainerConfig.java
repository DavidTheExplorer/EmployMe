package dte.employme.configs;

import org.bukkit.plugin.Plugin;

import dte.spigotconfiguration.SpigotConfig;
import dte.spigotconfiguration.exceptions.ConfigLoadException;

public class PlayerContainerConfig extends SpigotConfig
{
	public PlayerContainerConfig(Plugin plugin, String containerName) throws ConfigLoadException
	{
		super(new Builder(plugin)
				.byPath(String.format("containers/%s containers", containerName)));
	}

}
