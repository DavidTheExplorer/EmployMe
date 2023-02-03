package dte.employme.services.message;

import static dte.employme.messages.MessageKey.*;

import java.util.Map;

import dte.employme.messages.MessageBuilder;
import dte.employme.messages.MessageKey;
import dte.employme.utils.ChatColorUtils;
import dte.employme.utils.java.MapBuilder;
import dte.spigotconfiguration.SpigotConfig;

public class TranslatedMessageService implements MessageService
{
	private final SpigotConfig languageConfig;

	private static final Map<MessageKey, String> MESSAGE_KEY_PATHS = new MapBuilder<MessageKey, String>()

			//Jobs
			.put(JOB_ADDED_TO_BOARD, "Jobs.Added To Board")
			.put(MONEY_JOB_COMPLETED, "Jobs.Money Job Completed")
			.put(ITEMS_JOB_COMPLETED, "Jobs.Item Job Completed")
			.put(PLAYER_COMPLETED_YOUR_JOB, "Jobs.Player Completed Your Job")
			.put(PLAYER_PARTIALLY_COMPLETED_YOUR_JOB, "Jobs.Player Partially Completed Your Job")
			.put(YOU_OFFERED_TOO_MANY_JOBS, "Jobs.You Have Too Many Jobs")
			.put(JOB_SUCCESSFULLY_CANCELLED, "Jobs.Cancelled")
			.put(JOB_AUTO_REMOVED, "Jobs.Auto Removed")
			.put(CONVERSATION_ESCAPE_TITLE, "Jobs.Conversation Escape Title")
			
			//Job Added Notifiers
			.put(YOUR_NEW_JOB_ADDED_NOTIFIER_IS, "Job Add Notifiers.Your New Notifier Is")
			.put(NEW_JOB_POSTED, "Job Add Notifiers.New Job Posted")

			//Subscriptions
			.put(SUCCESSFULLY_SUBSCRIBED_TO_GOAL, "Subscriptions.Successfully Subscribed to Goal")
			.put(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, "Subscriptions.Successfully Unsubscribed from Goal")
			.put(SUBSCRIBED_TO_GOALS_NOTIFICATION, "Subscriptions.Subscription Notification")
			.put(YOUR_SUBSCRIPTIONS_ARE, "Subscriptions.Your Subscriptions Are")

			//General
			.put(PREFIX, "General.Prefix")
			.put(NONE, "General.None")
			.put(GET, "General.Get")
			.put(GOAL, "General.Goal")
			.put(REWARD, "General.Reward")
			.put(MUST_NOT_BE_CONVERSING, "General.Must Not Be Conversing")
			.put(NEW_UPDATE_AVAILABLE, "General.New Update Available")
			.put(CURRENCY_SYMBOL, "General.Currency Symbol")
			.put(PLUGIN_RELOADED, "General.Plugin Reloaded")
			.put(CONVERSATION_ESCAPE_WORD, "General.Conversation Escape Word")
			
			//Custom Items
			.put(INVALID_CUSTOM_ITEM_FORMAT, "Custom Items.Invalid Format")
			.put(CUSTOM_ITEM_NOT_FOUND, "Custom Items.Not Found Error")

			//Job Containers GUI
			.put(GUI_JOB_CONTAINERS_TITLE, "GUIs.Player Containers.Title")
			.put(GUI_JOB_CONTAINERS_REWARDS_CONTAINER_NAME, "GUIs.Player Containers.Items.Rewards Container.Name")
			.put(GUI_JOB_CONTAINERS_REWARDS_CONTAINER_LORE, "GUIs.Player Containers.Items.Rewards Container.Lore")
			.put(GUI_JOB_CONTAINERS_ITEMS_CONTAINER_NAME, "GUIs.Player Containers.Items.Items Container.Name")
			.put(GUI_JOB_CONTAINERS_ITEMS_CONTAINER_LORE, "GUIs.Player Containers.Items.Items Container.Lore")
			.put(CONTAINER_CLAIM_INSTRUCTION, "GUIs.Player Containers.Claim Instruction")

			//Player Container GUI
			.put(GUI_PLAYER_CONTAINER_NEXT_PAGE_NAME, "GUIs.Player Container.Items.Next Page.Name")
			.put(GUI_PLAYER_CONTAINER_NEXT_PAGE_LORE, "GUIs.Player Container.Items.Next Page.Lore")
			.put(GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_NAME, "GUIs.Player Container.Items.Back.Name")
			.put(GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_LORE, "GUIs.Player Container.Items.Back.Lore")

