package dte.employme.board.listeners.completion;

import static dte.employme.messages.MessageKey.ITEMS_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.MONEY_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.PLAYER_COMPLETED_YOUR_JOB;
import static dte.employme.messages.MessageKey.PLAYER_PARTIALLY_COMPLETED_YOUR_JOB;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.job.Job;
import dte.employme.messages.MessageKey;
import dte.employme.rewards.ItemsReward;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.OfflinePlayerUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class JobCompletedMessagesListener implements JobCompleteListener
{
	private final MessageService messageService;
	private final JobService jobService;
	private final double percentageToNotifyFrom;
	
	public JobCompletedMessagesListener(MessageService messageService, JobService jobService, double percentageToNotifyFrom) 
	{
		this.messageService = messageService;
		this.jobService = jobService;
		this.percentageToNotifyFrom = percentageToNotifyFrom;
	}
	
	@Override
	public void onJobCompleted(Job job, Player whoCompleted, JobCompletionContext context)
	{
		//send a message to who completed
		this.messageService.getMessage(getCompleterMessage(context)).sendTo(whoCompleted);
		
		//notify the employer if the completion percentage is above the required
		if(context.isJobCompleted() || context.getPartialInfo().getPercentage() > this.percentageToNotifyFrom) 
		{
			OfflinePlayerUtils.ifOnline(job.getEmployer(), employer -> 
			{
				this.messageService.getMessage(getEmployerMessage(context))
				.inject("completer", whoCompleted.getName())
				.stream()
				.map(message -> displayHoverDescription(message, job, context))
				.forEach(employer.spigot()::sendMessage);
			});
		}	
	}
	
	private static MessageKey getCompleterMessage(JobCompletionContext context) 
	{
		return context.getReward() instanceof ItemsReward ? ITEMS_JOB_COMPLETED : MONEY_JOB_COMPLETED;
	}
	
	private static MessageKey getEmployerMessage(JobCompletionContext context) 
	{
		return context.isJobCompleted() ? PLAYER_COMPLETED_YOUR_JOB : PLAYER_PARTIALLY_COMPLETED_YOUR_JOB;
	}
	
	private BaseComponent[] displayHoverDescription(String message, Job job, JobCompletionContext context) 
	{
		return new ComponentBuilder(message)
				.event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(this.jobService.describeCompletionInGame(job, context)).create()))
				.create();
	}
}