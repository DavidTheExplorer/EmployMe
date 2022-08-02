package dte.employme.services.rewards;

import static java.util.stream.Collectors.joining;

import dte.employme.messages.MessageKey;
import dte.employme.rewards.ItemsReward;
import dte.employme.rewards.MoneyReward;
import dte.employme.rewards.Reward;
import dte.employme.services.message.MessageService;
import dte.employme.utils.ItemStackUtils;

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
			String currencySymbol = this.messageService.getMessage(MessageKey.CURRENCY_SYMBOL).first();
			
			return String.format("%.2f%s", ((MoneyReward) reward).getPayment(), currencySymbol);
		}
		else if(reward instanceof ItemsReward) 
		{
			return ((ItemsReward) reward).getItems().stream()
					.map(ItemStackUtils::describe)
					.collect(joining(", "));
		}
		
		throw new IllegalArgumentException("Cannot describe the provided reward!");
	}
}
