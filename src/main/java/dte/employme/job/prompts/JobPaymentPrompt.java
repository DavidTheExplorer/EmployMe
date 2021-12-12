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

import dte.employme.job.rewards.MoneyReward;
import dte.employme.messages.Placeholders;
import dte.employme.messages.service.MessageService;
import dte.employme.visitors.reward.RewardTaker;
import net.milkbowl.vault.economy.Economy;

public class JobPaymentPrompt extends NumericPrompt
{
	private final Economy economy;
	private final MessageService messageService;
	
	public JobPaymentPrompt(Economy economy, MessageService messageService) 
	{
		this.economy = economy;
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
		MoneyReward moneyReward = new MoneyReward(this.economy, input.doubleValue());
		moneyReward.accept(new RewardTaker((Player) context.getForWhom(), this.economy));
		
		context.setSessionData("reward", moneyReward);
		
		return Prompt.END_OF_CONVERSATION;
	}
	
	@Override
	protected boolean isNumberValid(ConversationContext context, Number input) 
	{
		Player player = (Player) context.getForWhom();
		double payment = input.doubleValue();
		
		return payment > 0 && this.economy.has(player, payment);
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, Number invalidInput) 
	{
		double payment = invalidInput.doubleValue();
		
		if(payment <= 0)
			return this.messageService.getMessage(MONEY_REWARD_ERROR_NEGATIVE);
		
		else if(!this.economy.has((Player) context.getForWhom(), payment))
			return this.messageService.getMessage(MONEY_REWARD_NOT_ENOUGH);
		
		throw new IllegalStateException("Couldn't parse the provided input to an payment amount!");
	}
	
	@Override
	protected String getInputNotNumericText(ConversationContext context, String invalidInput) 
	{
		return this.messageService.getMessage(MONEY_REWARD_NOT_A_NUMBER);
	}
}
