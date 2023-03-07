package dte.employme.runnables;

import static dte.employme.messages.MessageKey.GET;
import static dte.employme.messages.MessageKey.LIVE_UPDATES_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.LIVE_UPDATES_TRACKER_ACTIONBAR;
import static dte.employme.utils.java.Percentages.toFraction;
import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import dte.employme.job.Job;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.java.Percentages;
import dte.employme.utils.java.StringUtils;
import net.md_5.bungee.api.chat.TextComponent;

public class JobLiveUpdateTask extends BukkitRunnable
{
	private final JobService jobService;
	private final MessageService messageService;
	
	public JobLiveUpdateTask(JobService jobService, MessageService messageService) 
	{
		this.jobService = jobService;
		this.messageService = messageService;
	}
	
	@Override
	public void run()
	{
		this.jobService.getLiveUpdatesInfo().forEach((job, players) -> 
		{
			for(Player player : players) 
			{
				double progressionPercentage = Percentages.of(
						this.jobService.getGoalAmountInInventory(job, player.getInventory()), 
						job.getGoal().getAmount());

				player.spigot().sendMessage(ACTION_BAR, getProgressionMessage(job, progressionPercentage));
			}
		});
	}
	
	private TextComponent getProgressionMessage(Job job, double progressionPercentage) 
	{
		if(progressionPercentage >= 100)
			return this.messageService.loadMessage(LIVE_UPDATES_JOB_COMPLETED).toTextComponent();

		int aquaLines = (int) (20 * toFraction(progressionPercentage));
		
		return this.messageService.loadMessage(LIVE_UPDATES_TRACKER_ACTIONBAR)
				.inject("get", this.messageService.loadMessage(GET).first())
				.inject("goal", ItemStackUtils.describe(job.getGoal()))
				.inject("progression", StringUtils.repeat("|", aquaLines))
				.inject("amount left", StringUtils.repeat("|", 20 - aquaLines))
				.inject("completion percentage", (int) progressionPercentage)
				.toTextComponent();
	}
}