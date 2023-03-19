package dte.employme.services.rewards;

import static dte.employme.messages.MessageKey.CURRENCY_SYMBOL;
import static java.util.stream.Collectors.joining;

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
			return String.format("%s%s", 
					MoneyReward.formatPayment(((MoneyReward) reward)), 
					this.messageService.loadMessage(CURRENCY_SYMBOL).first());
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
