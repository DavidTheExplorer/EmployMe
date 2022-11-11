package dte.employme.board.listeners.completion;

import org.bukkit.entity.Player;

import club.minnced.discord.webhook.WebhookClient;
import dte.employme.board.JobBoard.JobCompletionContext;
import dte.employme.job.Job;
import dte.employme.services.job.JobService;

public class JobCompletedWebhookListener implements JobCompleteListener
{
	private final String webhookURL;
	private final JobService jobService;
	
	public JobCompletedWebhookListener(String webhookURL, JobService jobService) 
	{
		this.webhookURL = webhookURL;
		this.jobService = jobService;
	}
	
	@Override
	public void onJobCompleted(Job job, Player whoCompleted, JobCompletionContext context)
	{
		try(WebhookClient client = WebhookClient.withUrl(this.webhookURL))
		{
			this.jobService.getWebhookMessageID(job)
			.ifPresent(id -> 
			{
				System.out.println("Removing a webhook job message!");
				client.delete(id);
			});
		}
	}
}
