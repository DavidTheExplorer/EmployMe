package dte.employme.conversations;

import static dte.employme.messages.MessageKey.CONVERSATION_ESCAPE_TITLE;
import static dte.employme.messages.MessageKey.CONVERSATION_ESCAPE_WORD;
import static dte.employme.messages.MessageKey.ITEM_GOAL_BLOCKED_IN_YOUR_WORLD;
import static dte.employme.messages.MessageKey.ITEM_GOAL_INVALID;
import static dte.employme.utils.java.Predicates.negate;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;
import dte.employme.utils.MaterialUtils;

public class JobGoalPrompt extends ValidatingPrompt
{
	private final JobService jobService;
	private final MessageService messageService;
	private final String question;
	
	private boolean blockedItemSpecified = false;
	
	public JobGoalPrompt(JobService jobService, MessageService messageService, String question) 
	{
		this.jobService = jobService;
		this.messageService = messageService;
		this.question = question;
	}

	@Override
	public String getPromptText(ConversationContext context)
	{
		//send the escape hint title
		this.messageService.loadMessage(CONVERSATION_ESCAPE_TITLE)
		.inject("escape word", this.messageService.loadMessage(CONVERSATION_ESCAPE_WORD).first())
		.sendTitleTo((Player) context.getForWhom());
		
		return this.question;
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input)
	{
		World world = ((Player) context.getForWhom()).getWorld();
		Material material = Material.matchMaterial(input);
		
		this.blockedItemSpecified = this.jobService.isBlacklistedAt(world, material);
		
		return Optional.ofNullable(material)
				.filter(MaterialUtils::isObtainable)
				.filter(negate(self -> this.blockedItemSpecified))
				.isPresent();
	}
	
	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) 
	{
		context.setSessionData("material", Material.matchMaterial(input));
		
		return Prompt.END_OF_CONVERSATION;
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, String invalidInput) 
	{
		if(this.blockedItemSpecified) 
		{
			this.blockedItemSpecified = false;
			return this.messageService.loadMessage(ITEM_GOAL_BLOCKED_IN_YOUR_WORLD).first();
		}
		
		return this.messageService.loadMessage(ITEM_GOAL_INVALID).first();
	}
}