package dte.employme.services.message;

import static dte.employme.messages.MessageKey.*;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.WordUtils;

import dte.employme.config.ConfigFile;
import dte.employme.messages.MessageBuilder;
import dte.employme.messages.MessageKey;
import dte.employme.utils.ChatColorUtils;
import dte.employme.utils.java.MapBuilder;

public class TranslatedMessageService implements MessageService
{
	private final ConfigFile languageConfig;

	private static final Map<MessageKey, String> MESSAGE_KEY_PATHS = new MapBuilder<MessageKey, String>()

			//Jobs
			.put(JOB_ADDED_TO_BOARD, toConfigPath("Jobs", "Added To Board"))
			.put(JOB_COMPLETED, toConfigPath("Jobs", "Completed"))
			.put(ITEMS_JOB_COMPLETED, toConfigPath("Jobs", "Item Job Completed"))
			.put(PLAYER_COMPLETED_YOUR_JOB, toConfigPath("Jobs", "Player Completed Your Job"))
			.put(YOU_OFFERED_TOO_MANY_JOBS, toConfigPath("Jobs", "You Have Too Many Jobs"))
			.put(JOB_CANCELLED_REWARD_REFUNDED, toConfigPath("Jobs", "Reward Refunded"))

			//Job Added Notifiers
			.put(YOUR_NEW_JOB_ADDED_NOTIFIER_IS, toConfigPath("Job Add Notifiers", "Your New Notifier Is"))
			.put(NEW_JOB_POSTED, toConfigPath("Job Add Notifiers", "New Job Posted"))

			//Subscriptions
			.put(SUCCESSFULLY_SUBSCRIBED_TO_GOAL, toConfigPath("Subscriptions", "Successfully Subscribed to Goal"))
			.put(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, toConfigPath("Subscriptions", "Successfully Unsubscribed from Goal"))
			.put(SUBSCRIBED_TO_GOALS_NOTIFICATION, toConfigPath("Subscriptions", "Subscription Notification"))
			.put(MUST_BE_SUBSCRIBED_TO_GOAL, toConfigPath("Subscriptions", "Must be Subscribed to Goal"))
			.put(YOUR_SUBSCRIPTIONS_ARE, toConfigPath("Subscriptions", "Your Subscriptions Are"))

			//General
			.put(PREFIX, toConfigPath("General", "Prefix"))
			.put(NONE, toConfigPath("General", "None"))
			.put(GET, toConfigPath("General", "Get"))
			.put(GOAL, toConfigPath("General", "Goal"))
			.put(REWARD, toConfigPath("General", "Reward"))
			.put(MUST_NOT_BE_CONVERSING, toConfigPath("General", "Must Not Be Conversing"))
			.put(MATERIAL_NOT_FOUND, toConfigPath("General", "Material Not Found"))
			.put(NEW_UPDATE_AVAILABLE, toConfigPath("General", "New Update Available"))
			.put(CURRENCY_SYMBOL, toConfigPath("General", "Currency Symbol"))
			.put(PLUGIN_RELOADED, toConfigPath("General", "Plugin Reloaded"))

			//Job Containers GUI
			.put(INVENTORY_JOB_CONTAINERS_TITLE, toConfigPath("Inventory", "Player Containers", "Title"))
			.put(INVENTORY_JOB_CONTAINERS_REWARDS_CONTAINER_NAME, toConfigPath("Inventory", "Player Containers", "Items", "Rewards Container", "Name"))
			.put(INVENTORY_JOB_CONTAINERS_REWARDS_CONTAINER_LORE, toConfigPath("Inventory", "Player Containers", "Items", "Rewards Container", "Lore"))
			.put(INVENTORY_JOB_CONTAINERS_ITEMS_CONTAINER_NAME, toConfigPath("Inventory", "Player Containers", "Items", "Items Container", "Name"))
			.put(INVENTORY_JOB_CONTAINERS_ITEMS_CONTAINER_LORE, toConfigPath("Inventory", "Player Containers", "Items", "Items Container", "Lore"))
			.put(CONTAINER_CLAIM_INSTRUCTION, toConfigPath("Inventory", "Player Containers", "Claim Instruction"))

