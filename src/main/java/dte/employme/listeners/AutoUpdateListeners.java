package dte.employme.listeners;

import static dte.employme.messages.MessageKey.NEW_UPDATE_AVAILABLE;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import dte.employme.messages.Placeholders;
import dte.employme.messages.service.MessageService;

public class AutoUpdateListeners implements Listener
{
	private final MessageService messageService;
	private final String newVersion;

	public AutoUpdateListeners(MessageService messageService, String newVersion) 
	{
		this.messageService = messageService;
		this.newVersion = newVersion;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) 
	{
		handle(event);
	}

	private void handle(PlayerEvent event)
	{
		Player player = event.getPlayer();
		
		if(!player.isOp())
			return;
		
		this.messageService.getMessage(NEW_UPDATE_AVAILABLE)
		.withGeneralPrefix()
		.inject(Placeholders.NEW_VERSION, this.newVersion)
		.sendTo(player);
	}
}