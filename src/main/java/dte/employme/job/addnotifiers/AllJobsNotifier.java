package dte.employme.job.addnotifiers;

import static dte.employme.messages.MessageKey.NEW_JOB_POSTED;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import dte.employme.job.Job;
import dte.employme.messages.MessageBuilder;
import dte.employme.services.message.MessageService;

public class AllJobsNotifier extends JobAddChatNotifier
{
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
	protected List<MessageBuilder> createMessages(Player player, Job job) 
	{
		return Arrays.asList(this.messageService.getMessage(NEW_JOB_POSTED));
	}
}