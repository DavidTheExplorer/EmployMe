package dte.employme.services.job.subscription;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;

public interface JobSubscriptionService
{
	void subscribe(UUID playerUUID, Material goalMaterial);
	void unsubscribe(UUID playerUUID, Material goalMaterial);
	boolean isSubscribedTo(UUID playerUUID, Material goalMaterial);
	Set<Material> getSubscriptions(UUID playerUUID);
	Map<UUID, Set<Material>> getSubscriptions();
	
	void loadSubscriptions();
	void saveSubscriptions();
}