			//Job Icon
			.put(JOB_ICON_NAME, "Items.Job Icon.Name")
			.put(JOB_ICON_GOAL_INSTRUCTIONS, "Items.Job Icon.Goal Instructions")
			.put(JOB_ICON_CUSTOM_GOAL_INSTRUCTIONS, "Items.Job Icon.Custom Goal Instructions")
			.put(JOB_ICON_ENCHANT_DESCRIPTION, "Items.Job Icon.Enchant Description")
			.put(JOB_ICON_MONEY_PAYMENT_DESCRIPTION, "Items.Job Icon.Money Payment Description")
			.put(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION, "Items.Job Icon.Items Payment Description")

			//Job Board GUI
			.put(GUI_JOB_BOARD_TITLE, "GUIs.Job Board.Title")
			.put(GUI_JOB_BOARD_OFFER_COMPLETED, "GUIs.Job Board.Offer Completed")
			.put(GUI_JOB_BOARD_OFFER_NOT_COMPLETED, "GUIs.Job Board.Offer Not Completed")
			.put(GUI_JOB_BOARD_OFFER_PARTIALLY_COMPLETED, "GUIs.Job Board.Offer Partially Completed")
			.put(GUI_JOB_BOARD_JOB_NOT_CONTAINED, "GUIs.Job Board.Offer Not Contained")
			.put(GUI_JOB_BOARD_PARTIAL_GOAL_AMOUNT_TO_USE_QUESTION, "GUIs.Job Board.Partial Goal Amount To Use Question")
			.put(GUI_JOB_BOARD_INVALID_PARTIAL_GOAL_AMOUNT_ERROR, "GUIs.Job Board.Invalid Partial Goal Amount Error")
			.put(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME, "GUIs.Job Board.Items.Your Jobs.Name")
			.put(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE, "GUIs.Job Board.Items.Your Jobs.Lore")
			.put(GUI_JOB_BOARD_NEXT_PAGE_NAME, "GUIs.Job Board.Items.Next Page.Name")
			.put(GUI_JOB_BOARD_NEXT_PAGE_LORE, "GUIs.Job Board.Items.Next Page.Lore")
			.put(GUI_JOB_BOARD_PREVIOUS_PAGE_NAME, "GUIs.Job Board.Items.Previous Page.Name")
			.put(GUI_JOB_BOARD_PREVIOUS_PAGE_LORE, "GUIs.Job Board.Items.Previous Page.Lore")

			//Player Jobs GUI
			.put(GUI_PLAYER_JOBS_TITLE, "GUIs.Player Jobs.Title")
			.put(GUI_PLAYER_JOBS_NEXT_PAGE_NAME, "GUIs.Player Jobs.Items.Next Page.Name")
			.put(GUI_PLAYER_JOBS_NEXT_PAGE_LORE, "GUIs.Player Jobs.Items.Next Page.Lore")
			.put(GUI_PLAYER_JOBS_PREVIOUS_PAGE_NAME, "GUIs.Player Jobs.Items.Previous Page.Name")
			.put(GUI_PLAYER_JOBS_PREVIOUS_PAGE_LORE, "GUIs.Player Jobs.Items.Previous Page.Lore")

			//Job Deletion GUI
			.put(GUI_JOB_DELETION_TITLE, "GUIs.Job Deletion.Title")
			.put(GUI_JOB_DELETION_DELETE_INSTRUCTION, "GUIs.Job Deletion.Instruction")

			//Job Creation GUI
			.put(GUI_JOB_CREATION_TITLE, "GUIs.Job Creation.Title")
			.put(GUI_JOB_CREATION_MONEY_JOB_ICON_NAME, "GUIs.Job Creation.Items.Money Job.Name")
			.put(GUI_JOB_CREATION_MONEY_JOB_ICON_LORE, "GUIs.Job Creation.Items.Money Job.Lore")
			.put(GUI_JOB_CREATION_ITEMS_JOB_ICON_NAME, "GUIs.Job Creation.Items.Items Job.Name")
			.put(GUI_JOB_CREATION_ITEMS_JOB_ICON_LORE, "GUIs.Job Creation.Items.Items Job.Lore")
			.put(MONEY_PAYMENT_AMOUNT_QUESTION, "GUIs.Job Creation.Money Payment Amount Question")
			.put(MONEY_REWARD_ERROR_NEGATIVE, "GUIs.Job Creation.Money Reward Negative Error")
			.put(MONEY_REWARD_NOT_ENOUGH, "GUIs.Job Creation.Money Reward Not Enough")
			.put(MONEY_REWARD_NOT_A_NUMBER, "GUIs.Job Creation.Money Reward Not A Number")

			//Items Reward Preview GUI
			.put(GUI_ITEMS_REWARD_PREVIEW_TITLE, "GUIs.Items Reward Preview.Title")

