package dte.employme.conversations;

import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_INVALID_PARTIAL_GOAL_AMOUNT_ERROR;
import static dte.employme.messages.MessageKey.GUI_JOB_BOARD_PARTIAL_GOAL_AMOUNT_TO_USE_QUESTION;
import static dte.employme.messages.Placeholders.GOAL_AMOUNT;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import dte.employme.job.Job;
import dte.employme.services.job.JobService;
import dte.employme.services.message.MessageService;

public class JobPartialCompletionAmountPrompt extends NumericPrompt
{
	private final MessageService messageService;
	private final JobService jobService;
	private final Job job;
	private final Player player;
	
	public JobPartialCompletionAmountPrompt(MessageService messageService, JobService jobService, Job job, Player player) 
	{
		this.messageService = messageService;
		this.jobService = jobService;
		this.job = job;
		this.player = player;
	}
	
	@Override
	public String getPromptText(ConversationContext context)
	{
		return this.messageService.getMessage(GUI_JOB_BOARD_PARTIAL_GOAL_AMOUNT_TO_USE_QUESTION)
				.inject(GOAL_AMOUNT, getGoalAmountInInventory())
				.first();
	}
	
	@Override
	protected boolean isNumberValid(ConversationContext context, Number input) 
	{
		int amount = input.intValue();
		
		return amount > 0 && amount <= Math.min(getGoalAmountInInventory(), this.job.getGoal().getAmount());
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, Number invalidInput) 
	{
		return this.messageService.getMessage(GUI_JOB_BOARD_INVALID_PARTIAL_GOAL_AMOUNT_ERROR).first();
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, Number input) 
	{
		context.setSessionData("Amount To Use", input);
		
		return Prompt.END_OF_CONVERSATION;
	}
	
	private int getGoalAmountInInventory() 
	{
		return this.jobService.getGoalAmountInInventory(this.job, this.player.getInventory());
	}
}
