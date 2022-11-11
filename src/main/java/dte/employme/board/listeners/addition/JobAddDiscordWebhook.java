package dte.employme.board.listeners.addition;

import static dte.employme.messages.Placeholders.EMPLOYER;
import static dte.employme.messages.Placeholders.GOAL;
import static dte.employme.messages.Placeholders.REWARD;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.messages.MessageBuilder;
import dte.employme.services.job.JobService;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.utils.ItemStackUtils;

public class JobAddDiscordWebhook implements JobAddListener
{
	private final String webhookURL, title, message;
	private final JobRewardService jobRewardService;
	private final JobService jobService;

	public JobAddDiscordWebhook(String webhookURL, String title, String message, JobRewardService jobRewardService, JobService jobService) 
	{
		this.webhookURL = webhookURL;
		this.title = title;
		this.message = message;
		this.jobRewardService = jobRewardService;
		this.jobService = jobService;
	}

	@Override
	public void onJobAdded(JobBoard jobBoard, Job job) 
	{
		try(WebhookClient client = WebhookClient.withUrl(this.webhookURL))
		{
			WebhookEmbed embed = new WebhookEmbedBuilder()
					.setTitle(new EmbedTitle(injectPlaceholders(this.title, job), null))
					.setDescription(injectPlaceholders(this.message, job))
					.build();
			
			client.send(embed)
			.thenApply(ReadonlyMessage::getId)
			.thenAccept(messageID -> this.jobService.setWebhookMessageID(job, messageID));
		}
	}
	
	private String injectPlaceholders(String text, Job job) 
	{
		return new MessageBuilder(text)
				.inject(EMPLOYER, job.getEmployer().getName())
				.inject(GOAL, ItemStackUtils.describe(job.getGoal()))
				.inject(REWARD, this.jobRewardService.describe(job.getReward()))
				.first();
	}
}
