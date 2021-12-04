package dte.employme.job.addnotifiers;

import static dte.employme.messages.MessageKey.NEW_JOB_POSTED;

import java.util.Map;

import org.bukkit.entity.Player;

import dte.employme.job.Job;
import dte.employme.messages.MessageKey;
import dte.employme.messages.Placeholders;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.java.MapBuilder;

public class AllJobsNotifier extends JobAddedChatNotifier
{
	private static final Map<MessageKey, Placeholders> MESSAGES = new MapBuilder<MessageKey, Placeholders>()
			.put(NEW_JOB_POSTED, Placeholders.NONE)
			.build();
	
	public AllJobsNotifier(MessageService messageService)
	{
		super("All Jobs", messageService);
	}
	
	@Override
	public boolean shouldNotify(Player player, Job job) 
	{
		return true;
	}
	
	@Override
	protected Map<MessageKey, Placeholders> createMessages(Player player, Job job) 
	{
		return MESSAGES;
	}
}

