package dte.employme.board.listeners;

import static java.util.stream.Collectors.toList;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import dte.employme.board.JobBoard;
import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.board.listeners.completion.JobCompleteListener;
import dte.employme.board.listeners.removal.JobRemovalListener;
import dte.employme.job.Job;
import dte.employme.services.job.JobService;
import dte.employme.utils.java.Percentages;

public class StopJobLiveUpdatesListener implements JobCompleteListener, JobRemovalListener
{
	private final JobService jobService;
	
	private static final List<Player> DUMMY_LIST = new ArrayList<>();

	public StopJobLiveUpdatesListener(JobService jobService)
	{
		this.jobService = jobService;
	}

	@Override
	public void onJobRemoved(JobBoard jobBoard, Job job)
	{
		notifySubscribers(job, YELLOW + "The job you were doing was just deleted by the employer!", job.getEmployer().getPlayer());
		this.jobService.stopLiveUpdates(job);
	}

	@Override
	public void onJobCompleted(Job job, Player whoCompleted, JobCompletionContext context) 
	{
		if(!context.isJobCompleted())
		{
			notifySubscribers(job, YELLOW + String.format("The job you were doing was just completed by %s%%!", Percentages.format(context.getPartialInfo().getPercentage())), whoCompleted);
			return;
		}
		
		notifySubscribers(job, RED + "The job you were doing was just completed by another player! :(", whoCompleted);
		this.jobService.stopLiveUpdates(job);
	}
	
	private void notifySubscribers(Job job, String message, Player... exemptPlayers) 
	{
		Set<Player> exemptSet = Sets.newHashSet(exemptPlayers);
		
		List<Player> subscribers = this.jobService.getLiveUpdatesInfo().getOrDefault(job, DUMMY_LIST).stream()
				.filter(player -> !exemptSet.contains(player))
				.collect(toList());
		
		subscribers.forEach(subscriber -> subscriber.sendMessage(message));
	}
}
