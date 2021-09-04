package dte.employme.commands;

import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.board.service.JobBoardService;
import dte.employme.conversations.JobGoalPrompt;
import dte.employme.conversations.JobPaymentPrompt;
import dte.employme.conversations.JobPostedMessagePrompt;
import dte.employme.reward.ItemsReward;
import net.milkbowl.vault.economy.Economy;

@CommandAlias("job")
@Description("Get or view the Available Jobs!")
public class JobsCommand extends BaseCommand
{
	private final JobBoard jobBoard;
	private final ConversationFactory moneyJobConversationFactory, itemsJobConversationFactory;

	public JobsCommand(JobBoard jobBoard, Economy economy, JobBoardService jobBoardService) 
	{
		this.jobBoard = jobBoard;
		
		this.moneyJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobGoalPrompt(new JobPaymentPrompt(jobBoardService, jobBoard, economy)));
		
		this.itemsJobConversationFactory = createConversationFactory()
				.withFirstPrompt(new JobGoalPrompt(new JobPostedMessagePrompt(jobBoardService, jobBoard)));
	}

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

	@Subcommand("offer money")
	@Description("Offer a job you will pay for.")
	public void createMoneyJob(@Conditions("Not Conversing") Player employer)
	{
		this.moneyJobConversationFactory.buildConversation(employer).begin();
	}

	@Subcommand("offer myinventory")
	@Description("Offer a job with Item(s) Reward.")
	public void createItemsJob(@Conditions("Not Conversing") Player employer, @Flags("Items In Inventory") ItemsReward inventoryItems)
	{
		Conversation conversation = this.itemsJobConversationFactory.buildConversation(employer);
		conversation.getContext().setSessionData("reward", inventoryItems);
		conversation.begin();
	}

	private static ConversationFactory createConversationFactory() 
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(false)
				.withModality(false)
				.withPrefix(context -> YELLOW + "> ");
	}
}
