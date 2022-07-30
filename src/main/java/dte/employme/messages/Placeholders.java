package dte.employme.messages;

public class Placeholders
{
	//Container of static fields
	private Placeholders(){}
	
	public static final String 
	PLAYER_NAME = create("player"),
	GOAL_SUBSCRIPTIONS = create("goal subscriptions"),
	COMPLETER = create("completer"),
	PLAYER_MONEY = create("player money"),
	GOAL = create("goal"),
	REWARDS = create("rewards"),
	JOB_ADDED_NOTIFIER = create("job added notifier"),
	JOB_ADDED_NOTIFIERS = create("job added notifiers"),
	ENCHANTMENT = create("enchantment"),
	ENCHANTMENT_MIN_LEVEL = create("enchantment min level"),
	NEW_VERSION = create("new version"),
	GOAL_AMOUNT = create("goal amount"),
	EMPLOYER = create("employer"),
	MONEY_PAYMENT = create("money payment"),
	ITEMS_AMOUNT = create("items amount"),
	CONTAINER_SUBJECT = create("container subject"),
	CURRENCY_SYMBOL = create("currency symbol"),
	RELOAD_TIME = create("reload time"),
	ITEM = create("item");

	private static String create(String value) 
	{
		return '%' + value + '%';
	}
}