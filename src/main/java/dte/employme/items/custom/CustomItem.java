package dte.employme.items.custom;

import org.bukkit.inventory.ItemStack;

public interface CustomItem
{
	boolean equals(ItemStack item);
	ItemStack getItemStack();
	
	
	
	public abstract class AbstractCustomItem implements CustomItem
	{
		protected final ItemStack item;
		
		public AbstractCustomItem(ItemStack item) 
		{
			this.item = item;
		}
		
		@Override
		public ItemStack getItemStack() 
		{
			return new ItemStack(this.item);
		}
	}
}
