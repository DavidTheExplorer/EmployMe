package dte.employme.messages;

import static dte.employme.messages.MessageKey.*;

import java.util.HashMap;
import java.util.Map;

public class MessageProvider
{
	private final Map<MessageKey, String[]> messages = new HashMap<>();

	public MessageProvider put(MessageKey messageKey, String... lines) 
	{
		this.messages.put(messageKey, lines);
		return this;
	}

	public String[] provide(MessageKey messageKey) 
	{
		return this.messages.get(messageKey);
	}



	public static final MessageProvider ENGLISH = new MessageProvider()

			//General
			.put(PREFIX, "&2[&aEmployMe&2]")
			.put(MUST_NOT_BE_CONVERSING, "&cYou have to finish your current conversation.")
			.put(NEW_UPDATE_AVAILABLE, "%prefix% &fPlease update &fto the lastest version! (&e%new version%&f)")
			.put(CURRENCY_SYMBOL, "$")
			.put(PLUGIN_RELOADED, "%prefix% &fReload completed in &a%reload time%ms&f!")
			.put(CONVERSATION_ESCAPE_WORD, "cancel")
			.put(CONVERSATION_ESCAPE_TITLE, "&aImportant:", "&fSay &e%escape word% &fto leave the conversation!")
			.put(NONE, "None")
			.put(GET, "Get")
			.put(GOAL, "Goal")
			.put(REWARD, "Reward")

			//Jobs
			.put(JOB_ADDED_TO_BOARD, "%prefix% &aYour offer was added to the &eJobs Board&a!")
			.put(MONEY_JOB_COMPLETED, "%prefix% &aYou successfully completed this Job!")
			.put(ITEMS_JOB_COMPLETED, "%prefix% &aJob Completed. You can access your items via &b\"/employment mycontainers\"")
			.put(PLAYER_COMPLETED_YOUR_JOB, "%prefix% &b%completer% &djust completed one of your Jobs! &nHover for more info.")
			.put(PLAYER_PARTIALLY_COMPLETED_YOUR_JOB, "%prefix% &b%completer% &djust partially completed one of your Jobs! &nHover for more info.")
			.put(CANNOT_OFFER_MORE_JOBS, "&cYou can only offer up to %max jobs allowed% Jobs! Delete one to proceed.")
			.put(JOB_SUCCESSFULLY_CANCELLED, "%prefix% &aReward &frefunded due to cancelling the Job!", "&fItem rewards can be accessed via &b\"/employment mycontainers\"&f.")
			.put(JOB_AUTO_REMOVED, "%prefix% &cYour job was &4auto-removed &cbecause no one completed it!")
			.put(NEW_JOB_POSTED, "%prefix% &fA new job was posted in the &aJobs Board&f!")
			.put(JOB_NOT_AVAILABLE_ANYMORE, "&cUnable to proceed because the job is not offered anymore!")

			//Job Added Notifiers
			.put(YOUR_NEW_JOB_ADDED_NOTIFIER_IS, "%prefix% &aYou will get notifications for &e%job added notifier%&a!")

			//Goal Subscriptions
			.put(GOAL_SUBSCRIPTION_QUESTION, "&fWhat item you want to subscribe to?")
			.put(GOAL_UNSUBSCRIPTION_QUESTION, "&fWhat item you want to unsubscribe from?")
			.put(SUCCESSFULLY_SUBSCRIBED_TO_GOAL, "%prefix% &fYou just &asubscribed &fto &e%goal% &fJobs!")
			.put(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, "%prefix% &fSuccessfully &4unsubscribed &ffrom &e%goal% &fJobs.")
			.put(JOB_MATERIAL_NOTIFIER_NOTIFICATION, "%prefix% &fA player just posted a job that offers &b&l%rewards%&f!")
			.put(YOUR_SUBSCRIPTIONS_ARE, "%prefix% &fYou are subscribed to: &6%goal subscriptions%")

			//Custom Items
			.put(INVALID_CUSTOM_ITEM_FORMAT, "&cInvalid item format!")
			.put(CUSTOM_ITEM_NOT_FOUND, "Cannot find the specified item!")

			//Live Updates
			.put(LIVE_UPDATES_JOB_COMPLETED, "&aYou collected all items! Finish the job by &e/emp view")
			.put(LIVE_UPDATES_TRACKER_ACTIONBAR, "&bJob Tracker &f» &e%get% %goal%&f &f[&b&l%progression%&7&l%amount left%&f] (&b%completion percentage%%&f)")
			
