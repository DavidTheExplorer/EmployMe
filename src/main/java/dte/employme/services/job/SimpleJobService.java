package dte.employme.services.job;

import static dte.employme.messages.MessageKey.GET;
import static dte.employme.messages.MessageKey.GOAL;
import static dte.employme.messages.MessageKey.JOB_AUTO_REMOVED;
import static dte.employme.messages.MessageKey.PREFIX;
import static dte.employme.messages.MessageKey.REWARD;
import static dte.employme.utils.ChatColorUtils.colorize;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.config.ConfigFile;
import dte.employme.job.Job;
import dte.employme.services.message.MessageService;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.utils.InventoryUtils;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.OfflinePlayerUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class SimpleJobService implements JobService
{
	private final JobBoard globalJobBoard;
	private final MessageService messageService;
	private final JobRewardService jobRewardService;
	private final Map<Job, JobDeletionInfo> autoDeletion = new HashMap<>();
	private final ConfigFile jobsConfig, autoDeletionConfig;

	public SimpleJobService(JobBoard globalJobBoard, JobRewardService jobRewardService, ConfigFile jobsConfig, ConfigFile autoDeletionConfig, MessageService messageService) 
	{
		this.globalJobBoard = globalJobBoard;
		this.jobsConfig = jobsConfig;
		this.autoDeletionConfig = autoDeletionConfig;
		this.messageService = messageService;
		this.jobRewardService = jobRewardService;
	}
	
	@Override
	public boolean hasFinished(Player player, Job job) 
	{
		ItemStack goal = job.getGoal();
		
		return InventoryUtils.containsAtLeast(player.getInventory(), item -> JobService.isGoal(item, goal), goal.getAmount());
	}
	
	@Override
	public String describeInGame(Job job) 
	{
		String goal = this.messageService.getMessage(GET).first() + " " + ItemStackUtils.describe(job.getGoal());
		
		return colorize(String.format("&6%s: &f%s &8&l| &6%s: &f%s", 
				this.messageService.getMessage(GOAL).first(),
				goal,
				this.messageService.getMessage(REWARD).first(),
				this.jobRewardService.describe(job.getReward())));
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
				String jobDescription = describeInGame(job);
				
				this.messageService.getMessage(JOB_AUTO_REMOVED)
				.prefixed(this.messageService.getMessage(PREFIX).first())
				.stream()
				.map(line -> new ComponentBuilder(line).event(new HoverEvent(Action.SHOW_TEXT, new Text(jobDescription))).create())
				.forEach(message -> employerPlayer.spigot().sendMessage(message));
			});
			
			this.autoDeletion.remove(job);
			
		}, delay.getSeconds() * 20);
	}
}