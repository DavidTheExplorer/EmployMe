package dte.employme.job.service;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.config.ConfigFile;
import dte.employme.job.Job;
import dte.employme.utils.InventoryUtils;

public class SimpleJobService implements JobService
{
	private final JobBoard globalJobBoard;
	private ConfigFile jobsConfig;

	public SimpleJobService(JobBoard globalJobBoard, ConfigFile jobsConfig) 
	{
		this.globalJobBoard = globalJobBoard;
		this.jobsConfig = jobsConfig;
	}
	
	@Override
	public boolean hasFinished(Player player, Job job) 
	{
		ItemStack goal = job.getGoal();
		
		return InventoryUtils.containsAtLeast(player.getInventory(), item -> JobService.isGoal(item, goal), goal.getAmount());
	}

	@Override
	public void loadJobs() 
	{
		this.jobsConfig.getList("Jobs", Job.class).forEach(this.globalJobBoard::addJob);
	}
	
	@Override
	public void saveJobs() 
	{
		this.jobsConfig.getConfig().set("Jobs", this.globalJobBoard.getOfferedJobs());
		this.jobsConfig.save(IOException::printStackTrace);
	}
}