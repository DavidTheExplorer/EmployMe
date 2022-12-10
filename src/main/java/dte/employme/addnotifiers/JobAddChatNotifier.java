package dte.employme.addnotifiers;

import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static org.bukkit.ChatColor.GRAY;

import java.util.List;

import org.bukkit.entity.Player;

import dte.employme.job.Job;
import dte.employme.messages.MessageBuilder;
import dte.employme.services.message.MessageService;

public abstract class JobAddChatNotifier extends JobAddNotifier
{
	protected final MessageService messageService;

	protected JobAddChatNotifier(String name, MessageService messageService) 
	{
		super(name);
		
		this.messageService = messageService;
	}
	
	@Override
	public void notify(Player player, Job job)
	{
		player.sendMessage(createSeparationLine(GRAY, 45));
		createMessages(player, job).stream().forEach(builder -> builder.sendTo(player));
		player.sendMessage(createSeparationLine(GRAY, 45));
	}

	protected abstract List<MessageBuilder> createMessages(Player player, Job job);
}