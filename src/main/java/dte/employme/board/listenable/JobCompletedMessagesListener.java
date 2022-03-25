package dte.employme.board.listenable;

import static dte.employme.messages.MessageKey.GET;
import static dte.employme.messages.MessageKey.GOAL;
import static dte.employme.messages.MessageKey.ITEMS_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.JOB_COMPLETED;
import static dte.employme.messages.MessageKey.PLAYER_COMPLETED_YOUR_JOB;
import static dte.employme.messages.MessageKey.PREFIX;
import static dte.employme.messages.MessageKey.REWARD;
import static dte.employme.messages.Placeholders.COMPLETER;
import static dte.employme.utils.ChatColorUtils.colorize;
import static java.util.stream.Collectors.joining;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.board.listenable.ListenableJobBoard.JobCompleteListener;
import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.job.rewards.Reward;
import dte.employme.messages.MessageKey;
import dte.employme.services.message.MessageService;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.OfflinePlayerUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class JobCompletedMessagesListener implements JobCompleteListener
{
	private final MessageService messageService;
	
	public JobCompletedMessagesListener(MessageService messageService) 
	{
		this.messageService = messageService;
	}
	
	@Override
	public void onJobCompleted(JobBoard board, Job job, Player whoCompleted) 
	{
		this.messageService.getMessage((job.getReward() instanceof ItemsReward ? ITEMS_JOB_COMPLETED : JOB_COMPLETED))
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.sendTo(whoCompleted);

		OfflinePlayerUtils.ifOnline(job.getEmployer(), employer -> 
		{
			this.messageService.getMessage(PLAYER_COMPLETED_YOUR_JOB)
			.prefixed(this.messageService.getMessage(PREFIX).first())
			.inject(COMPLETER, whoCompleted.getName())
			.stream()
			.map(line -> new ComponentBuilder(line).event(new HoverEvent(Action.SHOW_TEXT, new Text(describe(job)))).create())
			.forEach(message -> employer.spigot().sendMessage(message));
		});
				
	}
	
	private String describe(Job job)
	{
		String goal = this.messageService.getMessage(GET).first() + " " + ItemStackUtils.describe(job.getGoal());
		
		return colorize(String.format("&6%s: &f%s &8&l| &6%s: &f%s", 
				this.messageService.getMessage(GOAL).first(),
				goal,
				this.messageService.getMessage(REWARD).first(),
				describe(job.getReward())));
	}
	
	private String describe(Reward reward) 
	{
		if(reward instanceof MoneyReward) 
		{
			String currencySymbol = this.messageService.getMessage(MessageKey.CURRENCY_SYMBOL).first();
			
			return String.format("%.2f%s", ((MoneyReward) reward).getPayment(), currencySymbol);
		}
		else if(reward instanceof ItemsReward) 
		{
			return ((ItemsReward) reward).getItems().stream()
					.map(ItemStackUtils::describe)
					.collect(joining(", "));
		}
		
		throw new IllegalArgumentException("Cannot describe the provided reward!");
	}
}