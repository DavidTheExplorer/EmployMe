package dte.employme.board.listeners.completion;

import static dte.employme.messages.MessageKey.ITEMS_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.MONEY_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.PLAYER_COMPLETED_YOUR_JOB;
import static dte.employme.messages.Placeholders.COMPLETER;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.messages.MessageKey;
import dte.employme.rewards.ItemsReward;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.OfflinePlayerUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class JobCompletedMessagesListener implements JobCompleteListener
{
	private final MessageService messageService;
	private final JobService jobService;
	
	public JobCompletedMessagesListener(MessageService messageService, JobService jobService) 
	{
		this.messageService = messageService;
		this.jobService = jobService;
	}
	
	@Override
	public void onJobCompleted(JobBoard board, Job job, Player whoCompleted) 
	{
		//send a message to who completed
		this.messageService.getMessage(getRewardMessage(job)).sendTo(whoCompleted);
		
		//notify the employer
		OfflinePlayerUtils.ifOnline(job.getEmployer(), employer -> 
		{
			this.messageService.getMessage(PLAYER_COMPLETED_YOUR_JOB)
			.inject(COMPLETER, whoCompleted.getName())
			.stream()
			.map(line -> new ComponentBuilder(line).event(new HoverEvent(Action.SHOW_TEXT, new Text( this.jobService.describeInGame(job)))).create())
			.forEach(message -> employer.spigot().sendMessage(message));
		});
	}
	
	private static MessageKey getRewardMessage(Job job) 
	{
		return job.getReward() instanceof ItemsReward ? ITEMS_JOB_COMPLETED : MONEY_JOB_COMPLETED;
	}
}