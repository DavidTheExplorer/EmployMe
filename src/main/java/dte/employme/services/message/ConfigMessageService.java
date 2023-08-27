package dte.employme.services.message;

import static dte.employme.messages.MessageKey.*;

import java.util.Map;

import dte.employme.messages.MessageBuilder;
import dte.employme.messages.MessageKey;
import dte.employme.utils.ChatColorUtils;
import dte.employme.utils.java.MapBuilder;
import dte.spigotconfiguration.SpigotConfig;

public class ConfigMessageService implements MessageService
{
	private final SpigotConfig languageConfig;

	private static final Map<MessageKey, String> MESSAGE_KEY_PATHS;

	public ConfigMessageService(SpigotConfig languageConfig) 
	{
		this.languageConfig = languageConfig;
	}

	@Override
	public MessageBuilder loadMessage(MessageKey key) 
	{
		Object message = this.languageConfig.get(getConfigPath(key));

		MessageBuilder messageBuilder = MessageBuilder.from(message)
				.map(ChatColorUtils::colorize);

		//recursive call to prefix the message
		if(key != PREFIX)
			messageBuilder.inject("prefix", loadMessage(PREFIX).first());

		return messageBuilder;
	}

	public static String getConfigPath(MessageKey key) 
	{
		return MESSAGE_KEY_PATHS.get(key);
	}

