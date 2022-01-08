package dte.employme.config;

import static dte.employme.messages.MessageKey.CONTAINER_CLAIM_INSTRUCTION;
import static dte.employme.messages.MessageKey.CONTAINER_HELP_ITEM_NAME;
import static dte.employme.messages.MessageKey.CURRENCY_SYMBOL;
import static dte.employme.messages.MessageKey.ENCHANTMENT_LEVEL_NOT_A_NUMBER;
import static dte.employme.messages.MessageKey.ENCHANTMENT_LEVEL_OUT_OF_BOUNDS;
import static dte.employme.messages.MessageKey.ENTER_ENCHANTMENT_LEVEL;
import static dte.employme.messages.MessageKey.GET;
import static dte.employme.messages.MessageKey.GLOBAL_JOB_BOARD_IS_FULL;
import static dte.employme.messages.MessageKey.GOAL;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_TITLE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_GOAL_ENCHANTMENT_SELECTION_TITLE;
import static dte.employme.messages.MessageKey.INVENTORY_ITEMS_JOB_NO_ITEMS_WARNING;
import static dte.employme.messages.MessageKey.INVENTORY_ITEMS_REWARD_OFFER_TITLE;
import static dte.employme.messages.MessageKey.INVENTORY_ITEMS_REWARD_PREVIEW_TITLE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_BOARD_OFFER_COMPLETED;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_BOARD_OFFER_NOT_COMPLETED;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_BOARD_TITLE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_MONEY_JOB_ICON_LORE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_MONEY_JOB_ICON_NAME;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_CREATION_TITLE;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_DELETION_DELETE_INSTRUCTION;
import static dte.employme.messages.MessageKey.INVENTORY_JOB_DELETION_TITLE;
import static dte.employme.messages.MessageKey.ITEMS;
import static dte.employme.messages.MessageKey.ITEMS_CONTAINER_DESCRIPTION;
import static dte.employme.messages.MessageKey.ITEMS_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.ITEM_GOAL_FORMAT_QUESTION;
import static dte.employme.messages.MessageKey.ITEM_GOAL_INVALID;
import static dte.employme.messages.MessageKey.JOB_ADDED_NOTIFIER_NOT_FOUND;
import static dte.employme.messages.MessageKey.JOB_ADDED_TO_BOARD;
import static dte.employme.messages.MessageKey.JOB_COMPLETED;
import static dte.employme.messages.MessageKey.JOB_ICON_ENCHANT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_GOAL_INSTRUCTIONS;
import static dte.employme.messages.MessageKey.JOB_ICON_ITEMS_PAYMENT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_MONEY_PAYMENT_DESCRIPTION;
import static dte.employme.messages.MessageKey.JOB_ICON_NAME;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_DELETED;
import static dte.employme.messages.MessageKey.MATERIAL_NOT_FOUND;
import static dte.employme.messages.MessageKey.MONEY_PAYMENT_AMOUNT_QUESTION;
import static dte.employme.messages.MessageKey.MONEY_REWARD_ERROR_NEGATIVE;
import static dte.employme.messages.MessageKey.MONEY_REWARD_NOT_A_NUMBER;
import static dte.employme.messages.MessageKey.MONEY_REWARD_NOT_ENOUGH;
import static dte.employme.messages.MessageKey.MUST_BE_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.MUST_NOT_BE_CONVERSING;
import static dte.employme.messages.MessageKey.NEW_JOB_POSTED;
import static dte.employme.messages.MessageKey.NEW_UPDATE_AVAILABLE;
import static dte.employme.messages.MessageKey.NONE;
import static dte.employme.messages.MessageKey.PLAYER_COMPLETED_YOUR_JOB;
import static dte.employme.messages.MessageKey.REWARD;
import static dte.employme.messages.MessageKey.REWARDS;
import static dte.employme.messages.MessageKey.REWARDS_CONTAINER_DESCRIPTION;
import static dte.employme.messages.MessageKey.SUBSCRIBED_TO_GOALS_NOTIFICATION;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL;
import static dte.employme.messages.MessageKey.THE_JOB_ADDED_NOTIFIERS_ARE;
import static dte.employme.messages.MessageKey.YOUR_NEW_JOB_ADDED_NOTIFIER_IS;
import static dte.employme.messages.MessageKey.YOUR_SUBSCRIPTIONS_ARE;
import static dte.employme.messages.MessageKey.YOU_OFFERED_TOO_MANY_JOBS;
import static org.bukkit.ChatColor.RED;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import dte.employme.EmployMe;
import dte.employme.messages.MessageKey;
import dte.employme.utils.java.EnumUtils;
import dte.employme.utils.java.MapBuilder;

