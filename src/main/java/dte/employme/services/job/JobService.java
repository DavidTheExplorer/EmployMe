package dte.employme.services.job;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.job.Job;
import dte.employme.services.rewards.PartialCompletionInfo;

public interface JobService 
{
	FinishState getFinishState(Player player, Job job);
	boolean isBlacklistedAt(World world, Material material);
	
	String describeCompletionInGame(Job job, JobCompletionContext context);
	String describeInGame(Job job);
	
	int getGoalAmountInInventory(Job job, Inventory inventory);
	PartialCompletionInfo getPartialCompletionInfo(Player player, Job job, int maxGoalAmount);
	
	void loadJobs();
	void saveJobs();
	
	void startLiveUpdates(Player player, Job job);
	void stopLiveUpdates(Job job);
	void stopLiveUpdates(Player player);
	Map<Job, List<Player>> getLiveUpdatesInfo();
	
	void deleteAfter(Job job, Duration delay);
	void stopAutoDelete(Job job);
	void loadAutoDeletionData();
	void saveAutoDeletionData();
	
	
	
	enum FinishState
	{
		NEGATIVE, PARTIALLY, FULLY;
		
		public boolean hasFinished() 
		{
			return this != NEGATIVE;
		}
	}
}