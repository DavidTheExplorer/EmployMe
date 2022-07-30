package dte.employme.board.listenable;

import static dte.employme.messages.Placeholders.EMPLOYER;
import static dte.employme.messages.Placeholders.GOAL;
import static dte.employme.messages.Placeholders.REWARD;

import java.io.IOException;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.messages.MessageBuilder;
import dte.employme.utils.ItemStackUtils;
import dte.employme.utils.java.DiscordWebhook;
import dte.employme.utils.java.DiscordWebhook.EmbedObject;

public class JobAddDiscordWebhook implements JobAddListener
{
	private final String webhookURL, title, message;

	public JobAddDiscordWebhook(String webhookURL, String title, String message) 
	{
		this.webhookURL = webhookURL;
		this.title = title;
		this.message = message;
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
				.inject(EMPLOYER, job.getEmployer().getName())
				.inject(GOAL, ItemStackUtils.describe(job.getGoal()))
				.inject(REWARD, job.getReward().getDescription())
				.first();
	}
}