			//Player Container GUI
			.put(INVENTORY_PLAYER_CONTAINER_NEXT_PAGE_NAME, toConfigPath("Inventory", "Player Container", "Items", "Next Page", "Name"))
			.put(INVENTORY_PLAYER_CONTAINER_NEXT_PAGE_LORE, toConfigPath("Inventory", "Player Container", "Items", "Next Page", "Lore"))
			.put(INVENTORY_PLAYER_CONTAINER_PREVIOUS_PAGE_NAME, toConfigPath("Inventory", "Player Container", "Items", "Back", "Name"))
			.put(INVENTORY_PLAYER_CONTAINER_PREVIOUS_PAGE_LORE, toConfigPath("Inventory", "Player Container", "Items", "Back", "Lore"))

			//Job Icon
			.put(JOB_ICON_NAME, toConfigPath("Items", "Job Icon", "Name"))
			.put(JOB_ICON_GOAL_INSTRUCTIONS, toConfigPath("Items", "Job Icon", "Goal Instructions"))
			.put(JOB_ICON_ENCHANT_DESCRIPTION, toConfigPath("Items", "Job Icon", "Enchant Description"))
			.put(JOB_ICON_MONEY_PAYMENT_DESCRIPTION, toConfigPath("Items", "Job Icon", "Money Payment Description"))
			.put(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION, toConfigPath("Items", "Job Icon", "Items Payment Description"))

			//Job Board GUI
			.put(INVENTORY_JOB_BOARD_TITLE, toConfigPath("Inventory", "Job Board", "Title"))
			.put(INVENTORY_JOB_BOARD_OFFER_COMPLETED, toConfigPath("Inventory", "Job Board", "Offer Completed"))
			.put(INVENTORY_JOB_BOARD_OFFER_NOT_COMPLETED, toConfigPath("Inventory", "Job Board", "Offer Not Completed"))
			.put(INVENTORY_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME, toConfigPath("Inventory", "Job Board", "Items", "Your Jobs", "Name"))
			.put(INVENTORY_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE, toConfigPath("Inventory", "Job Board", "Items", "Your Jobs", "Lore"))
			.put(INVENTORY_JOB_BOARD_NEXT_PAGE_NAME, toConfigPath("Inventory", "Job Board", "Items", "Next Page", "Name"))
			.put(INVENTORY_JOB_BOARD_NEXT_PAGE_LORE, toConfigPath("Inventory", "Job Board", "Items", "Next Page", "Lore"))
			.put(INVENTORY_JOB_BOARD_PREVIOUS_PAGE_NAME, toConfigPath("Inventory", "Job Board", "Items", "Previous Page", "Name"))
			.put(INVENTORY_JOB_BOARD_PREVIOUS_PAGE_LORE, toConfigPath("Inventory", "Job Board", "Items", "Previous Page", "Lore"))

			//Player Jobs GUI
			.put(INVENTORY_PLAYER_JOBS_TITLE, toConfigPath("Inventory", "Player Jobs", "Title"))
			.put(INVENTORY_PLAYER_JOBS_NEXT_PAGE_NAME, toConfigPath("Inventory", "Player Jobs", "Items", "Next Page", "Name"))
			.put(INVENTORY_PLAYER_JOBS_NEXT_PAGE_LORE, toConfigPath("Inventory", "Player Jobs", "Items", "Next Page", "Lore"))
			.put(INVENTORY_PLAYER_JOBS_PREVIOUS_PAGE_NAME, toConfigPath("Inventory", "Player Jobs", "Items", "Previous Page", "Name"))
			.put(INVENTORY_PLAYER_JOBS_PREVIOUS_PAGE_LORE, toConfigPath("Inventory", "Player Jobs", "Items", "Previous Page", "Lore"))

