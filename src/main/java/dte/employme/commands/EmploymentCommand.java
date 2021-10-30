package dte.employme.commands;

import static dte.employme.messages.MessageKey.NONE;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL;
import static dte.employme.messages.MessageKey.YOUR_SUBSCRIPTIONS_ARE;
import static dte.employme.messages.Placeholders.GOAL;
import static dte.employme.messages.Placeholders.GOAL_SUBSCRIPTIONS;
import static java.util.stream.Collectors.joining;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Material;
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
import dte.employme.containers.service.PlayerContainerService;
import dte.employme.inventories.InventoryFactory;
import dte.employme.job.service.JobService;
import dte.employme.job.subscription.JobSubscriptionService;
import dte.employme.messages.MessageService;
import dte.employme.messages.Placeholders;
import dte.employme.utils.java.EnumUtils;

@CommandAlias("employment")
@Description("The general employment command - View or Manage them!")
public class EmploymentCommand extends BaseCommand
{
	@Dependency
	private JobBoard globalJobBoard;
	
	@Dependency
	private JobService jobService;
	
	@Dependency
	private InventoryFactory inventoryFactory;
	
	@Dependency
	private PlayerContainerService playerContainerService;
	
	@Dependency
	private JobSubscriptionService jobSubscriptionService;
	
	@Dependency
	private MessageService messageService;
	
	private static final int MAX_JOBS = ((6*9)-26);
	
	@HelpCommand
	@CatchUnknown
	public void sendHelp(CommandHelp help) 
	{
		help.showHelp();
	}
	
	@Subcommand("subscribe")
	@Description("Get a notification once a job that rewards a desired item is posted.")
	public void subscribe(Player player, Material material) 
	{
		this.jobSubscriptionService.subscribe(player.getUniqueId(), material);
		this.messageService.sendGeneralMessage(player, SUCCESSFULLY_SUBSCRIBED_TO_GOAL, new Placeholders().put(GOAL, EnumUtils.fixEnumName(material)));
	}
	
	@Subcommand("unsubscribe")
	@Description("Stop receiving notifications for an item.")
	public void unsubscribe(Player player, @Conditions("Subscribed To Goal") Material material) 
	{
		this.jobSubscriptionService.unsubscribe(player.getUniqueId(), material);
		this.messageService.sendGeneralMessage(player, SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, new Placeholders().put(GOAL, EnumUtils.fixEnumName(material)));
	}
	
	@Subcommand("mysubscriptions")
	@Description("See your reward subscriptions.")
	public void showSubscriptions(Player player) 
	{
		String subscriptionsNames = this.jobSubscriptionService.getSubscriptions(player.getUniqueId()).stream()
				.map(EnumUtils::fixEnumName)
				.collect(joining(WHITE + ", " + GOLD));
		
		if(subscriptionsNames.isEmpty())
			subscriptionsNames = this.messageService.createMessage(NONE);
		
		subscriptionsNames += WHITE + ".";
		
		this.messageService.sendGeneralMessage(player, YOUR_SUBSCRIPTIONS_ARE, new Placeholders().put(GOAL_SUBSCRIPTIONS, subscriptionsNames));
	}
	
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
		employer.openInventory(this.inventoryFactory.getCreationMenu(employer));
	}
	
	@Subcommand("delete")
	@Description("Delete one of your offered Jobs.")
	public void deleteJob(@Conditions("Employing") Player employer) 
	{
		employer.openInventory(this.inventoryFactory.getDeletionMenu(employer));
	}
	
	@Subcommand("myitems")
	@Description("Claim the items that people gathered for you.")
	public void openContainer(Player employer) 
	{
		employer.openInventory(this.playerContainerService.getItemsContainer(employer.getUniqueId()).getInventory());
	}
	
	@Subcommand("myrewards")
	@Description("Claim the rewards you got from Jobs your completed.")
	public void openRewardsContainer(Player player) 
	{
		player.openInventory(this.playerContainerService.getRewardsContainer(player.getUniqueId()).getInventory());
	}
}