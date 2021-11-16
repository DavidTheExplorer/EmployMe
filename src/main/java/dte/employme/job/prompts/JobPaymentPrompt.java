package dte.employme.job.prompts;

import static dte.employme.messages.MessageKey.MONEY_PAYMENT_AMOUNT_QUESTION;
import static dte.employme.messages.MessageKey.MONEY_REWARD_ERROR_NEGATIVE;
import static dte.employme.messages.MessageKey.MONEY_REWARD_NOT_A_NUMBER;
import static dte.employme.messages.MessageKey.MONEY_REWARD_NOT_ENOUGH;
import static dte.employme.messages.Placeholders.PLAYER_MONEY;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.messages.Placeholders;
import dte.employme.messages.service.MessageService;
import net.milkbowl.vault.economy.Economy;

public class JobPaymentPrompt extends NumericPrompt
{
	private final Economy economy;
	private final JobBoard jobBoard;
	private final MessageService messageService;
	
	public JobPaymentPrompt(JobBoard jobBoard, Economy economy, MessageService messageService) 
	{
		this.economy = economy;
		this.jobBoard = jobBoard;
		this.messageService = messageService;
	}
	
	@Override
	public String getPromptText(ConversationContext context) 
	{
		double employerMoney = this.economy.getBalance((Player) context.getForWhom());
		
		return this.messageService.getMessage(MONEY_PAYMENT_AMOUNT_QUESTION, new Placeholders().put(PLAYER_MONEY, employerMoney));
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, Number input) 
	{
		context.setSessionData("reward", new MoneyReward(input.doubleValue()));
		
		return new JobPostedMessagePrompt(this.jobBoard);
	}
	
	@Override
	protected boolean isNumberValid(ConversationContext context, Number input) 
	{
		double payment = input.doubleValue();
		
		return payment > 0 && this.economy.has((Player) context.getForWhom(), payment);
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, Number invalidInput) 
	{
		double payment = invalidInput.doubleValue();
		
		if(payment <= 0)
			return this.messageService.getMessage(MONEY_REWARD_ERROR_NEGATIVE);
		
		else if(!this.economy.has((Player) context.getForWhom(), payment))
			return this.messageService.getMessage(MONEY_REWARD_NOT_ENOUGH);
		
		throw new IllegalStateException("Can't create a Money Reward from the provided input.");
	}
	
	@Override
	protected String getInputNotNumericText(ConversationContext context, String invalidInput) 
	{
		return this.messageService.getMessage(MONEY_REWARD_NOT_A_NUMBER);
	}
}