			//Job Deletion GUI
			.put(INVENTORY_JOB_DELETION_TITLE, toConfigPath("Inventory", "Job Deletion", "Title"))
			.put(INVENTORY_JOB_DELETION_DELETE_INSTRUCTION, toConfigPath("Inventory", "Job Deletion", "Instruction"))
			.put(JOB_SUCCESSFULLY_DELETED, toConfigPath("Inventory", "Job Deletion", "Job Successfully Deleted"))

			//Job Creation GUI
			.put(INVENTORY_JOB_CREATION_TITLE, toConfigPath("Inventory", "Job Creation", "Title"))
			.put(INVENTORY_JOB_CREATION_MONEY_JOB_ICON_NAME, toConfigPath("Inventory", "Job Creation", "Items", "Money Job", "Name"))
			.put(INVENTORY_JOB_CREATION_MONEY_JOB_ICON_LORE, toConfigPath("Inventory", "Job Creation", "Items", "Money Job", "Lore"))
			.put(INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_NAME, toConfigPath("Inventory", "Job Creation", "Items", "Items Job", "Name"))
			.put(INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_LORE, toConfigPath("Inventory", "Job Creation", "Items", "Items Job", "Lore"))
			.put(MONEY_PAYMENT_AMOUNT_QUESTION, toConfigPath("Inventory", "Job Creation", "Money Payment Amount Question"))
			.put(MONEY_REWARD_ERROR_NEGATIVE, toConfigPath("Inventory", "Job Creation", "Money Reward Negative Error"))
			.put(MONEY_REWARD_NOT_ENOUGH, toConfigPath("Inventory", "Job Creation", "Money Reward Not Enough"))
			.put(MONEY_REWARD_NOT_A_NUMBER, toConfigPath("Inventory", "Job Creation", "Money Reward Not A Number"))

			//Items Reward Preview GUI
			.put(INVENTORY_ITEMS_REWARD_PREVIEW_TITLE, toConfigPath("Inventory", "Items Reward Preview", "Title"))

			//Items Reward Offer GUI
			.put(INVENTORY_ITEMS_REWARD_OFFER_TITLE, toConfigPath("Inventory", "Items Reward Offer", "Title"))
			.put(INVENTORY_ITEMS_JOB_NO_ITEMS_WARNING, toConfigPath("Inventory", "Items Reward Offer", "No Items Warning"))
			.put(INVENTORY_ITEMS_REWARD_OFFER_CONFIRMATION_ITEM_NAME, toConfigPath("Inventory", "Items Reward Offer", "Items", "Confirmation Item", "Name"))
			.put(INVENTORY_ITEMS_REWARD_OFFER_CONFIRMATION_ITEM_LORE, toConfigPath("Inventory", "Items Reward Offer", "Items", "Confirmation Item", "Lore"))

			//Goal Enchantment Selection GUI
			.put(INVENTORY_GOAL_ENCHANTMENT_SELECTION_TITLE, toConfigPath("Inventory", "Goal Enchantment Selection", "Title"))
			.put(INVENTORY_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE, toConfigPath("Inventory", "Goal Enchantment Selection", "Items", "Goal Enchantment Selection", "Lore"))
			.put(ENTER_ENCHANTMENT_LEVEL, toConfigPath("Inventory", "Goal Enchantment Selection", "Enter Enchantment Level"))
			.put(ENCHANTMENT_LEVEL_NOT_A_NUMBER, toConfigPath("Inventory", "Goal Enchantment Selection", "Enchantment Level Not A Number"))
			.put(ENCHANTMENT_LEVEL_OUT_OF_BOUNDS, toConfigPath("Inventory", "Goal Enchantment Selection", "Enchantment Level Out Of Bounds"))

