package dte.employme.job.rewards.service;

import static dte.employme.messages.MessageKey.JOB_CANCELLED_REWARD_REFUNDED;
import static dte.employme.messages.MessageKey.PREFIX;

import org.bukkit.OfflinePlayer;

import dte.employme.job.rewards.Reward;
import dte.employme.messages.service.MessageService;

public class SimpleRewardService implements RewardService
{
	private final MessageService messageService;
	
	public SimpleRewardService(MessageService messageService) 
	{
		this.messageService = messageService;
	}
	
	@Override
	public void refund(OfflinePlayer employer, Reward reward) 
	{
		reward.giveTo(employer);
		this.messageService.getMessage(JOB_CANCELLED_REWARD_REFUNDED)
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.sendIfOnline(employer);
	}
}
