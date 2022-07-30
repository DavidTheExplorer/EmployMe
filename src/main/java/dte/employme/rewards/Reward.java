package dte.employme.rewards;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface Reward extends ConfigurationSerializable
{
	void giveTo(OfflinePlayer offlinePlayer);
	
	String getDescription();
}