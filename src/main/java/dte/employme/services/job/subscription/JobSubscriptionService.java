package dte.employme.services.job.subscription;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;

public interface JobSubscriptionService
{
	void subscribe(UUID playerUUID, Material material);
	void unsubscribe(UUID playerUUID, Material material);
	boolean isSubscribedTo(UUID playerUUID, Material material);
	Set<Material> getSubscriptions(UUID playerUUID);
	
	void loadSubscriptions();
	void saveSubscriptions();
}