package dte.employme.visitors.goal;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.job.Job;
import dte.employme.job.goals.ItemGoal;
import dte.employme.job.service.JobService;
import dte.employme.utils.InventoryUtils;

public class GoalReachHandler implements GoalVisitor<Void>
{
	private final Job job;
	private final Player whoReached;
	private final JobService jobService;
	
	public GoalReachHandler(Job job, Player whoReached, JobService jobService)
	{
		this.job = job;
		this.whoReached = whoReached;
		this.jobService = jobService;
	}

	@Override
	public Void visit(ItemGoal itemGoal) 
	{
		ItemStack item = itemGoal.getItem();
		
		InventoryUtils.remove(this.whoReached.getInventory(), item);
		this.jobService.getItemsContainer(this.job.getEmployer().getUniqueId()).addItem(item);
		return null;
	}
}