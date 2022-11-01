package dte.employme.configs;

import static dte.employme.messages.MessageKey.*;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import dte.employme.messages.MessageKey;
import dte.employme.services.message.TranslatedMessageService;
import dte.spigotconfiguration.SpigotConfig;
import dte.spigotconfiguration.exceptions.ConfigLoadException;

public class MessagesConfig extends SpigotConfig
{
	public MessagesConfig(Plugin plugin, Messages defaultMessages) throws ConfigLoadException
	{
		super(new Builder(plugin)
				.byPath("messages")
				.supplyDefaults(config -> 
				{
					//regenerate the missing messages based on the provided defaults
					return Arrays.stream(MessageKey.VALUES)
							.filter(key -> isMissing(config, key))
							.collect(toMap(TranslatedMessageService::getConfigPath, missingKey -> getDefaultMessage(missingKey, defaultMessages)));
				})
				.loadDefaults());
	}

	private static boolean isMissing(YamlConfiguration config, MessageKey key) 
	{
		return !config.contains(TranslatedMessageService.getConfigPath(key));
	}

	private static Object getDefaultMessage(MessageKey missingKey, Messages defaultMessages) 
	{
		String[] lines = defaultMessages.getLines(missingKey);

		return lines.length == 1 ? lines[0] : Arrays.asList(lines);
	}



