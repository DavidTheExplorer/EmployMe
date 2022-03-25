package dte.employme.commands.sub.employment;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.services.playercontainer.PlayerContainerService;

@CommandAlias("employment|emp")
public class EmploymentContainerCommands extends BaseCommand
{
	private final PlayerContainerService playerContainerService;
	
	public EmploymentContainerCommands(PlayerContainerService playerContainerService) 
	{
		this.playerContainerService = playerContainerService;
	}
	
	@Subcommand("myitems")
	@Description("Claim the items that people gathered for you.")
	@CommandPermission("employme.jobs.myitems")
	public void openContainer(Player employer) 
	{
		employer.openInventory(this.playerContainerService.getItemsContainer(employer.getUniqueId()));
	}

	@Subcommand("myrewards")
	@Description("Claim the rewards you got from Jobs your completed.")
	@CommandPermission("employme.jobs.myrewards")
	public void openRewardsContainer(Player player) 
	{
		player.openInventory(this.playerContainerService.getRewardsContainer(player.getUniqueId()));
	}
}