public class ConfigFileFactory
{
	private final ExceptionHandler creationExceptionHandler, saveExceptionHandler;

	private ConfigFileFactory(Builder builder) 
	{
		this.creationExceptionHandler = builder.creationExceptionHandler;
		this.saveExceptionHandler = builder.saveExceptionHandler;
	}

	public ConfigFile getConfig() 
	{
		ConfigFile config = ConfigFile.byPath("config.yml");

		if(!create(config))
			return null;

		config.getConfig().addDefault("Language", "english");
		config.getConfig().options().copyDefaults(true);

		return save(config) ? config : null;
	}
	
	public ConfigFile getConfig(String path) 
	{
		ConfigFile config = ConfigFile.byPath(path);

		return create(config) ? config : null;
	}
	
	public ConfigFile getLanguageConfigFrom(ConfigFile config) 
	{
		//always create the english config
		ConfigFile englishConfig = getEnglishConfig();

		String language = config.getConfig().getString("Language");
		ConfigFile languageConfig = ConfigFile.byPath(String.format("languages/%s.yml", language));

		if(!languageConfig.exists()) 
		{
			EmployMe.getInstance().logToConsole(RED + String.format("The messages file for language '%s' is missing, defaulting to English!", language));
			return englishConfig;
		}

		return languageConfig;
	}

