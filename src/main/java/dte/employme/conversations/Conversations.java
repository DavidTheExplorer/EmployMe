package dte.employme.conversations;

import java.util.Collection;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.inventories.InventoryFactory;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.messages.Message;
import net.milkbowl.vault.economy.Economy;

public class Conversations
{
	private final InventoryFactory inventoryFactory;
	private final ConversationFactory moneyJobConversationFactory, itemsJobConversationFactory;
	
	public Conversations(JobBoard globalJobBoard, InventoryFactory inventoryFactory, Economy economy)
	{
		this.inventoryFactory = inventoryFactory;
		this.moneyJobConversationFactory = createConversationFactory().withFirstPrompt(new JobGoalPrompt(new JobPaymentPrompt(globalJobBoard, economy)));
		this.itemsJobConversationFactory = createConversationFactory().withFirstPrompt(new JobGoalPrompt(new JobPostedMessagePrompt(globalJobBoard)));
	}
	
	public Conversation buildMoneyJobConversation(Player employer)
	{
		return this.moneyJobConversationFactory.buildConversation(employer);
	}
	
	public Conversation buildItemsJobConversation(Player employer, Collection<ItemStack> offeredItems)
	{
		Conversation conversation = this.itemsJobConversationFactory.buildConversation(employer);
		conversation.getContext().setSessionData("reward", new ItemsReward(offeredItems, this.inventoryFactory));
		
		return conversation;
	}
	
	private static ConversationFactory createConversationFactory()
	{
		return new ConversationFactory(EmployMe.getInstance())
				.withLocalEcho(true)
				.withModality(false)
				.withEscapeSequence("stop")
				.withPrefix(context -> Message.GENERAL_PREFIX.toString());
	}
}
