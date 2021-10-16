package dte.employme.job.subscription;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;

import dte.employme.board.listeners.JobAddListener;

public interface JobSubscriptionService extends JobAddListener
{
	void subscribe(UUID playerUUID, Material goalMaterial);
	void unsubscribe(UUID playerUUID, Material goalMaterial);
	boolean isSubscribedTo(UUID playerUUID, Material goalMaterial);
	Set<Material> getSubscriptions(UUID playerUUID);
	
	void loadSubscriptions();
	void saveSubscriptions();
}