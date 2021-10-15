package dte.employme.board.listeners;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.messages.Message;
import dte.employme.utils.ChatColorUtils;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.OfflinePlayerUtils;
import dte.employme.visitors.reward.TextRewardDescriptor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class JobCompletedMessagesListener implements JobCompleteListener
{
	@Override
	public void onJobCompleted(JobBoard board, Job job, Player whoCompleted) 
	{
		Message.sendGeneralMessage(whoCompleted, (job.getReward() instanceof ItemsReward ? Message.ITEMS_JOB_COMPLETED : Message.JOB_COMPLETED));

		OfflinePlayerUtils.ifOnline(job.getEmployer(), employer -> employer.spigot().sendMessage(new ComponentBuilder(Message.GENERAL_PREFIX + Message.PLAYER_COMPLETED_YOUR_JOB.inject(whoCompleted.getName()))
				.event(new HoverEvent(Action.SHOW_TEXT, new Text(describe(job))))
				.create()));
	}
	
	private static String describe(Job job) 
	{
		return ChatColorUtils.colorize(String.format("&6Goal: &f%s &8&l| &6Reward: &f%s", 
				"Get " + ItemStackUtils.describe(job.getGoal()), 
				job.getReward().accept(TextRewardDescriptor.INSTANCE)));
	}
}
