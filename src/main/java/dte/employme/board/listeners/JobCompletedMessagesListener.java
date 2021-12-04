package dte.employme.board.listeners;

import static dte.employme.messages.MessageKey.ITEMS_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.JOB_COMPLETED;
import static dte.employme.messages.MessageKey.PLAYER_COMPLETED_YOUR_JOB;
import static dte.employme.messages.Placeholders.COMPLETER;
import static dte.employme.utils.ChatColorUtils.colorize;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.messages.Placeholders;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.OfflinePlayerUtils;
import dte.employme.visitors.reward.TextRewardDescriptor;
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
		this.messageService.sendGeneralMessage(whoCompleted, (job.getReward() instanceof ItemsReward ? ITEMS_JOB_COMPLETED : JOB_COMPLETED));

		OfflinePlayerUtils.ifOnline(job.getEmployer(), employer -> employer.spigot().sendMessage(
				new ComponentBuilder(this.messageService.getGeneralMessage(PLAYER_COMPLETED_YOUR_JOB, new Placeholders().put(COMPLETER, whoCompleted.getName())))
				.event(new HoverEvent(Action.SHOW_TEXT, new Text(describe(job))))
				.create()));
	}
	
	private static String describe(Job job) 
	{
		String goal = "Get " + ItemStackUtils.describe(job.getGoal());
		String reward = job.getReward().accept(TextRewardDescriptor.INSTANCE);
		
		return colorize(String.format("&6Goal: &f%s &8&l| &6Reward: &f%s", goal, reward));
	}
}
