package dte.employme.job;

import org.bukkit.Bukkit;

import net.milkbowl.vault.economy.Economy;

public enum JobType
{
	ITEMS
	{
		@Override
		public boolean canBeUsed()
		{
			return true;
		}

		@Override
		public String getErrorMessage() 
		{
			return null;
		}
	},
	
	MONEY
	{
		@Override
		public boolean canBeUsed() 
		{
			if(Bukkit.getPluginManager().getPlugin("Vault") == null)
				return false;

			return Bukkit.getServicesManager().getRegistration(Economy.class) != null;
		}

		@Override
		public String getErrorMessage() 
		{
			if(Bukkit.getPluginManager().getPlugin("Vault") == null)
				return "Value must be installed";
			
			return "Economy Provider(e.g. EssentialsX) must be installed";
		}
	};
	
	public abstract boolean canBeUsed();
	public abstract String getErrorMessage();
}