	private ConfigFile getEnglishConfig() 
	{
		Map<MessageKey, String[]> messages = new MapBuilder<MessageKey, String[]>()

				//Jobs
				.put(JOB_ADDED_TO_BOARD, new String[]{"&aYour offer was added to the &eJobs Board&a!"})
				.put(JOB_COMPLETED, new String[]{"&aYou successfully completed this Job!"})
				.put(ITEMS_JOB_COMPLETED, new String[]{"&aJob Completed. You can access your items via &b\"/employment myrewards\""})
				.put(PLAYER_COMPLETED_YOUR_JOB, new String[]{"&b%completer% &djust completed one of your Jobs!"})
				.put(GLOBAL_JOB_BOARD_IS_FULL, new String[]{"&cNot enough room for additional Jobs."})
				.put(YOU_OFFERED_TOO_MANY_JOBS, new String[]{"&cYou have offered too many Jobs! Please delete one to proceed."})

				//Job Added Notifiers
				.put(JOB_ADDED_NOTIFIER_NOT_FOUND, new String[]{"&cNo notifier named '%job added notifier%' was found."})
				.put(YOUR_NEW_JOB_ADDED_NOTIFIER_IS, new String[]{"&aYou will get notifications for &e%job added notifier%&a!"})
				.put(THE_JOB_ADDED_NOTIFIERS_ARE, new String[]{"&fThe current notifiers are: &a%job added notifiers%"})
				.put(NEW_JOB_POSTED, new String[]{"&fA new job was posted in the &aJobs Board&f!"})

				//Subscriptions
				.put(SUCCESSFULLY_SUBSCRIBED_TO_GOAL, new String[]{"&fYou just &asubscribed &fto &e%goal% &fJobs!"})
				.put(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, new String[]{"&fSuccessfully &4unsubscribed &ffrom &e%goal% &fJobs."})
				.put(SUBSCRIBED_TO_GOALS_NOTIFICATION, new String[]{"&fA player just posted a job that offers &b&l%rewards%&f!"})
				.put(MUST_BE_SUBSCRIBED_TO_GOAL, new String[]{"&cYou have to be subscribed to this Goal!"})
				.put(YOUR_SUBSCRIPTIONS_ARE, new String[]{"&fYou are subscribed to: &6%goal subscriptions%"})

				//General
				.put(NONE, new String[]{"None"})
				.put(GET, new String[]{"Get"})
				.put(GOAL, new String[]{"Goal"})
				.put(REWARD, new String[]{"Reward"})
				.put(ITEMS, new String[]{"Items"})
				.put(REWARDS, new String[]{"Rewards"})
				.put(MUST_NOT_BE_CONVERSING, new String[]{"&cYou have to finish your current conversation."})
				.put(MATERIAL_NOT_FOUND, new String[]{"&cThe specified Material doesn't exist!"})
				.put(NEW_UPDATE_AVAILABLE, new String[]{"&fPlease update &fto the lastest version! (&e%new version%&f)"})
				.put(CURRENCY_SYMBOL, new String[]{"$"})

				//Players Containers
				.put(ITEMS_CONTAINER_DESCRIPTION, new String[]{"&fWhen someone completes one of your jobs,", "&fThe items they got for you are stored here."})
				.put(REWARDS_CONTAINER_DESCRIPTION, new String[]{"&fThis is where Reward Items are stored", "&fafter you complete a job that pays them."})
				.put(CONTAINER_CLAIM_INSTRUCTION, new String[]{"Claim your %container subject%:"})
				.put(CONTAINER_HELP_ITEM_NAME, new String[]{"&aHelp"})

				//Job Icon
				.put(JOB_ICON_NAME, new String[]{"&a%employer%'s Offer"})
				.put(JOB_ICON_GOAL_INSTRUCTIONS, new String[]{"&b&lGoal: &fI need &b%goal%&f."})
				.put(JOB_ICON_ENCHANT_DESCRIPTION, new String[]{"&dEnchanted &fwith:"})
				.put(JOB_ICON_MONEY_PAYMENT_DESCRIPTION, new String[]{"&6&n&lPayment&6: &f%money payment%%currency symbol%"})
				.put(JOB_ICON_ITEMS_PAYMENT_DESCRIPTION, new String[]{"&6&n&lPayment&6: &fRight Click to preview items(%items amount%)"})

				//Job Board GUI
				.put(INVENTORY_JOB_BOARD_TITLE, new String[]{"Available Jobs"})
				.put(INVENTORY_JOB_BOARD_OFFER_COMPLETED, new String[]{"&a&lClick to Finish!"})
				.put(INVENTORY_JOB_BOARD_OFFER_NOT_COMPLETED, new String[]{"&cYou didn't complete this Job."})

				//Job Deletion GUI
				.put(INVENTORY_JOB_DELETION_TITLE, new String[]{"Select Jobs to Delete"})
				.put(INVENTORY_JOB_DELETION_DELETE_INSTRUCTION, new String[]{"&4&lClick to Delete!"})
				.put(JOB_SUCCESSFULLY_DELETED, new String[]{"&eYou successfully deleted this Job!"})

				//Job Creation GUI
				.put(INVENTORY_JOB_CREATION_TITLE, new String[]{"Create a new Job"})
				.put(INVENTORY_JOB_CREATION_MONEY_JOB_ICON_NAME, new String[]{"&6Money Job"})
				.put(INVENTORY_JOB_CREATION_MONEY_JOB_ICON_LORE, new String[]{"&fClick to offer a Job for which", "&fyou will pay a certain amount of money."})
				.put(INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_NAME, new String[]{"&bItems Job"})
				.put(INVENTORY_JOB_CREATION_ITEMS_JOB_ICON_LORE, new String[]{"&fClick to offer a Job for which", "&fyou will pay with resources."})
				.put(MONEY_PAYMENT_AMOUNT_QUESTION, new String[]{"&fHow much will you &e&lPay&f? &f(Current Balance: &e&l%player money%&6%currency symbol%&f)"})
				.put(MONEY_REWARD_ERROR_NEGATIVE, new String[]{"&cCan't create a Money Reward that pays nothing or less!"})
				.put(MONEY_REWARD_NOT_ENOUGH, new String[]{"&cYou can't offer an amount of money that you don't have!"})
				.put(MONEY_REWARD_NOT_A_NUMBER, new String[]{"&cPayment has to be a Positive Integer!"})

				//Items Reward Preview GUI
				.put(INVENTORY_ITEMS_REWARD_PREVIEW_TITLE, new String[]{"Reward Preview (Esc to Return)"})

				//Items Reward Offer GUI
				.put(INVENTORY_ITEMS_REWARD_OFFER_TITLE, new String[]{"What would you like to offer?"})
				.put(INVENTORY_ITEMS_JOB_NO_ITEMS_WARNING, new String[]{"&cJob creation cancelled because you didn't offer any item."})

				//Goal Enchantment Selection GUI
				.put(INVENTORY_GOAL_ENCHANTMENT_SELECTION_TITLE, new String[]{"Choose an Enchantment:"})
				.put(INVENTORY_GOAL_ENCHANTMENT_SELECTION_ITEM_LORE, new String[]{"&fClick to add this Enchantment to the Goal."})
				.put(ENTER_ENCHANTMENT_LEVEL, new String[]{"&fWhat level for &a%enchantment%&f?"})
				.put(ENCHANTMENT_LEVEL_NOT_A_NUMBER, new String[]{"&cThe Level must be an Integer!"})
				.put(ENCHANTMENT_LEVEL_OUT_OF_BOUNDS, new String[]{"&cThe provided level is out of bounds! (&4%enchantment min level%&c-&4%enchantment max level%&c)"})

				//Goal Customization GUI
				.put(INVENTORY_GOAL_CUSTOMIZATION_TITLE, new String[]{"What should the Goal Item be?"})
				.put(INVENTORY_GOAL_CUSTOMIZATION_CURRENT_ITEM_NAME, new String[]{"&aCurrent Goal"})
				.put(INVENTORY_GOAL_CUSTOMIZATION_NO_CURRENT_ITEM_NAME, new String[]{"&c&lCurrent Goal: None"})
				.put(INVENTORY_GOAL_CUSTOMIZATION_FINISH_ITEM_NAME, new String[]{"&a&lFinish"})
				.put(INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_NAME, new String[]{"&aType"})
				.put(INVENTORY_GOAL_CUSTOMIZATION_TYPE_ITEM_LORE, new String[]{"&fClick to set the type of the goal."})
				.put(INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_NAME, new String[]{"&6Amount: &f&l%goal amount%"})
				.put(INVENTORY_GOAL_CUSTOMIZATION_AMOUNT_ITEM_LORE, new String[]{"&fLeft Click to &aIncrease&f.", "&fRight Click to &cDecrease&f."})
				.put(INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_NAME, new String[]{"&dEnchantments"})
				.put(INVENTORY_GOAL_CUSTOMIZATION_ENCHANTMENTS_ITEM_LORE, new String[]{"&fClick to add an enchantment that", "&fthe goal must have on it."})
				.put(ITEM_GOAL_FORMAT_QUESTION, new String[]{"&fWhich &aitem &fdo you need? Reply with the name of it!"})
				.put(ITEM_GOAL_INVALID, new String[]{"&cThe specified goal is either incorrectly formatted or unachievable!"})
				.build();

		return getLanguageConfig("english", messages);
	}

