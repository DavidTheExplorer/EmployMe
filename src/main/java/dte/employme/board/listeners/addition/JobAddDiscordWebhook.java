package dte.employme.board.listeners.addition;

import java.io.IOException;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.messages.MessageBuilder;
import dte.employme.services.rewards.JobRewardService;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.java.DiscordWebhook;
import dte.employme.utils.java.DiscordWebhook.EmbedObject;

public class JobAddDiscordWebhook implements JobAddListener
{
	private final String webhookURL, title, message;
	private final JobRewardService jobRewardService;

	public JobAddDiscordWebhook(String webhookURL, String title, String message, JobRewardService jobRewardService) 
	{
		this.webhookURL = webhookURL;
		this.title = title;
		this.message = message;
		this.jobRewardService = jobRewardService;
	}

	@Override
	public void onJobAdded(JobBoard jobBoard, Job job) 
	{
		DiscordWebhook webhook = new DiscordWebhook(this.webhookURL);
		
		webhook.addEmbed(new EmbedObject()
				.setTitle(injectPlaceholders(this.title, job))
				.setDescription(injectPlaceholders(this.message, job)));

		try
		{
			webhook.execute();
		}
		catch(IOException exception) 
		{
			exception.printStackTrace();
		}
	}
	
	private String injectPlaceholders(String text, Job job) 
	{
		return new MessageBuilder(text)
				.inject("employer", job.getEmployer().getName())
				.inject("goal", ItemStackUtils.describe(job.getGoal()))
				.inject("reward", this.jobRewardService.describe(job.getReward()))
				.first();
	}
}
