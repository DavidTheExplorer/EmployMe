package dte.employme.messages;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import dte.employme.EmployMe;
import dte.employme.messages.service.MessageService;

/**
 * This class, along with {@link MessageService}, allows implementing multi-line messages in an encapsulated and relatively clean way.
 * 
 * @see MessageService
 */
public class MessageBuilder
{
	private final LinkedList<String> lines;
	
	public MessageBuilder(String... lines) 
	{
		this.lines = new LinkedList<>(Arrays.asList(lines));
	}
	
	public MessageBuilder inject(String placeholder, String value) 
	{
		this.lines.replaceAll(line -> line.replace(placeholder, value));
		return this;
	}
	
	public MessageBuilder inject(Map<String, String> placeholders) 
	{
		placeholders.forEach(this::inject);
		return this;
	}
	
	public MessageBuilder transform(UnaryOperator<String> transformer)
	{
		this.lines.replaceAll(transformer);
		return this;
	}
	
	public MessageBuilder withGeneralPrefix()
	{
		return transform(line -> EmployMe.CHAT_PREFIX + " " + line);
	}
	
	
	/*
	 * Termination methods that either return the messages or do something with them.
	 */
	public String first() 
	{
		return this.lines.peek();
	}

	public List<String> toList() 
	{
		return this.lines;
	}
	
	public String[] toArray() 
	{
		return this.lines.toArray(new String[0]);
	}
	
	public Stream<String> stream()
	{
		return this.lines.stream();
	}
	
	public void sendTo(CommandSender sender) 
	{
		this.lines.forEach(sender::sendMessage);
	}
	
	public void sendIfOnline(OfflinePlayer offlinePlayer)
	{
		if(offlinePlayer.isOnline())
			sendTo(offlinePlayer.getPlayer());
	}

	@Override
	public String toString()
	{
		return this.lines.size() == 1 ? first() : this.lines.toString();
	}
}