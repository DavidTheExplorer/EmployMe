package dte.employme.job.addnotifiers;

import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static org.bukkit.ChatColor.GRAY;

import java.util.Map;

import org.bukkit.entity.Player;

import dte.employme.job.Job;
import dte.employme.messages.MessageKey;
import dte.employme.messages.Placeholders;
import dte.employme.messages.service.MessageService;

public abstract class JobAddedChatNotifier extends AbstractJobAddedNotifier
{
	private final MessageService messageService;

	public JobAddedChatNotifier(String name, MessageService messageService) 
	{
		super(name);
		
		this.messageService = messageService;
	}
	
	@Override
	public void notify(Player player, Job job)
	{
		player.sendMessage(createSeparationLine(GRAY, 45));
		createMessages(player, job).forEach((messageKey, placeholders) -> player.sendMessage(Placeholders.apply(this.messageService.getGeneralMessage(messageKey), placeholders)));
		player.sendMessage(createSeparationLine(GRAY, 45));
	}
	
	protected abstract Map<MessageKey, Map<String, String>> createMessages(Player player, Job job);
}
