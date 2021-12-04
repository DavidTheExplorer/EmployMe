package dte.employme.messages;

import java.util.Map.Entry;

import dte.employme.job.addnotifiers.JobAddedNotifier;
import dte.employme.utils.java.MapBuilder;

public class Placeholders extends MapBuilder<String, String>
{
	public static final String 
	PLAYER_NAME = "player",
	GOAL_SUBSCRIPTIONS = "goal subscriptions",
	COMPLETER = "completer",
	PLAYER_MONEY = "player money",
	GOAL = "goal",
	REWARDS = "rewards",
	JOB_ADDED_NOTIFIER = "job added notifier";
	
	public static final Placeholders NONE = new Placeholders();
	
	@Override
	public Placeholders put(String key, String value) 
	{
		super.put("%" + key + "%", value);
		return this;
	}
	
	public Placeholders put(String key, JobAddedNotifier notifier) 
	{
		return put(key, notifier.getName());
	}
	
	public Placeholders put(String key, Object value) 
	{
		return put(key, value.toString());
	}
	
	public String apply(String text) 
	{
		for(Entry<String, String> entry : build().entrySet())
			text = text.replace(entry.getKey(), entry.getValue());
		
		return text;
	}
}
