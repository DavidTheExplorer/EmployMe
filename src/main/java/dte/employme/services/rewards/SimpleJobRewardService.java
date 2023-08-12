package dte.employme.services.rewards;

import static dte.employme.messages.MessageKey.CURRENCY_SYMBOL;

import dte.employme.rewards.MoneyReward;
import dte.employme.rewards.Reward;
import dte.employme.services.message.MessageService;

public class SimpleJobRewardService implements JobRewardService
{
	private final MessageService messageService;
	
	public SimpleJobRewardService(MessageService messageService) 
	{
		this.messageService = messageService;
	}
	
	@Override
	public String describe(Reward reward) 
	{
		if(reward instanceof MoneyReward) 
		{
			return String.format("%s%s", 
					MoneyReward.formatPayment(((MoneyReward) reward)), 
					this.messageService.loadMessage(CURRENCY_SYMBOL).first());
		}
		
		throw new IllegalArgumentException("Cannot describe the provided reward!");
	}
}
