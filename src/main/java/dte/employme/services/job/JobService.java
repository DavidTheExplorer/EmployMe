package dte.employme.services.job;

import java.time.Duration;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import de.tr7zw.nbtapi.NBTItem;
import dte.employme.job.Job;

public interface JobService 
{
	boolean hasFinished(Player player, Job job);
	String describeInGame(Job job);
	
	void loadJobs();
	void saveJobs();
	
	void deleteAfter(Job job, Duration delay);
	void stopAutoDelete(Job job);
	void loadAutoDeletionData();
	void saveAutoDeletionData();
	
	public static boolean isGoal(ItemStack item, ItemStack goal) 
	{
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.removeKey("RepairCost");
		ItemStack fixedItem = nbtItem.getItem();

		if(!fixedItem.isSimilar(goal))
			return false;

		if(fixedItem.getItemMeta() instanceof Damageable && ((Damageable) fixedItem.getItemMeta()).hasDamage())
			return false;

		return true;
	}
}