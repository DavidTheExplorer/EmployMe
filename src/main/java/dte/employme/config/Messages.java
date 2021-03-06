package dte.employme.config;

import static dte.employme.messages.MessageKey.*;

import java.util.HashMap;
import java.util.Map;

import dte.employme.messages.MessageKey;

public class Messages
{
	private final Map<MessageKey, String[]> messages = new HashMap<>();
	
	public Messages put(MessageKey messageKey, String... lines) 
	{
		this.messages.put(messageKey, lines);
		return this;
	}
	
	public String[] getLines(MessageKey messageKey) 
	{
		return this.messages.get(messageKey);
	}
	
	public Map<MessageKey, String[]> toMap()
	{
		return this.messages;
	}
	
	public static final Messages ENGLISH = new Messages()

			//Jobs
			.put(JOB_ADDED_TO_BOARD, "&aYour offer was added to the &eJobs Board&a!")
			.put(JOB_COMPLETED, "&aYou successfully completed this Job!")
			.put(ITEMS_JOB_COMPLETED, "&aJob Completed. You can access your items via &b\"/employment myrewards\"")
			.put(PLAYER_COMPLETED_YOUR_JOB, "&b%completer% &djust completed one of your Jobs!")
			.put(YOU_OFFERED_TOO_MANY_JOBS, "&cYou have offered too many Jobs! Please delete one to proceed.")
			.put(JOB_CANCELLED_REWARD_REFUNDED, "&aReward &frefunded due to cancelling the Job!", "&fItem rewards can be accessed via &b\"/employment mycontainers\"&f.")

			//Job Added Notifiers
			.put(YOUR_NEW_JOB_ADDED_NOTIFIER_IS, "&aYou will get notifications for &e%job added notifier%&a!")
			.put(NEW_JOB_POSTED, "&fA new job was posted in the &aJobs Board&f!")

			//Subscriptions
			.put(SUCCESSFULLY_SUBSCRIBED_TO_GOAL, "&fYou just &asubscribed &fto &e%goal% &fJobs!")
			.put(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, "&fSuccessfully &4unsubscribed &ffrom &e%goal% &fJobs.")
			.put(SUBSCRIBED_TO_GOALS_NOTIFICATION, "&fA player just posted a job that offers &b&l%rewards%&f!")
			.put(MUST_BE_SUBSCRIBED_TO_GOAL, "&cYou have to be subscribed to this Goal!")
			.put(YOUR_SUBSCRIPTIONS_ARE, "&fYou are subscribed to: &6%goal subscriptions%")

			//General
			.put(PREFIX, "&2[&aEmployMe&2] ")
			.put(NONE, "None")
			.put(GET, "Get")
			.put(GOAL, "Goal")
			.put(REWARD, "Reward")
			.put(MUST_NOT_BE_CONVERSING, "&cYou have to finish your current conversation.")
			.put(MATERIAL_NOT_FOUND, "&cThe specified Material doesn't exist!")
			.put(NEW_UPDATE_AVAILABLE, "&fPlease update &fto the lastest version! (&e%new version%&f)")
			.put(CURRENCY_SYMBOL, "$")
			.put(PLUGIN_RELOADED, "&fReload completed in &a%reload time%ms&f!")

			//Job Containers GUI
			.put(INVENTORY_JOB_CONTAINERS_TITLE, "Personal Job Containers")
			.put(INVENTORY_JOB_CONTAINERS_REWARDS_CONTAINER_NAME, "&dRewards")
			.put(INVENTORY_JOB_CONTAINERS_REWARDS_CONTAINER_LORE, "&fThis is where Reward Items are stored", "&fafter you complete a job that pays them.")
			.put(INVENTORY_JOB_CONTAINERS_ITEMS_CONTAINER_NAME, "&dItems")
			.put(INVENTORY_JOB_CONTAINERS_ITEMS_CONTAINER_LORE, "&fWhen someone completes one of your jobs,", "&fThe items they got for you are stored here.")
			.put(CONTAINER_CLAIM_INSTRUCTION, "Claim your %container subject%:")
			
