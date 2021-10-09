package dte.employme.job.service;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.config.ConfigFile;
import dte.employme.inventories.InventoryFactory;
import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.messages.Message;
import dte.employme.utils.ChatColorUtils;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.OfflinePlayerUtils;
import dte.employme.visitors.reward.TextRewardDescriptor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class SimpleJobService implements JobService
{
	private final JobBoard globalJobBoard;
	private final InventoryFactory inventoryFactory;
	private ConfigFile jobsConfig;

	public SimpleJobService(JobBoard globalJobBoard, InventoryFactory inventoryFactory) 
	{
		this.globalJobBoard = globalJobBoard;
		this.inventoryFactory = inventoryFactory;
	}

	@Override
	public void loadJobs() 
	{
		this.jobsConfig = ConfigFile.byPath("jobs.yml");
		this.jobsConfig.createIfAbsent(IOException::printStackTrace);
		
		this.jobsConfig.getList("Jobs", Job.class).forEach(this.globalJobBoard::addJob);
	}
	
	@Override
	public void saveJobs() 
	{
		this.jobsConfig.getConfig().set("Jobs", this.globalJobBoard.getOfferedJobs());
		this.jobsConfig.save(IOException::printStackTrace);
	}

	@Override
	public void onComplete(Job job, Player completer)
	{
		this.globalJobBoard.removeJob(job);

		//reward the completer
		job.getReward().giveTo(completer);

		//transfer the goal to the employer's items container
		ItemStack goal = job.getGoal();
		InventoryUtils.remove(completer.getInventory(), goal);
		this.inventoryFactory.getItemsContainer(job.getEmployer().getUniqueId()).addItem(goal);

		//message the completer & employer
		Message.sendGeneralMessage(completer, (job.getReward() instanceof ItemsReward ? Message.ITEMS_JOB_COMPLETED : Message.JOB_COMPLETED));

		OfflinePlayerUtils.ifOnline(job.getEmployer(), employer -> employer.spigot().sendMessage(new ComponentBuilder(Message.GENERAL_PREFIX + Message.PLAYER_COMPLETED_YOUR_JOB.inject(completer.getName()))
				.event(new HoverEvent(Action.SHOW_TEXT, new Text(describe(job))))
				.create()));
	}

	@Override
	public boolean hasFinished(Job job, Player player)
	{
		ItemStack goalItem = job.getGoal();

		return player.getInventory().containsAtLeast(goalItem, goalItem.getAmount());
	}

	private static String describe(Job job) 
	{
		return ChatColorUtils.colorize(String.format("&6Goal: &f%s &8&l| &6Reward: &f%s", 
				"Get " + ItemStackUtils.describe(job.getGoal()), 
				job.getReward().accept(TextRewardDescriptor.INSTANCE)));
	}
}