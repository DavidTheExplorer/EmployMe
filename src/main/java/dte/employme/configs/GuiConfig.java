package dte.employme.configs;

import org.bukkit.configuration.ConfigurationSection;

import com.github.stefvanschie.inventoryframework.pane.util.Slot;

import dte.employme.EmployMe;
import dte.employme.messages.MessageBuilder;
import dte.employme.utils.ChatColorUtils;
import dte.employme.utils.inventoryframework.GuiItemBuilder;
import dte.spigotconfiguration.SpigotConfig;

public class GuiConfig extends SpigotConfig
{
	public GuiConfig(String name) 
	{
		super(new Builder(EmployMe.getInstance())
				.fromInternalResource(String.format("GUIs/%s", name)));
	}
	
	public String getTitle() 
	{
		return getString("title");
	}
	
	public MessageBuilder getText(String identifier)
	{
		Object message = get("texts." + identifier);
		
		return MessageBuilder.from(message).map(ChatColorUtils::colorize);
	}
	
	public GuiItemBuilder parseGuiItem(String identifier)
	{
		ConfigurationSection section = getSection("items." + identifier);
		
		//parse the item
		GuiItemBuilder builder = new GuiItemBuilder()
				.forItem(parseItem(section.getCurrentPath()));
		
		//parse the slot
		if(section.contains("slot"))
			builder.at(Slot.fromIndex(section.getInt("slot", 0) -1));

		return builder;
	}
}