	static 
	{
		MESSAGE_KEY_PATHS = new MapBuilder<MessageKey, String>()

				//General
				.put(PREFIX, "General.Prefix")
				.put(MUST_NOT_BE_CONVERSING, "General.Must Not Be Conversing")
				.put(NEW_UPDATE_AVAILABLE, "General.New Update Available")
				.put(CURRENCY_SYMBOL, "General.Currency Symbol")
				.put(PLUGIN_RELOADED, "General.Plugin Reloaded")
				.put(CONVERSATION_ESCAPE_WORD, "General.Conversation Escape Word")
				.put(CONVERSATION_ESCAPE_TITLE, "General.Conversation Escape Title")
				.put(NONE, "General.None")
				.put(GET, "General.Get")
				.put(GOAL, "General.Goal")
				.put(REWARD, "General.Reward")

				//Jobs
				.put(JOB_ADDED_TO_BOARD, "Jobs.Added To Board")
				.put(MONEY_JOB_COMPLETED, "Jobs.Money Job Completed")
				.put(ITEMS_JOB_COMPLETED, "Jobs.Item Job Completed")
				.put(PLAYER_COMPLETED_YOUR_JOB, "Jobs.Player Completed Your Job")
				.put(PLAYER_PARTIALLY_COMPLETED_YOUR_JOB, "Jobs.Player Partially Completed Your Job")
				.put(CANNOT_OFFER_MORE_JOBS, "Jobs.Cannot Offer More Jobs")
				.put(JOB_SUCCESSFULLY_CANCELLED, "Jobs.Job Cancelled")
				.put(JOB_AUTO_REMOVED, "Jobs.Job Auto Removed")
				.put(NEW_JOB_POSTED, "Jobs.New Job Posted")
				.put(JOB_NOT_AVAILABLE_ANYMORE, "Jobs.Job Not Available")

				//Job Added Notifiers
				.put(YOUR_NEW_JOB_ADDED_NOTIFIER_IS, "Job Added Notifiers.Your New Notifier Is")

				//Goal Subscriptions
				.put(GOAL_SUBSCRIPTION_QUESTION, "Goal Subscriptions.Subscription Question")
				.put(GOAL_UNSUBSCRIPTION_QUESTION, "Goal Subscriptions.Unsubscription Question")
				.put(SUCCESSFULLY_SUBSCRIBED_TO_GOAL, "Goal Subscriptions.Successfully Subscribed to Goal")
				.put(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, "Goal Subscriptions.Successfully Unsubscribed from Goal")
				.put(JOB_MATERIAL_NOTIFIER_NOTIFICATION, "Goal Subscriptions.Item Subscription Notification")
				.put(YOUR_SUBSCRIPTIONS_ARE, "Goal Subscriptions.Your Subscriptions Are")

				//Custom Items
				.put(INVALID_CUSTOM_ITEM_FORMAT, "Custom Items.Invalid Format")
				.put(CUSTOM_ITEM_NOT_FOUND, "Custom Items.Not Found Error")

				//Live Updates
				.put(LIVE_UPDATES_JOB_COMPLETED, "Live Updates.Job Completed")
				.put(LIVE_UPDATES_TRACKER_ACTIONBAR, "Live Updates.Tracker Action Bar")

				//Goal
				.put(GOAL_QUESTION, "Goal.Question")
				.put(INVALID_GOAL, "Goal.Invalid Goal Error")
				.put(PARTIAL_GOAL_AMOUNT_QUESTION, "Goal.Partial Amount Question")
				.put(INVALID_PARTIAL_GOAL_AMOUNT, "Goal.Invalid Partial Amount Error")
				.put(GOAL_BLOCKED_IN_YOUR_WORLD, "Goal.Blocked In Your World")
				.put(GOAL_AMOUNT_QUESTION, "Goal.Amount Question")
				.put(GOAL_AMOUNT_MUST_BE_POSITIVE, "Goal.Amount Must Be Positive")
				.put(GOAL_AMOUNT_NOT_A_NUMBER, "Goal.Amount Not A Number")
				.put(GOAL_ENCHANTMENT_LEVEL_QUESTION, "Goal.Enter Enchantment Level")
				.put(GOAL_ENCHANTMENT_LEVEL_NOT_A_NUMBER, "Goal.Enchantment Level Not A Number")
				.put(GOAL_ENCHANTMENT_LEVEL_OUT_OF_BOUNDS, "Goal.Enchantment Level Out Of Bounds")

				//Rewards
				.put(MONEY_REWARD_AMOUNT_QUESTION, "Rewards.Money.Amount Question")
				.put(MONEY_REWARD_NEGATIVE, "Rewards.Money.Negative Error")
				.put(MONEY_REWARD_NOT_ENOUGH, "Rewards.Money.Not Enough Error")
				.put(MONEY_REWARD_NOT_A_NUMBER, "Rewards.Money.Not A Number Error")

				//Commands
				.put(COMMAND_VIEW_NAME, "Commands.View.Name")
				.put(COMMAND_VIEW_DESCRIPTION, "Commands.View.Description")
				.put(COMMAND_OFFER_NAME, "Commands.Offer.Name")
				.put(COMMAND_OFFER_DESCRIPTION, "Commands.Offer.Description")
				.put(COMMAND_MYCONTAINERS_NAME, "Commands.My Containers.Name")
				.put(COMMAND_MYCONTAINERS_DESCRIPTION, "Commands.My Containers.Description")
				.put(COMMAND_ADDNOTIFIERS_NAME, "Commands.Add Notifiers.Name")
				.put(COMMAND_ADDNOTIFIERS_DESCRIPTION, "Commands.Add Notifiers.Description")
				.put(COMMAND_MYSUBSCRIPTIONS_NAME, "Commands.My Subscriptions.Name")
				.put(COMMAND_MYSUBSCRIPTIONS_DESCRIPTION, "Commands.My Subscriptions.Description")
				.put(COMMAND_STOPLIVEUPDATES_NAME, "Commands.Stop Live Updates.Name")
				.put(COMMAND_STOPLIVEUPDATES_DESCRIPTION, "Commands.Stop Live Updates.Description")
				.put(COMMAND_RELOAD_NAME, "Commands.Reload.Name")
				.put(COMMAND_RELOAD_DESCRIPTION, "Commands.Reload.Description")
				.put(COMMAND_HELP_NAME, "Commands.Help.Name")
				.put(COMMAND_HELP_DESCRIPTION, "Commands.Help.Description")

				//Job Icon
				.put(JOB_ICON_NAME, "Items.Job Icon.Name")
				.put(JOB_ICON_GOAL_INSTRUCTIONS, "Items.Job Icon.Goal Instructions")
				.put(JOB_ICON_CUSTOM_GOAL_INSTRUCTIONS, "Items.Job Icon.Custom Goal Instructions")
				.put(JOB_ICON_ENCHANT_DESCRIPTION, "Items.Job Icon.Enchant Description")
				.put(JOB_ICON_MONEY_PAYMENT_DESCRIPTION, "Items.Job Icon.Money Payment Description")
				.put(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION, "Items.Job Icon.Items Payment Description")
				.put(JOB_ICON_VIEW_ACTIONS_DESCRIPTION, "Items.Job Icon.View Actions Description")
				.build();
	}
}