			//Goal Customization GUI
			.put(INVENTORY_GOAL_CUSTOMIZATION_TITLE, toConfigPath("Inventory", "Goal Customization", "Title"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME, toConfigPath("Inventory", "Goal Customization", "Items", "Current Item", "Name"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME, toConfigPath("Inventory", "Goal Customization", "Items", "No Current Item", "Name"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME, toConfigPath("Inventory", "Goal Customization", "Items", "Finish Item", "Name"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME, toConfigPath("Inventory", "Goal Customization", "Items", "Type Item", "Name"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE, toConfigPath("Inventory", "Goal Customization", "Items", "Type Item", "Lore"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME, toConfigPath("Inventory", "Goal Customization", "Items", "Amount Item", "Name"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE, toConfigPath("Inventory", "Goal Customization", "Items", "Amount Item", "Lore"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME, toConfigPath("Inventory", "Goal Customization", "Items", "Enchantments Item", "Name"))
			.put(INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE, toConfigPath("Inventory", "Goal Customization", "Items", "Enchantments Item", "Lore"))
			.put(ITEM_GOAL_FORMAT_QUESTION, toConfigPath("Inventory", "Goal Customization", "Item Goal Format Question"))
			.put(ITEM_GOAL_INVALID, toConfigPath("Inventory", "Goal Customization", "Invalid Goal Item"))

			//Item Palette GUI
			.put(INVENTORY_ITEM_PALETTE_TITLE, toConfigPath("Inventory", "Item Palette", "Title"))
			.put(INVENTORY_ITEM_PALETTE_NEXT_ITEM_NAME, toConfigPath("Inventory", "Item Palette", "Items", "Next Item", "Name"))
			.put(INVENTORY_ITEM_PALETTE_BACK_ITEM_NAME, toConfigPath("Inventory", "Item Palette", "Items", "Back Item", "Name"))
			.put(INVENTORY_ITEM_PALETTE_ENGLISH_SEARCH_ITEM_NAME, toConfigPath("Inventory", "Item Palette", "Items", "English Search Item", "Name"))

			//Goal Amount GUI
			.put(INVENTORY_GOAL_AMOUNT_TITLE, toConfigPath("Inventory", "Goal Amount", "Title"))
			.put(INVENTORY_GOAL_AMOUNT_FINISH_ITEM_NAME, toConfigPath("Inventory", "Goal Amount", "Items", "Finish Item", "Name"))
			.put(INVENTORY_GOAL_AMOUNT_FINISH_ITEM_LORE, toConfigPath("Inventory", "Goal Amount", "Items", "Finish Item", "Lore"))
			.put(INVENTORY_GOAL_AMOUNT_NUMERIC_AMOUNT_TITLE, toConfigPath("Inventory", "Goal Amount", "Numeric Amount Title"))

			//Job Added Notifiers GUI
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_TITLE, toConfigPath("Inventory", "Job Added Notifiers", "Title"))
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_ALL_ITEM_NAME, toConfigPath("Inventory", "Job Added Notifiers", "Items", "All Jobs", "Name"))
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_ALL_ITEM_LORE, toConfigPath("Inventory", "Job Added Notifiers", "Items", "All Jobs", "Lore"))
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_NAME, toConfigPath("Inventory", "Job Added Notifiers", "Items", "Material Subscriptions", "Name"))
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_LORE, toConfigPath("Inventory", "Job Added Notifiers", "Items", "Material Subscriptions", "Lore"))
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_NONE_ITEM_NAME, toConfigPath("Inventory", "Job Added Notifiers", "Items", "No Jobs", "Name"))
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_NONE_ITEM_LORE, toConfigPath("Inventory", "Job Added Notifiers", "Items", "No Jobs", "Lore"))
			.put(INVENTORY_JOB_ADDED_NOTIFIERS_SELECTED, toConfigPath("Inventory", "Job Added Notifiers", "Currently Selected"))
			.build();

	public TranslatedMessageService(ConfigFile languageConfig) 
	{
		this.languageConfig = languageConfig;
	}

	@Override
	public MessageBuilder getMessage(MessageKey key) 
	{
		Object message = this.languageConfig.getConfig().get(getConfigPath(key));

		return MessageBuilder.from(message).map(ChatColorUtils::colorize);
	}

	public static String getConfigPath(MessageKey key) 
	{
		return MESSAGE_KEY_PATHS.get(key);
	}

	private static String toConfigPath(String... path) 
	{
		return Arrays.stream(path)
				.map(key -> WordUtils.capitalizeFully(key).replace('_', ' '))
				.collect(joining("."));
	}
}