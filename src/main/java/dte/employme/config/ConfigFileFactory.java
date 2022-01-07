package dte.employme.config;

import static dte.employme.messages.MessageKey.GLOBAL_JOB_BOARD_IS_FULL;
import static dte.employme.messages.MessageKey.ITEMS_JOB_COMPLETED;
import static dte.employme.messages.MessageKey.ITEMS_JOB_NO_ITEMS_WARNING;
import static dte.employme.messages.MessageKey.ITEM_GOAL_FORMAT_QUESTION;
import static dte.employme.messages.MessageKey.ITEM_GOAL_INVALID;
import static dte.employme.messages.MessageKey.JOB_ADDED_NOTIFIER_NOT_FOUND;
import static dte.employme.messages.MessageKey.JOB_ADDED_TO_BOARD;
import static dte.employme.messages.MessageKey.JOB_COMPLETED;
import static dte.employme.messages.MessageKey.JOB_SUCCESSFULLY_DELETED;
import static dte.employme.messages.MessageKey.MATERIAL_NOT_FOUND;
import static dte.employme.messages.MessageKey.MONEY_PAYMENT_AMOUNT_QUESTION;
import static dte.employme.messages.MessageKey.MONEY_REWARD_ERROR_NEGATIVE;
import static dte.employme.messages.MessageKey.MONEY_REWARD_NOT_A_NUMBER;
import static dte.employme.messages.MessageKey.MONEY_REWARD_NOT_ENOUGH;
import static dte.employme.messages.MessageKey.MUST_BE_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.MUST_HAVE_JOBS;
import static dte.employme.messages.MessageKey.MUST_NOT_BE_CONVERSING;
import static dte.employme.messages.MessageKey.NEW_JOB_POSTED;
import static dte.employme.messages.MessageKey.NEW_UPDATE_AVAILABLE;
import static dte.employme.messages.MessageKey.NONE;
import static dte.employme.messages.MessageKey.PLAYER_COMPLETED_YOUR_JOB;
import static dte.employme.messages.MessageKey.SUBSCRIBED_TO_GOALS_NOTIFICATION;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_SUBSCRIBED_TO_GOAL;
import static dte.employme.messages.MessageKey.SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL;
import static dte.employme.messages.MessageKey.THE_JOB_ADDED_NOTIFIERS_ARE;
import static dte.employme.messages.MessageKey.YOUR_NEW_JOB_ADDED_NOTIFIER_IS;
import static dte.employme.messages.MessageKey.YOUR_SUBSCRIPTIONS_ARE;
import static org.bukkit.ChatColor.RED;

