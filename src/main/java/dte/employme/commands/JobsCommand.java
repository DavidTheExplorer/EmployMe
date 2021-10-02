package dte.employme.commands;

import static org.bukkit.ChatColor.RED;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.board.JobBoard;
import dte.employme.job.service.JobService;

@CommandAlias("job")
@Description("Get a job or view the Available Jobs!")
public class JobsCommand extends BaseCommand
{
	private static final int MAX_JOBS = ((6*9)-26);
	
	@Dependency
	private JobBoard globalJobBoard;
	
	@Dependency
	private JobService jobService;

	@HelpCommand
	@CatchUnknown
	public void sendHelp(CommandHelp help) 
	{
		help.showHelp();
	}

	@CommandAlias("jobs")
	@Subcommand("view")
	@Description("Search through all the Available Jobs.")
	public void view(Player player)
	{
		this.globalJobBoard.showTo(player);
	}
	
	@Subcommand("offer")
	@Description("Offer a new Job to the public.")
	public void createJob(@Conditions("Not Conversing") Player employer) 
	{
		if(this.globalJobBoard.getOfferedJobs().size() == MAX_JOBS) 
		{
			employer.sendMessage(RED + "Not enough room for additional Jobs.");
			return;
		}
		employer.openInventory(this.jobService.getCreationInventory(employer));
	}
	
	@Subcommand("delete")
	@Description("Delete one of your offered Jobs.")
	public void deleteJob(@Conditions("Employing") Player employer) 
	{
		employer.openInventory(this.jobService.getDeletionInventory(employer));
	}
	
	@Subcommand("myitems")
	@Description("Claim the items that people gathered for you.")
	public void openContainer(Player employer) 
	{
		employer.openInventory(this.jobService.getItemsContainer(employer.getUniqueId()));
	}
	
	@Subcommand("myrewards")
	@Description("Claim the rewards you got from Jobs your completed.")
	public void openRewardsContainer(Player player) 
	{
		player.openInventory(this.jobService.getRewardsContainer(player.getUniqueId()));
	}
}