			//Player Container GUI
			.put(INVENTORY_PLAYER_CONTAINER_NEXT_PAGE_NAME, "&aNext")
			.put(INVENTORY_PLAYER_CONTAINER_NEXT_PAGE_LORE, "&fClick to open the next page")
			.put(INVENTORY_PLAYER_CONTAINER_PREVIOUS_PAGE_NAME, "&cBack")
			.put(INVENTORY_PLAYER_CONTAINER_PREVIOUS_PAGE_LORE, "&fClick to open the previous page")

			//Job Icon
			.put(JOB_ICON_NAME, "&a%employer%'s Offer")
			.put(JOB_ICON_GOAL_INSTRUCTIONS, "&b&lGoal: &fI need &b%goal%&f.")
			.put(JOB_ICON_ENCHANT_DESCRIPTION, "&dEnchanted &fwith:")
			.put(JOB_ICON_MONEY_PAYMENT_DESCRIPTION, "&6&n&lPayment&6: &f%money payment%%currency symbol%")
			.put(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION, "&6&n&lPayment&6: &fRight Click to preview items(%items amount%)")
			
			//Job Board GUI
			.put(INVENTORY_JOB_BOARD_TITLE, "Available Jobs")
			.put(INVENTORY_JOB_BOARD_OFFER_COMPLETED, "&a&lClick to Finish!")
			.put(INVENTORY_JOB_BOARD_OFFER_NOT_COMPLETED, "&cYou didn't complete this Job.")
			.put(INVENTORY_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME, "&aYour Jobs")
			.put(INVENTORY_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE, "&fView or Edit the jobs that you posted.")
			.put(INVENTORY_JOB_BOARD_NEXT_PAGE_NAME, "&aNext")
			.put(INVENTORY_JOB_BOARD_NEXT_PAGE_LORE, "&fClick to open the next page")
			.put(INVENTORY_JOB_BOARD_PREVIOUS_PAGE_NAME, "&cBack")
			.put(INVENTORY_JOB_BOARD_PREVIOUS_PAGE_LORE, "&fClick to open the previous page")
			
			//Player Jobs GUI
			.put(INVENTORY_PLAYER_JOBS_TITLE, "Your Jobs")
			.put(INVENTORY_PLAYER_JOBS_NEXT_PAGE_NAME, "&aNext")
			.put(INVENTORY_PLAYER_JOBS_NEXT_PAGE_LORE, "&fClick to open the next page")
			.put(INVENTORY_PLAYER_JOBS_PREVIOUS_PAGE_NAME, "&cBack")
			.put(INVENTORY_PLAYER_JOBS_PREVIOUS_PAGE_LORE, "&fClick to open the previous page")
			
			//Job Deletion GUI
			.put(INVENTORY_JOB_DELETION_TITLE, "Select Jobs to Delete")
			.put(INVENTORY_JOB_DELETION_DELETE_INSTRUCTION, "&4&lClick to Delete!")
			.put(JOB_SUCCESSFULLY_DELETED, "&eYou successfully deleted this Job!")

			//Job Creation GUI
			.put(INVENTORY_JOB_CREATION_TITLE, "Create a new Job")
			.put(INVENTORY_JOB_CREATION_MONEY_JOB_ICON_NAME, "&6Money Job")
			.put(INVENTORY_JOB_CREATION_MONEY_JOB_ICON_LORE, "&fClick to offer a Job for which", "&fyou will pay a certain amount of money.")
			.put(INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_NAME, "&bItems Job")
			.put(INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_LORE, "&fClick to offer a Job for which", "&fyou will pay with resources.")
			.put(MONEY_PAYMENT_AMOUNT_QUESTION, "&fHow much will you &e&lPay&f? &f(Current Balance: &e&l%player money%&6%currency symbol%&f)")
			.put(MONEY_REWARD_ERROR_NEGATIVE, "&cCan't create a Money Reward that pays nothing or less!")
			.put(MONEY_REWARD_NOT_ENOUGH, "&cYou can't offer an amount of money that you don't have!")
			.put(MONEY_REWARD_NOT_A_NUMBER, "&cPayment has to be a Positive Integer!")