import java.io.File;
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
	
	public ConfigFile getJobsConfig() 
	{
		ConfigFile config = ConfigFile.byPath("jobs.yml");

		return create(config) ? config : null;
	}

	public ConfigFile getSubscriptionsConfig() 
	{
		ConfigFile config = ConfigFile.byPath("subscriptions");
		
		return create(config) ? config : null;
	}

	public ConfigFile getJobAddNotifiersConfig() 
	{
		ConfigFile config = ConfigFile.byPath("job add notifiers");
		
		return create(config) ? config : null;
	}

	public ConfigFile getItemsContainersConfig()
	{
		ConfigFile config = ConfigFile.byPath("containers/items containers");
		
		return create(config) ? config : null;
	}

	public ConfigFile getRewardsContainersConfig() 
	{
		ConfigFile config = ConfigFile.byPath("containers/rewards containers");
		
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
		Map<MessageKey, String> messages = new MapBuilder<MessageKey, String>()

				//Jobs
				.put(JOB_ADDED_TO_BOARD, "&aYour offer was added to the &eJobs Board&a!")
				.put(JOB_COMPLETED, "&aYou successfully completed this Job!")
				.put(ITEMS_JOB_COMPLETED, "&aJob Completed. You can access your items via &b\"/employment myrewards\"")
				.put(JOB_SUCCESSFULLY_DELETED, "&eYou successfully deleted this Job!")
				.put(PLAYER_COMPLETED_YOUR_JOB, "&b%completer% &djust completed one of your Jobs!")
				.put(ITEMS_JOB_NO_ITEMS_WARNING, "&cJob creation cancelled because you didn't offer any item.")
				.put(NEW_JOB_POSTED, "&fA new job was posted in the &aJobs Board&f!")
				.put(GLOBAL_JOB_BOARD_IS_FULL, "&cNot enough room for additional Jobs.")

				//Job Added Notifiers
				.put(JOB_ADDED_NOTIFIER_NOT_FOUND, "&cNo notifier named '%job added notifier%' was found.")
				.put(YOUR_NEW_JOB_ADDED_NOTIFIER_IS, "&aYou will get notifications for &e%job added notifier%&a!")
				.put(THE_JOB_ADDED_NOTIFIERS_ARE, "&fThe current notifiers are: &a%job added notifiers%")

				//Subscriptions
				.put(SUCCESSFULLY_SUBSCRIBED_TO_GOAL, "&fYou just &asubscribed &fto &e%goal% &fJobs!")
				.put(SUCCESSFULLY_UNSUBSCRIBED_FROM_GOAL, "&fSuccessfully &4unsubscribed &ffrom &e%goal% &fJobs.")
				.put(SUBSCRIBED_TO_GOALS_NOTIFICATION, "&fA player just posted a job that offers &b&l%rewards%&f!")
				.put(MUST_BE_SUBSCRIBED_TO_GOAL, "&cYou must be subscribed to this Goal!")
				.put(YOUR_SUBSCRIPTIONS_ARE, "&fYou are subscribed to: &6%goal subscriptions%")

				//Rewards
				.put(MONEY_PAYMENT_AMOUNT_QUESTION, "&fHow much will you &e&lPay&f? &f(Current Balance: &e&l%player money%&6$&f)")

				//Goals
				.put(ITEM_GOAL_FORMAT_QUESTION, "&fWhich &aitem &fdo you need? Reply with the name of it!")
				.put(ITEM_GOAL_INVALID, "&cThe specified goal is either incorrectly formatted or unachievable!")

				//Rewards
				.put(MONEY_REWARD_ERROR_NEGATIVE, "&cCan't create a Money Reward that pays nothing or less!")
				.put(MONEY_REWARD_NOT_ENOUGH, "&cYou can't offer an amount of money that you don't have!")
				.put(MONEY_REWARD_NOT_A_NUMBER, "&cPayment has to be a Positive Integer!")
				
				//Enchantments
				.put(MessageKey.ENTER_ENCHANTMENT_LEVEL, "&fWhat level for &a%enchantment%&f?")
				.put(MessageKey.ENCHANTMENT_LEVEL_NOT_A_NUMBER, "&cThe Level must be an Integer!")
				.put(MessageKey.ENCHANTMENT_LEVEL_OUT_OF_BOUNDS, "&cThe provided level is out of bounds! (&4%enchantment min level%&c-&4%enchantment max level%&c)")

				//General
				.put(MUST_NOT_BE_CONVERSING, "&cYou have to finish your current conversation.")
				.put(MUST_HAVE_JOBS, "&cYou must have offered at least one Job!")
				.put(MATERIAL_NOT_FOUND, "&cThe specified Material doesn't exist!")
				.put(NONE, "None")
				.put(NEW_UPDATE_AVAILABLE, "&fPlease update &fto the lastest version! (&e%new version%&f)")
				.build();

		return getLanguageConfig("english", messages);
	}

	private ConfigFile getLanguageConfig(String language, Map<MessageKey, String> messages) 
	{
		ConfigFile config = ConfigFile.byPath("languages" + File.separator + language + ".yml");

		if(!create(config))
			return null;

		messages.forEach((messageKey, message) -> config.getConfig().addDefault("Messages." + EnumUtils.fixEnumName(messageKey), message));
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