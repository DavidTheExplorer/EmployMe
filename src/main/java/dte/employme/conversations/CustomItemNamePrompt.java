package dte.employme.conversations;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import dte.employme.items.custom.CustomItemParseException;
import dte.employme.items.custom.CustomItemProvider;

public class CustomItemNamePrompt extends ValidatingPrompt
{
	private final CustomItemProvider customItemProvider;

	public CustomItemNamePrompt(CustomItemProvider customItemProvider) 
	{
		this.customItemProvider = customItemProvider;
	}

	@Override
	public String getPromptText(ConversationContext context) 
	{
		return WHITE + "Please reply with the format: " + YELLOW + this.customItemProvider.getRequestFormat();
	}

	@Override
	protected String getFailedValidationText(ConversationContext context, String invalidInput) 
	{
		return RED + "Invalid format! (Use " + this.customItemProvider.getRequestFormat() + ")";
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) 
	{
		try 
		{
			this.customItemProvider.parse(input);
			return true;
		}
		catch(CustomItemParseException exception)
		{
			return false;
		}
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) 
	{
		context.setSessionData("custom item", this.customItemProvider.parse(input));

		return Prompt.END_OF_CONVERSATION;
	}
}
