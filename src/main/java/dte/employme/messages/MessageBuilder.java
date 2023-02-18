package dte.employme.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dte.employme.services.message.MessageService;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * This class along with {@link MessageService}, allows implementing multi-line messages in an encapsulated and clean way.
 */
public class MessageBuilder
{
	private final List<String> lines;
	
	public MessageBuilder(String message) 
	{
		this(Arrays.asList(message));
	}
	
	public MessageBuilder(Collection<String> lines) 
	{
		this.lines = new ArrayList<>(lines);
	}
	
	@SuppressWarnings("unchecked")
	public static MessageBuilder from(Object message) 
	{
		if(message instanceof String)
			return new MessageBuilder((String) message);
		
		if(message instanceof Collection)
			return new MessageBuilder(((Collection<String>) message));
		
		throw new IllegalArgumentException(String.format("The specified object(%s) doesn't represent a message!", message));
	}
	
	public MessageBuilder map(UnaryOperator<String> transformer)
	{
		this.lines.replaceAll(transformer);
		return this;
	}
	
	public MessageBuilder prefixed(String prefix) 
	{
		return map(line -> prefix + line);
	}
	
	public MessageBuilder inject(String placeholder, Object value) 
	{
		return map(line -> line.replace('%' + placeholder + '%', value.toString()));
	}
	
	
	
	/*
	 * Termination methods that either return the messages or do something with them.
	 */
	public String first() 
	{
		return this.lines.get(0);
	}
	
	public List<String> toList() 
	{
		return this.lines;
	}
	
	public String[] toArray() 
	{
		return this.lines.toArray(new String[0]);
	}
	
	public TextComponent toTextComponent() 
	{
		if(this.lines.size() > 1)
			throw new IllegalStateException("Cannot convert a multi-line message into a TextComponent!");
		
		return new TextComponent(this.lines.get(0));
	}
	
	public Stream<String> stream()
	{
		return this.lines.stream();
	}
	
	public void sendTo(CommandSender sender) 
	{
		this.lines.forEach(sender::sendMessage);
	}
	
	public void sendTitleTo(Player player) 
	{
		sendTitleTo(player, -1, -1, -1);
	}
	
	public void sendTitleTo(Player player, int fadeIn, int stay, int fadeOut) 
	{
		if(this.lines.size() > 2)
			throw new IllegalStateException("Cannot send a title whose size is more than 2 lines!");
		
		String title = this.lines.get(0);
		String subtitle = this.lines.get(1);
		
		player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
	}

	@Override
	public String toString()
	{
		return this.lines.size() == 1 ? first() : this.lines.toString();
	}
}