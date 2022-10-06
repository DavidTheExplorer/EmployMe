package dte.employme.board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.listeners.addition.JobAddListener;
import dte.employme.board.listeners.completion.JobCompleteListener;
import dte.employme.board.listeners.removal.JobRemovalListener;
import dte.employme.job.Job;
import dte.employme.rewards.Reward;
import dte.employme.services.rewards.PartialCompletionInfo;

public interface JobBoard extends Iterable<Job>
{
	void addJob(Job job);
	void removeJob(Job job);
	void completeJob(Job job, Player whoCompleted, JobCompletionContext context);

	//query
	Optional<Job> getJobByUUID(UUID uuid);
	List<Job> getJobsOfferedBy(UUID employerUUID);
	List<Job> getOfferedJobs();

	//listeners
	void registerAddListener(JobAddListener... listeners);
	void registerCompleteListener(JobCompleteListener... listeners);
	void registerRemovalListener(JobRemovalListener... listeners);
	void removeAddListener(JobAddListener... listeners);
	void removeCompleteListener(JobCompleteListener... listeners);
	void removeRemovalListener(JobRemovalListener... listeners);
	
	
	
	class JobCompletionContext 
	{
		private final ItemStack goal;
		private final Reward reward;
		private final PartialCompletionInfo partialCompletionInfo;
		
		/**
		 * Returns a completion context for a job that was normally completed - meaning the required item was gathered by the player.
		 * 
		 * @param job The job that was completed.
		 * @return A Context object that describes the completion.
		 */
		public static JobCompletionContext normal(Job job)
		{
			return new JobCompletionContext(job.getGoal(), job.getReward(), null);
		}
		
		/**
		 * Returns a completion context for a job that was partially completed - meaning only a percentage of the required item was gathered by the player.
		 * 
		 * @param partialCompletionInfo The information about the partial completion.
		 * @return A Context object that describes the completion.
		 */
		public static JobCompletionContext partial(PartialCompletionInfo partialCompletionInfo) 
		{
			return new JobCompletionContext(partialCompletionInfo.getGoal(), partialCompletionInfo.getReward(), partialCompletionInfo);
		}
		
		private JobCompletionContext(ItemStack goal, Reward reward, PartialCompletionInfo partialCompletionInfo) 
		{
			this.goal = goal;
			this.reward = reward;
			this.partialCompletionInfo = partialCompletionInfo;
		}
		
		public boolean isJobCompleted() 
		{
			return this.partialCompletionInfo == null;
		}
		
		public PartialCompletionInfo getPartialInfo() 
		{
			return this.partialCompletionInfo;
		}

		public Reward getReward() 
		{
			return this.reward;
		}
		
		public ItemStack getGoal() 
		{
			return this.goal;
		}

		@Override
		public String toString()
		{
			return String.format("JobCompletionContext [goal=%s, reward=%s, partialCompletionInfo=%s]", this.goal, this.reward, this.partialCompletionInfo);
		}
	}
}