	public static class Messages
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
	}



	public static final Messages ENGLISH = new Messages()

			//Jobs
			.put(JOB_ADDED_TO_BOARD, "&aYour offer was added to the &eJobs Board&a!")
			.put(MONEY_JOB_COMPLETED, "&aYou successfully completed this Job!")
			.put(ITEMS_JOB_COMPLETED, "&aJob Completed. You can access your items via &b\"/employment mycontainers\"")
			.put(PLAYER_COMPLETED_YOUR_JOB, "&b%completer% &djust completed one of your Jobs! &nHover for more info.")
			.put(PLAYER_PARTIALLY_COMPLETED_YOUR_JOB, "&b%completer% &djust partially completed one of your Jobs! &nHover for more info.")
			.put(YOU_OFFERED_TOO_MANY_JOBS, "&cYou can only offer up to %max jobs allowed% Jobs! Delete one to proceed.")
			.put(JOB_SUCCESSFULLY_CANCELLED, "&aReward &frefunded due to cancelling the Job!", "&fItem rewards can be accessed via &b\"/employment mycontainers\"&f.")
			.put(JOB_AUTO_REMOVED, "&cYour job was &4auto-removed &cbecause no one completed it!")

			//Job Added Notifiers
			.put(YOUR_NEW_JOB_ADDED_NOTIFIER_IS, "&aYou will get notifications for &e%job added notifier%&a!")
			.put(NEW_JOB_POSTED, "&fA new job was posted in the &aJobs Board&f!")

			//Subscriptions
			.put(SUCCESSFULLY_SUBSCRIBED_TO_GOAL, "&fYou just &asubscribed &fto &e%goal% &fJobs!")
			.put(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, "&fSuccessfully &4unsubscribed &ffrom &e%goal% &fJobs.")
			.put(SUBSCRIBED_TO_GOALS_NOTIFICATION, "&fA player just posted a job that offers &b&l%rewards%&f!")
			.put(YOUR_SUBSCRIPTIONS_ARE, "&fYou are subscribed to: &6%goal subscriptions%")

			//General
			.put(PREFIX, "&2[&aEmployMe&2] ")
			.put(NONE, "None")
			.put(GET, "Get")
			.put(GOAL, "Goal")
			.put(REWARD, "Reward")
			.put(MUST_NOT_BE_CONVERSING, "&cYou have to finish your current conversation.")
			.put(NEW_UPDATE_AVAILABLE, "&fPlease update &fto the lastest version! (&e%new version%&f)")
			.put(CURRENCY_SYMBOL, "$")
			.put(PLUGIN_RELOADED, "&fReload completed in &a%reload time%ms&f!")

			//Job Containers GUI
			.put(GUI_JOB_CONTAINERS_TITLE, "Personal Job Containers")
			.put(GUI_JOB_CONTAINERS_REWARDS_CONTAINER_NAME, "&dRewards")
			.put(GUI_JOB_CONTAINERS_REWARDS_CONTAINER_LORE, "&fThis is where Reward Items are stored", "&fafter you complete a job that pays them.")
			.put(GUI_JOB_CONTAINERS_ITEMS_CONTAINER_NAME, "&dItems")
			.put(GUI_JOB_CONTAINERS_ITEMS_CONTAINER_LORE, "&fWhen someone completes one of your jobs,", "&fThe items they got for you are stored here.")
			.put(CONTAINER_CLAIM_INSTRUCTION, "Claim your %container subject%:")

			//Player Container GUI
			.put(GUI_PLAYER_CONTAINER_NEXT_PAGE_NAME, "&aNext")
			.put(GUI_PLAYER_CONTAINER_NEXT_PAGE_LORE, "&fClick to open the next page")
			.put(GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_NAME, "&cBack")
			.put(GUI_PLAYER_CONTAINER_PREVIOUS_PAGE_LORE, "&fClick to open the previous page")

			//Job Icon
			.put(JOB_ICON_NAME, "&a%employer%'s Offer")
			.put(JOB_ICON_GOAL_INSTRUCTIONS, "&b&lGoal: &fI need &b%goal%&f.")
			.put(JOB_ICON_ENCHANT_DESCRIPTION, "&dEnchanted &fwith:")
			.put(JOB_ICON_MONEY_PAYMENT_DESCRIPTION, "&6&n&lPayment&6: &f%currency symbol%%money payment%")
			.put(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION, "&6&n&lPayment&6: &fRight Click to preview items(%items amount%)")

			//Job Board GUI
			.put(GUI_JOB_BOARD_TITLE, "Available Jobs")
			.put(GUI_JOB_BOARD_OFFER_COMPLETED, "&a&lClick to Finish!")
			.put(GUI_JOB_BOARD_OFFER_NOT_COMPLETED, "&cYou didn't complete this Job.")
			.put(GUI_JOB_BOARD_OFFER_PARTIALLY_COMPLETED, "&aYou can &2&lPartially complete &athis job!", "&aClick to continue")
			.put(GUI_JOB_BOARD_JOB_NOT_CONTAINED, "&cUnable to complete the job because it's not offered anymore!")
			.put(GUI_JOB_BOARD_PARTIAL_GOAL_AMOUNT_TO_USE_QUESTION, "&fYour inventory has &a%goal amount% &fitems, how many of them should be used for completion?")
			.put(GUI_JOB_BOARD_INVALID_PARTIAL_GOAL_AMOUNT_ERROR, "Invalid amount! Please use a positive amount that is is within 1 and the job's goal amount.")
			.put(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_NAME, "&aYour Jobs")
			.put(GUI_JOB_BOARD_PERSONAL_JOBS_ITEM_LORE, "&fView or Edit the jobs that you posted.")
			.put(GUI_JOB_BOARD_NEXT_PAGE_NAME, "&aNext")
			.put(GUI_JOB_BOARD_NEXT_PAGE_LORE, "&fClick to open the next page")
			.put(GUI_JOB_BOARD_PREVIOUS_PAGE_NAME, "&cBack")
			.put(GUI_JOB_BOARD_PREVIOUS_PAGE_LORE, "&fClick to open the previous page")

			//Player Jobs GUI
			.put(GUI_PLAYER_JOBS_TITLE, "Your Jobs")
			.put(GUI_PLAYER_JOBS_NEXT_PAGE_NAME, "&aNext")
			.put(GUI_PLAYER_JOBS_NEXT_PAGE_LORE, "&fClick to open the next page")
			.put(GUI_PLAYER_JOBS_PREVIOUS_PAGE_NAME, "&cBack")
			.put(GUI_PLAYER_JOBS_PREVIOUS_PAGE_LORE, "&fClick to open the previous page")

			//Job Deletion GUI
			.put(GUI_JOB_DELETION_TITLE, "Select Jobs to Delete")
			.put(GUI_JOB_DELETION_DELETE_INSTRUCTION, "&4&lClick to Delete!")

			//Job Creation GUI
			.put(GUI_JOB_CREATION_TITLE, "Create a new Job")
			.put(GUI_JOB_CREATION_MONEY_JOB_ICON_NAME, "&6Money Job")
			.put(GUI_JOB_CREATION_MONEY_JOB_ICON_LORE, "&fClick to offer a Job for which", "&fyou will pay a certain amount of money.")
			.put(GUI_JOB_CREATION_ITEMS_JOB_ICON_NAME, "&bItems Job")
			.put(GUI_JOB_CREATION_ITEMS_JOB_ICON_LORE, "&fClick to offer a Job for which", "&fyou will pay with resources.")
			.put(MONEY_PAYMENT_AMOUNT_QUESTION, "&fHow much will you &e&lPay&f? &f(Current Balance: &e&l%currency symbol%%player money%&6&f)")
			.put(MONEY_REWARD_ERROR_NEGATIVE, "&cCan't create a Money Reward that pays nothing or less!")
			.put(MONEY_REWARD_NOT_ENOUGH, "&cYou can't offer an amount of money that you don't have!")
			.put(MONEY_REWARD_NOT_A_NUMBER, "&cPayment has to be a Positive Integer!")

			//Items Reward Preview GUI
			.put(GUI_ITEMS_REWARD_PREVIEW_TITLE, "Reward Preview (Esc to Return)")

			//Items Reward Offer GUI
			.put(GUI_ITEMS_REWARD_OFFER_TITLE, "What would you like to offer?")
			.put(GUI_ITEMS_JOB_NO_ITEMS_WARNING, "&cJob creation cancelled because you didn't offer any item.")
			.put(GUI_ITEMS_REWARD_OFFER_CONFIRMATION_ITEM_NAME, "&aConfirm and Continue")
			.put(GUI_ITEMS_REWARD_OFFER_CONFIRMATION_ITEM_LORE, "&fClick to offer the items you added as the job's reward.", "&fExiting this inventory would cancel the job.")

			//Goal Enchantment Selection GUI
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_TITLE, "Choose an Enchantment:")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE, "&fClick to add this Enchantment to the Goal.")
			.put(ENTER_ENCHANTMENT_LEVEL, "&fWhat level for &a%enchantment%&f?")
			.put(ENCHANTMENT_LEVEL_NOT_A_NUMBER, "&cThe Level must be an Integer!")
			.put(ENCHANTMENT_LEVEL_OUT_OF_BOUNDS, "&cThe provided level is out of bounds! (min level is &4%enchantment min level%&c)")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_PREVIOUS_PAGE_NAME, "&cBack")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_PREVIOUS_PAGE_LORE, "&fClick to open the previous page")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_NEXT_PAGE_NAME, "&aNext")
			.put(GUI_GOAL_ENCHANTMENT_SELECTION_NEXT_PAGE_LORE, "&fClick to open the next page")

			//Goal Customization GUI
			.put(GUI_GOAL_CUSTOMIZATION_TITLE, "What should the Goal Item be?")
			.put(GUI_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME, "&aCurrent Goal")
			.put(GUI_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME, "&c&lCurrent Goal: None")
			.put(GUI_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME, "&a&lFinish")
			.put(GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME, "&aType")
			.put(GUI_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE, "&fClick to set the type of the goal.")
			.put(GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME, "&6Amount")
			.put(GUI_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE, "&fClick to set the amount of the goal.")
			.put(GUI_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME, "&dEnchantments")
			.put(GUI_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE, "&fClick to add an enchantment that", "&fthe goal must have on it.")
			.put(ITEM_GOAL_FORMAT_QUESTION, "&fWhich &aitem &fdo you need? Reply with the name of it!")
			.put(ITEM_GOAL_INVALID, "&cThe specified goal is either incorrectly formatted or unachievable!")
			.put(ITEM_GOAL_BLOCKED_IN_YOUR_WORLD, "&cYou cannot ask for this item in your world!")

			//Item Palette GUI
			.put(GUI_ITEM_PALETTE_TITLE, "Select the Goal Item:")
			.put(GUI_ITEM_PALETTE_BACK_ITEM_NAME, "&cBack")
			.put(GUI_ITEM_PALETTE_NEXT_ITEM_NAME, "&aNext")
			.put(GUI_ITEM_PALETTE_ENGLISH_SEARCH_ITEM_NAME, "&aSearch By English Name")

			//Goal Amount GUI
			.put(GUI_GOAL_AMOUNT_TITLE, "Specify the Amount:")
			.put(GUI_GOAL_AMOUNT_FINISH_ITEM_NAME, "&aContinue")
			.put(GUI_GOAL_AMOUNT_FINISH_ITEM_LORE, "&fClick to set the new amount.")
			.put(GUI_GOAL_AMOUNT_NUMERIC_AMOUNT_TITLE, "&cEnter Numeric Amount:")

			//Job Added Notifiers GUI
			.put(GUI_JOB_ADDED_NOTIFIERS_TITLE, "Receive Notifications For:")
			.put(GUI_JOB_ADDED_NOTIFIERS_ALL_ITEM_NAME, "&aAll Jobs")
			.put(GUI_JOB_ADDED_NOTIFIERS_ALL_ITEM_LORE, "&fAll jobs whenever they're posted.")
			.put(GUI_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_NAME, "&aSubscriptions")
			.put(GUI_JOB_ADDED_NOTIFIERS_SUBSCRIPTIONS_ITEM_LORE, "&fJobs that items for which", "&fYou were subscribed via &e/emp mysubscriptions&f.")
			.put(GUI_JOB_ADDED_NOTIFIERS_NONE_ITEM_NAME, "&aNone")
			.put(GUI_JOB_ADDED_NOTIFIERS_NONE_ITEM_LORE, "&cNo Jobs &f(Default)")
			.put(GUI_JOB_ADDED_NOTIFIERS_SELECTED, "&bCurrently Selected")

			//Player Subscriptions GUI
			.put(GUI_PLAYER_SUBSCRIPTIONS_TITLE, "Your Job Subscriptions")
			.put(GUI_PLAYER_SUBSCRIPTIONS_YOUR_SUBSCRIPTIONS_ITEM_NAME, "&dYour Subscriptions")
			.put(GUI_PLAYER_SUBSCRIPTIONS_YOUR_SUBSCRIPTIONS_ITEM_LORE, "&fSee what items you are subscribed for.")
			.put(GUI_PLAYER_SUBSCRIPTIONS_SUBSCRIBE_ITEM_NAME, "&bSubscribe")
			.put(GUI_PLAYER_SUBSCRIPTIONS_SUBSCRIBE_ITEM_LORE, "&fGet an instant notification once an", "&fitem you need is offered as a reward", "&ffor a job, once that job is posted.")
			.put(GUI_PLAYER_SUBSCRIPTIONS_UNSUBSCRIBE_ITEM_NAME, "&cUnsubscribe")
			.put(GUI_PLAYER_SUBSCRIPTIONS_UNSUBSCRIBE_ITEM_LORE, "&fRemove your subscription from a certain item.")

			//Subscribe Item Palette
			.put(GUI_SUBSCRIBE_ITEM_PALETTE_TITLE, "What item to subscribe for?")
			.put(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_QUESTION, "&fWhat item you want to subscribe to?")
			.put(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_ITEM_NAME, "&f%item%")
			.put(GUI_SUBSCRIBE_ITEM_PALETTE_SUBSCRIBE_ITEM_LORE, "&aSend me a notification for this item.")

			//Unsubscribe from Items Palette
			.put(GUI_UNSUBSCRIBE_ITEM_PALETTE_TITLE, "Notifications Removal")
			.put(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_QUESTION, "&fWhat item you want to unsubscribe from?")
			.put(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_NAME, "&f%item%")
			.put(GUI_UNSUBSCRIBE_ITEM_PALETTE_UNSUBSCRIBE_ITEM_LORE, "&cClick to stop getting notifications for this item.");
}
