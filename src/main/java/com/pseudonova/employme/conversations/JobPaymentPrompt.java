package com.pseudonova.employme.conversations;

import static com.pseudonova.employme.utils.ChatColorUtils.colorize;
import static org.bukkit.ChatColor.RED;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import com.pseudonova.employme.board.JobBoard;
import com.pseudonova.employme.board.service.JobBoardService;
import com.pseudonova.employme.reward.MoneyReward;

import net.milkbowl.vault.economy.Economy;

public class JobPaymentPrompt extends NumericPrompt
{
	private final Economy economy;
	private final JobBoardService jobBoardService;
	private final JobBoard jobBoard;
	
	public JobPaymentPrompt(JobBoardService jobBoardService, JobBoard jobBoard, Economy economy) 
	{
		this.economy = economy;
		this.jobBoardService = jobBoardService;
		this.jobBoard = jobBoard;
	}
	
	@Override
	public String getPromptText(ConversationContext context) 
	{
		double employerMoney = this.economy.getBalance((Player) context.getForWhom());
		
		return colorize(String.format("&fHow much will you &e&lPay&f? &f(Current Balance: &e&l%.2f&6$&f)", employerMoney));
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, Number input) 
	{
		MoneyReward moneyReward = MoneyReward.of(input.doubleValue());
		
		context.setSessionData("reward", moneyReward);
		
		return new JobPostedMessagePrompt(this.jobBoardService, this.jobBoard);
	}
	
	@Override
	protected boolean isNumberValid(ConversationContext context, Number input) 
	{
		return this.economy.has((Player) context.getForWhom(), input.doubleValue());
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, Number invalidInput) 
	{
		return RED + "You can't afford to pay such an amount!";
	}
	
	@Override
	protected String getInputNotNumericText(ConversationContext context, String invalidInput) 
	{
		return RED + "Payment has to be a Positive Integer!";
	}
}
