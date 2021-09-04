package dte.employme.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dte.employme.board.InventoryJobBoard;
import dte.employme.messages.Message;
import dte.employme.reward.visitor.RewardNameVisitor;

public class JobInventoryListener implements Listener
{
	@EventHandler
	public void onCompleteAttempt(InventoryClickEvent event) 
	{
		Inventory inventory = event.getInventory();

		InventoryJobBoard.getRepresentedBoard(inventory).ifPresent(inventoryBoard -> 
		{
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			
			if(item == null)
				return;
			
			Player player = (Player) event.getWhoClicked();
			
			inventoryBoard.getJobID(item)
			.flatMap(inventoryBoard::getJobByID)
			.filter(job -> job.getGoal().hasReached(player))
			
			.ifPresent(job ->
			{
				player.closeInventory();
				inventoryBoard.onComplete(job, player);
				
				Message.JOB_SUCCESSFULLY_COMPLETED.sendTo(player, job.getReward().accept(RewardNameVisitor.INSTANCE));
			});
		});
	}
}