			//Items Reward Offer GUI
			.put(GUI_ITEMS_REWARD_OFFER_TITLE, "GUIs.Items Reward Offer.Title")
			.put(GUI_ITEMS_JOB_NO_ITEMS_WARNING, "GUIs.Items Reward Offer.No Items Warning")
			.put(GUI_ITEMS_REWARD_OFFER_CONFIRMATION_ITEM_NAME, "GUIs.Items Reward Offer.Items.Confirmation Item.Name")
			.put(GUI_ITEMS_REWARD_OFFER_CONFIRMATION_ITEM_LORE, "GUIs.Items Reward Offer.Items.Confirmation Item.Lore")

			//Goal Enchantment Selection GUI
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_TITLE, "GUIs.Goal Enchantment Selection.Title")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE, "GUIs.Goal Enchantment Selection.Items.Goal Enchantment Selection.Lore")
			.put(ENTER_ENCHANTMENT_LEVEL, "GUIs.Goal Enchantment Selection.Enter Enchantment Level")
			.put(ENCHANTMENT_LEVEL_NOT_A_NUMBER, "GUIs.Goal Enchantment Selection.Enchantment Level Not A Number")
			.put(ENCHANTMENT_LEVEL_OUT_OF_BOUNDS, "GUIs.Goal Enchantment Selection.Enchantment Level Out Of Bounds")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_PREVIOUS_PAGE_NAME, "GUIs.Goal Enchantment Selection.Items.Previous Page.Name")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_PREVIOUS_PAGE_LORE, "GUIs.Goal Enchantment Selection.Items.Previous Page.Lore")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_NEXT_PAGE_NAME, "GUIs.Goal Enchantment Selection.Items.Next Page.Name")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_NEXT_PAGE_LORE, "GUIs.Goal Enchantment Selection.Items.Next Page.Lore")

			//Goal Customization GUI
			.put(GUI_GOAL_CUSTOMIZATION_TITLE, "GUIs.Goal Customization.Title")
			.put(GUI_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME, "GUIs.Goal Customization.Items.Current Item.Name")
			.put(GUI_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME, "GUIs.Goal Customization.Items.No Current Item.Name")
			.put(GUI_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME, "GUIs.Goal Customization.Items.Finish Item.Name")
			.put(GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME, "GUIs.Goal Customization.Items.Type Item.Name")
			.put(GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE, "GUIs.Goal Customization.Items.Type Item.Lore")
			.put(GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_CUSTOM_ITEM_SUPPORT, "GUIs.Goal Customization.Items.Type Item.Custom Item Support")
			.put(GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME, "GUIs.Goal Customization.Items.Amount Item.Name")
			.put(GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE, "GUIs.Goal Customization.Items.Amount Item.Lore")
			.put(GUI_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME, "GUIs.Goal Customization.Items.Enchantments Item.Name")
			.put(GUI_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE, "GUIs.Goal Customization.Items.Enchantments Item.Lore")
			.put(ITEM_GOAL_FORMAT_QUESTION, "GUIs.Goal Customization.Item Goal Format Question")
			.put(ITEM_GOAL_INVALID, "GUIs.Goal Customization.Invalid Goal Item")
			.put(ITEM_GOAL_BLOCKED_IN_YOUR_WORLD, "GUIs.Goal Customization.Item Goal Blocked In Your World")
			.put(GOAL_AMOUNT_QUESTION, "GUIs.Goal Customization.Goal Amount Question")
			.put(GOAL_AMOUNT_MUST_BE_POSITIVE, "GUIs.Goal Customization.Goal Amount Must Be Positive")
			.put(GOAL_AMOUNT_NOT_A_NUMBER, "GUIs.Goal Customization.Goal Amount Not A Number")

			//Item Palette GUI
			.put(GUI_ITEM_PALETTE_TITLE, "GUIs.Item Palette.Title")
			.put(GUI_ITEM_PALETTE_NEXT_ITEM_NAME, "GUIs.Item Palette.Items.Next Item.Name")
			.put(GUI_ITEM_PALETTE_BACK_ITEM_NAME, "GUIs.Item Palette.Items.Back Item.Name")
			.put(GUI_ITEM_PALETTE_ENGLISH_SEARCH_ITEM_NAME, "GUIs.Item Palette.Items.English Search Item.Name")

			//Job Added Notifiers GUI
			.put(GUI_JOB_ADDED_NOTIFIERS_TITLE, "GUIs.Job Added Notifiers.Title")
			.put(GUI_JOB_ADDED_NOTIFIERS_ALL_ITEM_NAME, "GUIs.Job Added Notifiers.Items.All Jobs.Name")
			.put(GUI_JOB_ADDED_NOTIFIERS_ALL_ITEM_LORE, "GUIs.Job Added Notifiers.Items.All Jobs.Lore")
			.put(GUI_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_NAME, "GUIs.Job Added Notifiers.Items.Material Subscriptions.Name")
			.put(GUI_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_LORE, "GUIs.Job Added Notifiers.Items.Material Subscriptions.Lore")
			.put(GUI_JOB_ADDED_NOTIFIERS_NONE_ITEM_NAME, "GUIs.Job Added Notifiers.Items.No Jobs.Name")
			.put(GUI_JOB_ADDED_NOTIFIERS_NONE_ITEM_LORE, "GUIs.Job Added Notifiers.Items.No Jobs.Lore")
			.put(GUI_JOB_ADDED_NOTIFIERS_SELECTED, "GUIs.Job Added Notifiers.Currently Selected")
			
			//Player Subscriptions GUI
			.put(GUI_PLAYER_SUBSCRIPTIONS_TITLE, "GUIs.Player Subscriptions.Title")
			.put(GUI_PLAYER_SUBSCRIPTIONS_YOUR_SUBSCRIPTIONS_ITEM_NAME, "GUIs.Player Subscriptions.Items.Your Subscriptions.Name")
			.put(GUI_PLAYER_SUBSCRIPTIONS_YOUR_SUBSCRIPTIONS_ITEM_LORE, "GUIs.Player Subscriptions.Items.Your Subscriptions.Lore")
			.put(GUI_PLAYER_SUBSCRIPTIONS_SUBSCRIBE_ITEM_NAME, "GUIs.Player Subscriptions.Items.Subscribe.Name")
			.put(GUI_PLAYER_SUBSCRIPTIONS_SUBSCRIBE_ITEM_LORE, "GUIs.Player Subscriptions.Items.Subscribe.Lore")
			.put(GUI_PLAYER_SUBSCRIPTIONS_UNSUBSCRIBE_ITEM_NAME, "GUIs.Player Subscriptions.Items.Unsubscribe.Name")
			.put(GUI_PLAYER_SUBSCRIPTIONS_UNSUBSCRIBE_ITEM_LORE, "GUIs.Player Subscriptions.Items.Unsubscribe.Lore")
			
			//Custom Goal Selection GUI
			.put(GUI_CUSTOM_GOAL_SELECTION_TITLE, "GUIs.Custom Goal Selection.Title")
			.put(GUI_CUSTOM_GOAL_SELECTION_MORE_PLUGINS_SOON_ITEM_NAME, "GUIs.Custom Goal Selection.Items.More Plugins Soon.Name")
			.put(GUI_CUSTOM_GOAL_SELECTION_ITEM_PROVIDER_ITEM_NAME, "GUIs.Custom Goal Selection.Items.Item Provider.Name")
			.put(GUI_CUSTOM_GOAL_SELECTION_ITEM_PROVIDER_ITEM_LORE, "GUIs.Custom Goal Selection.Items.Item Provider.Lore")
			
			//Subscribe Item Palette
			.put(GUI_SUBSCRIBE_ITEM_PALETTE_TITLE, "GUIs.Subscribe Item Palette.Title")
			.put(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_QUESTION, "GUIs.Subscribe Item Palette.Subscribe Question")
			.put(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_ITEM_NAME, "GUIs.Subscribe Item Palette.Items.Name")
			.put(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_ITEM_LORE, "GUIs.Subscribe Item Palette.Items.Lore")
			
			//Unsubscribe from Items Item Palette
			.put(GUI_UNSUBSCRIBE_ITEM_PALETTE_TITLE, "GUIs.Unsubscribe From Items.Title")
			.put(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_QUESTION, "GUIs.Unsubscribe From Items.Unsubscribe Question")
			.put(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_NAME, "GUIs.Unsubscribe From Items.Items.Name")
			.put(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_LORE, "GUIs.Unsubscribe From Items.Items.Lore")
			.build();

	public TranslatedMessageService(SpigotConfig languageConfig) 
	{
		this.languageConfig = languageConfig;
	}

	@Override
	public MessageBuilder getMessage(MessageKey key) 
	{
		Object message = this.languageConfig.get(getConfigPath(key));

		MessageBuilder messageBuilder = MessageBuilder.from(message).map(ChatColorUtils::colorize);
		
		if(key.shouldBePrefixed())
			messageBuilder.prefixed(getMessage(PREFIX).first());
		
		return messageBuilder;
	}

	public static String getConfigPath(MessageKey key) 
	{
		return MESSAGE_KEY_PATHS.get(key);
	}
}