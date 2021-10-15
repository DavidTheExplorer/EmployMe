package dte.employme.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.job.rewards.MoneyReward;
import dte.employme.messages.Message;
import net.milkbowl.vault.economy.Economy;

public class JobPaymentPrompt extends NumericPrompt
{
	private final Economy economy;
	private final JobBoard jobBoard;
	
	public JobPaymentPrompt(JobBoard jobBoard, Economy economy) 
	{
		this.economy = economy;
		this.jobBoard = jobBoard;
	}
	
	@Override
	public String getPromptText(ConversationContext context) 
	{
		double employerMoney = this.economy.getBalance((Player) context.getForWhom());
		
		return Message.MONEY_PAYMENT_AMOUNT_QUESTION.inject(employerMoney);
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
			return Message.MONEY_REWARD_ERROR_NEGATIVE.toString();
		
		else if(!this.economy.has((Player) context.getForWhom(), payment))
			return Message.MONEY_REWARD_NOT_ENOUGH.toString();
		
		throw new IllegalStateException("Can't create a Money Reward from the provided input.");
	}
	
	@Override
	protected String getInputNotNumericText(ConversationContext context, String invalidInput) 
	{
		return Message.MONEY_REWARD_NOT_A_NUMBER.toString();
	}
}