			//Items Reward Preview GUI
			.put(INVENTORY_ITEMS_REWARD_PREVIEW_TITLE, "Reward Preview (Esc to Return)")

			//Items Reward Offer GUI
			.put(INVENTORY_ITEMS_REWARD_OFFER_TITLE, "What would you like to offer?")
			.put(INVENTORY_ITEMS_JOB_NO_ITEMS_WARNING, "&cJob creation cancelled because you didn't offer any item.")
			.put(INVENTORY_ITEMS_REWARD_OFFER_CONFIRMATION_ITEM_NAME, "&aConfirm and Continue")
			.put(INVENTORY_ITEMS_REWARD_OFFER_CONFIRMATION_ITEM_LORE, "&fClick to offer the items you added as the job's reward.", "&fExiting this inventory would cancel the job.")

			//Goal Enchantment Selection GUI
			.put(INVENTORY_GOAL_ENCHANTMENT_SELECTION_TITLE, "Choose an Enchantment:")
			.put(INVENTORY_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE, "&fClick to add this Enchantment to the Goal.")
			.put(ENTER_ENCHANTMENT_LEVEL, "&fWhat level for &a%enchantment%&f?")
			.put(ENCHANTMENT_LEVEL_NOT_A_NUMBER, "&cThe Level must be an Integer!")
			.put(ENCHANTMENT_LEVEL_OUT_OF_BOUNDS, "&cThe provided level is out of bounds! (&4%enchantment min level%&c-&4%enchantment max level%&c)")

			//Goal Customization GUI
			.put(INVENTORY_GOAL_CUSTOMIZATION_TITLE, "What should the Goal Item be?")
			.put(INVENTORY_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME, "&aCurrent Goal")
			.put(INVENTORY_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME, "&c&lCurrent Goal: None")
			.put(INVENTORY_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME, "&a&lFinish")
			.put(INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME, "&aType")
			.put(INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE, "&fClick to set the type of the goal.")
			.put(INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME, "&6Amount")
			.put(INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE, "&fClick to set the amount of the goal.")
			.put(INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME, "&dEnchantments")
			.put(INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE, "&fClick to add an enchantment that", "&fthe goal must have on it.")
			.put(ITEM_GOAL_FORMAT_QUESTION, "&fWhich &aitem &fdo you need? Reply with the name of it!")
			.put(ITEM_GOAL_INVALID, "&cThe specified goal is either incorrectly formatted or unachievable!")
			
			//Item Palette GUI
			.put(INVENTORY_ITEM_PALETTE_TITLE, "Select the Goal Item:")
			.put(INVENTORY_ITEM_PALETTE_BACK_ITEM_NAME, "&cBack")
			.put(INVENTORY_ITEM_PALETTE_NEXT_ITEM_NAME, "&aNext")
			.put(INVENTORY_ITEM_PALETTE_ENGLISH_SEARCH_ITEM_NAME, "&aSearch By English Name")
			
			//Goal Amount GUI
			.put(INVENTORY_GOAL_AMOUNT_TITLE, "Specify the Amount:")
			.put(INVENTORY_GOAL_AMOUNT_FINISH_ITEM_NAME, "&aContinue")
			.put(INVENTORY_GOAL_AMOUNT_FINISH_ITEM_LORE, "&fClick to set the new amount.")
			.put(INVENTORY_GOAL_AMOUNT_NUMERIC_AMOUNT_TITLE, "&cEnter Numeric Amount:")
			
			//Job Added Notifiers GUI
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_TITLE, "Receive Notifications For:")
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_ALL_ITEM_NAME, "&aAll Jobs")
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_ALL_ITEM_LORE, "&fAll jobs whenever they're posted.")
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_NAME, "&aSubscriptions")
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_LORE, "&fJobs that items for which", "&fYou were subscribed via &e/emp subscriptions&f.")
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_NONE_ITEM_NAME, "&aNone")
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_NONE_ITEM_LORE, "&cNo Jobs &f(Default)")
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_SELECTED, "&bCurrently Selected");
}