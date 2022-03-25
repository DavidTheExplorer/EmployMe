package dte.employme.commands.sub.employment;

import static dte.employme.messages.MessageKey.PREFIX;
import static dte.employme.messages.MessageKey.THE_JOB_ADDED_NOTIFIERS_ARE;
import static dte.employme.messages.MessageKey.YOUR_NEW_JOB_ADDED_NOTIFIER_IS;
import static dte.employme.messages.Placeholders.JOB_ADDED_NOTIFIER;
import static dte.employme.messages.Placeholders.JOB_ADDED_NOTIFIERS;
import static java.util.stream.Collectors.joining;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import dte.employme.job.addnotifiers.JobAddedNotifier;
import dte.employme.services.job.addnotifiers.JobAddedNotifierService;
import dte.employme.services.message.MessageService;

@CommandAlias("employment|emp")
public class EmploymentAddNotifierCommands extends BaseCommand
{
	private final JobAddedNotifierService jobAddedNotifierService;
	private final MessageService messageService;
	
	public EmploymentAddNotifierCommands(JobAddedNotifierService jobAddedNotifierService, MessageService messageService)
	{
		this.jobAddedNotifierService = jobAddedNotifierService;
		this.messageService = messageService;
	}

	@Subcommand("notifier")
	@Syntax("<notifier name>")
	@Description("Choose which notifications you get once a job is created.")
	@CommandPermission("employme.jobs.notifications")
	public void setNotifications(Player player, JobAddedNotifier notifier) 
	{
		this.jobAddedNotifierService.setPlayerNotifier(player.getUniqueId(), notifier);

		this.messageService.getMessage(YOUR_NEW_JOB_ADDED_NOTIFIER_IS)
		.prefixed(this.messageService.getMessage(PREFIX).first())
		.inject(JOB_ADDED_NOTIFIER, notifier.getName())
		.sendTo(player);
	}

	@Subcommand("notifiers list")
	@Description("See the list of notifiers you can select.")
	@CommandPermission("employme.jobs.notifications")
	public void sendNotificationsList(Player player) 
	{
		String notifiersNames = this.jobAddedNotifierService.getNotifiers().stream()
				.map(JobAddedNotifier::getName)
				.collect(joining(WHITE + ", " + GREEN, "", WHITE + "."));

		this.messageService.getMessage(THE_JOB_ADDED_NOTIFIERS_ARE)
		.inject(JOB_ADDED_NOTIFIERS, notifiersNames)
		.sendTo(player);
	}
}