			//Goal
			.put(GOAL_QUESTION, "&fWhich &aitem &fdo you need? Reply with the name of it!")
			.put(INVALID_GOAL, "&cThe specified goal is either incorrectly formatted or unachievable!")
			.put(PARTIAL_GOAL_AMOUNT_QUESTION, "&fYour inventory has &a%goal amount% &fitems, how many of them should be used for completion?")
			.put(INVALID_PARTIAL_GOAL_AMOUNT, "Invalid amount! Please use a positive amount that is is within 1 and the job's goal amount.")
			.put(GOAL_BLOCKED_IN_YOUR_WORLD, "&cThis item cannot be requested in your world!")
			.put(GOAL_AMOUNT_QUESTION, "&fEnter the amount you need:")
			.put(GOAL_AMOUNT_MUST_BE_POSITIVE, "&cThe goal amount must be positive!")
			.put(GOAL_AMOUNT_NOT_A_NUMBER, "&cThe goal amount must be a number!")
			.put(GOAL_ENCHANTMENT_LEVEL_QUESTION, "&fWhat level for &a%enchantment%&f?")
			.put(GOAL_ENCHANTMENT_LEVEL_NOT_A_NUMBER, "&cThe Level must be an Integer!")
			.put(GOAL_ENCHANTMENT_LEVEL_OUT_OF_BOUNDS, "&cThe provided level is out of bounds! (min level is &4%enchantment min level%&c)")

			//Rewards
			.put(MONEY_REWARD_AMOUNT_QUESTION, "&fHow much will you &e&lPay&f? &f(Current Balance: &e&l%currency symbol%%player money%&6&f)")
			.put(MONEY_REWARD_NEGATIVE, "&cCan't create a Money Reward that pays nothing or less!")
			.put(MONEY_REWARD_NOT_ENOUGH, "&cYou can't offer an amount of money that you don't have!")
			.put(MONEY_REWARD_NOT_A_NUMBER, "&cPayment has to be a Positive Integer!")
			
			//Commands
			.put(COMMAND_VIEW_NAME, "view")
			.put(COMMAND_VIEW_DESCRIPTION, "Search through the Available Jobs.")
			.put(COMMAND_OFFER_NAME, "offer")
			.put(COMMAND_OFFER_DESCRIPTION, "Offer a new job to the public.")
			.put(COMMAND_MYCONTAINERS_NAME, "mycontainers")
			.put(COMMAND_MYCONTAINERS_DESCRIPTION, "Obtain items from your containers.")
			.put(COMMAND_ADDNOTIFIERS_NAME, "addnotifiers")
			.put(COMMAND_ADDNOTIFIERS_DESCRIPTION, "Choose what kind of jobs you want to be notified about.")
			.put(COMMAND_MYSUBSCRIPTIONS_NAME, "mysubscriptions")
			.put(COMMAND_MYSUBSCRIPTIONS_DESCRIPTION, "Subscribe to rewards you need.")
			.put(COMMAND_STOPLIVEUPDATES_NAME, "stopliveupdates")
			.put(COMMAND_STOPLIVEUPDATES_DESCRIPTION, "Stop getting live updates for a job.")
			.put(COMMAND_RELOAD_NAME, "reload")
			.put(COMMAND_RELOAD_DESCRIPTION, "Reload the plugin.")
			.put(COMMAND_HELP_NAME, "help")
			.put(COMMAND_HELP_DESCRIPTION, "Get help about the plugin.")
			
			//Job Icon
			.put(JOB_ICON_NAME, "&a%employer%'s Offer")
			.put(JOB_ICON_GOAL_INSTRUCTIONS, "&b&lGoal&b: &fI need &b%goal%&f.")
			.put(JOB_ICON_CUSTOM_GOAL_INSTRUCTIONS, "&b&lGoal&b: &fI need &b%goal% &ffrom", "&fthe &e%item provider% &fplugin!")
			.put(JOB_ICON_ENCHANT_DESCRIPTION, "&dEnchanted &fwith:")
			.put(JOB_ICON_MONEY_PAYMENT_DESCRIPTION, "&6&lPayment&6: &fI will pay &6&l%currency symbol%%money payment%")
			.put(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION, "&6&lPayment&6: &fI will pay a list of items(Size: &6&l%items amount%&f)")
			.put(JOB_ICON_VIEW_ACTIONS_DESCRIPTION, "&a&l> &f&lClick&r &fto View Actions &ffor this job.");
}
