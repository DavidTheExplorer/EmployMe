package dte.employme.items;

import static dte.employme.utils.ChatColorUtils.colorize;

import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.JobBoard;
import dte.employme.job.Job;

public class JobItemUtils
{
	//Container of static methods
	private JobItemUtils(){}
	
	public static Optional<String> getJobID(ItemStack jobIcon)
	{
		if(!jobIcon.hasItemMeta() || !jobIcon.getItemMeta().hasLore() || jobIcon.getItemMeta().getLore().isEmpty())
			return Optional.empty();

		List<String> lore = jobIcon.getItemMeta().getLore();
		String lastLine = lore.get(lore.size()-1);

		return Optional.of(ChatColor.stripColor(lastLine.substring(6)));
	}

	public static String createIDLoreLine(Job job, JobBoard jobBoard)
	{
		return colorize(String.format("&7ID: %s", jobBoard.getJobID(job).get()));
	}
}