	private ConfigFile getLanguageConfig(String language, Map<MessageKey, String[]> messages) 
	{
		ConfigFile config = ConfigFile.byPath("languages" + File.separator + language + ".yml");

		if(!create(config))
			return null;

		messages.forEach((messageKey, keyMessages) -> 
		{
			//genius
			Object message = keyMessages.length == 1 ? keyMessages[0] : Arrays.asList(keyMessages);

			config.getConfig().addDefault("Messages." + EnumUtils.fixEnumName(messageKey), message);
		});

		config.getConfig().options().copyDefaults(true);

		return save(config) ? config : null;
	}

	private boolean create(ConfigFile config)
	{
		return ConfigFile.createIfAbsent(config, exception -> this.creationExceptionHandler.handle(exception, config));
	}

	private boolean save(ConfigFile config) 
	{
		return config.save(exception -> this.saveExceptionHandler.handle(exception, config));
	}



	public static class Builder
	{
		ExceptionHandler creationExceptionHandler, saveExceptionHandler;

		public Builder handleCreationException(ExceptionHandler handler) 
		{
			this.creationExceptionHandler = handler;
			return this;
		}

		public Builder handleSaveException(ExceptionHandler handler) 
		{
			this.saveExceptionHandler = handler;
			return this;
		}

		public ConfigFileFactory build() 
		{
			return new ConfigFileFactory(this);
		}
	}
}