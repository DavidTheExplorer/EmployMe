package dte.employme.commands;

import static dte.employme.messages.MessageKey.PLUGIN_RELOADED;
import static dte.employme.messages.MessageKey.PREFIX;

import java.util.List;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.board.JobBoard;
import dte.employme.board.displayers.JobBoardDisplayer;
import dte.employme.reloadable.Reloadable;
import dte.employme.services.message.MessageService;

@CommandAlias("employment|emp")
@Description("The main entry of EmployMe commands.")
public class EmploymentCommand extends BaseCommand
{
	private final JobBoard globalJobBoard;
	private final JobBoardDisplayer jobBoardDisplayer;
	private final MessageService messageService;
	private final List<Reloadable> reloadables;
	
	public EmploymentCommand(JobBoard globalJobBoard, MessageService messageService, JobBoardDisplayer jobBoardDisplayer, List<Reloadable> reloadables) 
	{
		this.globalJobBoard = globalJobBoard;
		this.messageService = messageService;
		this.jobBoardDisplayer = jobBoardDisplayer;
		this.reloadables = reloadables;
	}
	
	@HelpCommand
	@CatchUnknown
	public void sendHelp(CommandHelp help) 
	{
		help.showHelp();
	}
	
	@Subcommand("view")
	@Description("Search through all the Available Jobs.")
	@CommandPermission("employme.jobs.view")
	public void view(Player player)
	{
		this.jobBoardDisplayer.display(player, this.globalJobBoard);
	}
	
	@Subcommand("reload")
	@CommandPermission("employme.reload")
	public void reload(Player player) 
	{
		this.reloadables.forEach(Reloadable::reload);
		
		this.messageService.getMessage(PLUGIN_RELOADED)
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.sendTo(player);
	}
}