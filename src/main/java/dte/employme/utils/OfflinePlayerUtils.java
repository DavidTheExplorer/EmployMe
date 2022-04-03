package dte.employme.utils;

import java.util.function.Consumer;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class OfflinePlayerUtils 
{
	public static void ifOnline(OfflinePlayer offlinePlayer, Consumer<Player> playerAction) 
	{
		if(offlinePlayer.isOnline())
			playerAction.accept(offlinePlayer.getPlayer());
	}
}
