package dte.employme.items;

import static dte.employme.utils.ChatColorUtils.bold;
import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;

import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;
import dte.employme.utils.items.ItemBuilder;

public class JobDeletionIcon 
{
	public static ItemStack create(JobBoard jobBoard, Job job) 
	{
		return new ItemBuilder(JobBasicIcon.create(job))
				.addToLore(true,
						createSeparationLine(GRAY, 23),
						bold(DARK_RED) + "Click to Delete!",
						createSeparationLine(GRAY, 23),
						JobItemUtils.createIDLoreLine(job, jobBoard))
				.createCopy();
	}
}
