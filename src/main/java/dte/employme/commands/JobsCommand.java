package dte.employme.commands;

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
@Description("Get or view the Available Jobs!")
public class JobsCommand extends BaseCommand
{
	@Dependency
	private JobBoard jobBoard;
	
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
	@Description("Search through all the available jobs.")
	public void view(Player player) 
	{
		this.jobBoard.showTo(player);
	}
	
	@Subcommand("offer")
	@Description("Offer a new Job to the public.")
	public void createJob(@Conditions("Not Conversing") Player employer) 
	{
		employer.openInventory(this.jobService.getCreationInventory());
	}
}