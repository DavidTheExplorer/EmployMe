package dte.employme.services.job;

import static dte.employme.messages.MessageKey.GET;
import static dte.employme.messages.MessageKey.GOAL;
import static dte.employme.messages.MessageKey.JOB_AUTO_REMOVED;
import static dte.employme.messages.MessageKey.REWARD;
import static dte.employme.services.job.JobService.FinishState.FULLY;
import static dte.employme.services.job.JobService.FinishState.NEGATIVE;
import static dte.employme.services.job.JobService.FinishState.PARTIALLY;
import static dte.employme.utils.ChatColorUtils.colorize;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.configs.BlacklistedItemsConfig;
import dte.employme.job.Job;
import dte.employme.listeners.JobLiveUpdatesListener;
import dte.employme.rewards.PartialReward;
import dte.employme.runnables.JobLiveUpdateTask;
import dte.employme.services.message.MessageService;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.services.rewards.PartialCompletionInfo;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.OfflinePlayerUtils;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.Percentages;
import dte.spigotconfiguration.SpigotConfig;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class SimpleJobService implements JobService, Listener
{
	private final JobBoard globalJobBoard;
	private final MessageService messageService;
	private final JobRewardService jobRewardService;
	private final SpigotConfig jobsConfig, autoDeletionConfig;
	private final BlacklistedItemsConfig blacklistedItemsConfig;

	private final Map<Job, JobDeletionInfo> autoDeletion = new HashMap<>();
	private final Map<Job, List<Player>> liveUpdates = new HashMap<>();

	public SimpleJobService(JobBoard globalJobBoard, JobRewardService jobRewardService, SpigotConfig jobsConfig, SpigotConfig autoDeletionConfig, BlacklistedItemsConfig blacklistedItemsConfig, MessageService messageService) 
	{
		this.globalJobBoard = globalJobBoard;
		this.jobsConfig = jobsConfig;
		this.autoDeletionConfig = autoDeletionConfig;
		this.blacklistedItemsConfig = blacklistedItemsConfig;
		this.messageService = messageService;
		this.jobRewardService = jobRewardService;
		
		new JobLiveUpdateTask(this, this.messageService).runTaskTimer(EmployMe.getInstance(), 0, 5);
		EmployMe.getInstance().registerListeners(new JobLiveUpdatesListener(this));
	}

	@Override
	public FinishState getFinishState(Player player, Job job) 
	{
		PlayerInventory playerInventory = player.getInventory();

		if(!InventoryUtils.containsAtLeast(playerInventory, job::isGoal, 1))
			return NEGATIVE;

		if(InventoryUtils.containsAtLeast(playerInventory, job::isGoal, job.getGoal().getAmount()))
			return FULLY;

		return job.getReward() instanceof PartialReward ? PARTIALLY : NEGATIVE;
	}

	@Override
	public boolean isBlacklistedAt(World world, Material material)
	{
		return this.blacklistedItemsConfig.isBlacklistedAt(world, material);
	}

	@Override
	public String describeInGame(Job job) 
	{
		return String.format(colorize("&6%s: &f%s &8&l| &6%s: &f%s"), 
				this.messageService.getMessage(GOAL).first(),
				this.messageService.getMessage(GET).first() + " " + job.getGoalProvider().getDisplayName(job.getGoal()),
				this.messageService.getMessage(REWARD).first(),
				this.jobRewardService.describe(job.getReward()));
	}

	@Override
	public String describeCompletionInGame(Job job, JobCompletionContext context) 
	{
		if(context.isJobCompleted()) 
			return describeInGame(job);
		
		return String.format(colorize("&6%s: &f%s (&6%.1f%% done&f) &8&l| &6%s: &f%s"),
				this.messageService.getMessage(GOAL).first(),
				this.messageService.getMessage(GET).first() + " " + job.getGoalProvider().getDisplayName(context.getGoal()),
				context.getPartialInfo().getPercentage(),
				this.messageService.getMessage(REWARD).first(),
				this.jobRewardService.describe(context.getReward()));
	}

	@Override
	public PartialCompletionInfo getPartialCompletionInfo(Player player, Job job, int maxGoalAmount)
	{
		if(!(job.getReward() instanceof PartialReward))
			throw new IllegalArgumentException("Cannot calculate completion percentage for a job whose reward is not partial!");

		int goalAmount = Math.min(getGoalAmountInInventory(job, player.getInventory()), maxGoalAmount);
		double completionPercentage = Percentages.of(goalAmount, job.getGoal().getAmount());
		PartialReward partialReward = ((PartialReward) job.getReward()).afterPartialCompletion(100 - completionPercentage);

		ItemStack partialGoal = new ItemBuilder(job.getGoal())
				.amounted(goalAmount)
				.createCopy();

		return new PartialCompletionInfo(completionPercentage, partialGoal, partialReward);
	}

	@Override
	public int getGoalAmountInInventory(Job job, Inventory inventory) 
	{
		return InventoryUtils.allSlotsThat(inventory, job::isGoal)
				.map(i -> inventory.getItem(i).getAmount())
				.sum();
	}

	@Override
	public void loadJobs() 
	{
		this.jobsConfig.getList("Jobs", Job.class).forEach(this.globalJobBoard::addJob);
	}

	@Override
	public void saveJobs() 
	{
		this.jobsConfig.set("Jobs", this.globalJobBoard.getOfferedJobs());

		try 
		{
			this.jobsConfig.save();
		} 
		catch(IOException exception) 
		{
			exception.printStackTrace();
		}
	}
	
	@Override
	public void startLiveUpdates(Player player, Job job) 
	{
		stopLiveUpdates(player);
		
		this.liveUpdates.computeIfAbsent(job, l -> new ArrayList<>()).add(player);
	}
	
	@Override
	public void stopLiveUpdates(Job job) 
	{
		this.liveUpdates.remove(job);
	}
	
	@Override
	public void stopLiveUpdates(Player player) 
	{
		this.liveUpdates.values().forEach(players -> players.remove(player));
	}
	
	@Override
	public Map<Job, List<Player>> getLiveUpdatesInfo() 
	{
		return new HashMap<>(this.liveUpdates);
	}

	@Override
	public void deleteAfter(Job job, Duration delay) 
	{
		BukkitTask deletionTask = createDeletionTask(job, delay);

		this.autoDeletion.put(job, new JobDeletionInfo(delay, deletionTask));
	}

	@Override
	public void stopAutoDelete(Job job) 
	{
		this.autoDeletion.remove(job).getTask().cancel();
	}

	@Override
	public void loadAutoDeletionData() 
	{
		this.autoDeletionConfig.getSection("Auto Deletion").getValues(false).forEach((jobUUID, deletionTimeString) -> 
		{
			Job job = this.globalJobBoard.getJobByUUID(UUID.fromString(jobUUID))
					.orElseThrow(() -> new RuntimeException("Could not find a job with the UUID of: " + jobUUID));

			LocalDateTime deletionTime = LocalDateTime.parse((String) deletionTimeString);

			if(LocalDateTime.now().isAfter(deletionTime)) 
			{
				this.globalJobBoard.removeJob(job);
				return;
			}

			deleteAfter(job, Duration.between(LocalDateTime.now(), deletionTime));
		});
	}

	@Override
	public void saveAutoDeletionData() 
	{
		this.autoDeletionConfig.delete("Auto Deletion");

		ConfigurationSection section = this.autoDeletionConfig.getSection("Auto Deletion");
		this.autoDeletion.forEach((job, data) -> section.set(job.getUUID().toString(), data.getDeletionDate().toString()));

		try 
		{
			this.autoDeletionConfig.save();
		} 
		catch(IOException exception) 
		{
			exception.printStackTrace();
		}
	}

	private BukkitTask createDeletionTask(Job job, Duration delay) 
	{
		return Bukkit.getScheduler().runTaskLater(EmployMe.getInstance(), () ->
		{
			OfflinePlayer employer = job.getEmployer();

			//remove the job from the board
			this.globalJobBoard.removeJob(job);
			job.getReward().giveTo(employer);

			//notify the employer
			OfflinePlayerUtils.ifOnline(employer, employerPlayer -> 
			{
				this.messageService.getMessage(JOB_AUTO_REMOVED)
				.stream()
				.map(line -> new ComponentBuilder(line).event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(describeInGame(job)).create())).create())
				.forEach(employerPlayer.spigot()::sendMessage);
			});

			this.autoDeletion.remove(job);

		}, delay.getSeconds() * 20);
	}
}