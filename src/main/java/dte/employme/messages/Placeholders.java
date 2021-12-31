package dte.employme.messages;

import java.util.Map;

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
	ENCHANTMENT_MAX_LEVEL = create("enchantment max level"),
	NEW_VERSION = create("new version");

	public static String apply(String text, Map<String, String> placeholders) 
	{
		for(Map.Entry<String, String> entry : placeholders.entrySet())
			text = text.replace(entry.getKey(), entry.getValue());
		
		return text;
	}

	private static String create(String value) 
	{
		return '%' + value + '%';
	}
}