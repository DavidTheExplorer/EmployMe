package dte.employme.services.job.subscription;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Material;

import dte.spigotconfiguration.SpigotConfig;

public class SimpleJobSubscriptionService implements JobSubscriptionService
{
	private final Map<UUID, Set<Material>> subscriptions = new HashMap<>();
	private final SpigotConfig subscriptionsConfig;
	
	public SimpleJobSubscriptionService(SpigotConfig subscriptionsConfig) 
	{
		this.subscriptionsConfig = subscriptionsConfig;
	}

	@Override
	public void subscribe(UUID playerUUID, Material material)
	{
		this.subscriptions.computeIfAbsent(playerUUID, u -> new HashSet<>()).add(material);
	}

	@Override
	public void unsubscribe(UUID playerUUID, Material material) 
	{
		Set<Material> materials = this.subscriptions.getOrDefault(playerUUID, new HashSet<>());
		materials.remove(material);
		
		if(materials.isEmpty())
			this.subscriptions.remove(playerUUID);
	}

	@Override
	public boolean isSubscribedTo(UUID playerUUID, Material material)
	{
		return this.subscriptions.getOrDefault(playerUUID, new HashSet<>()).contains(material);
	}

	@Override
	public Set<Material> getSubscriptions(UUID playerUUID) 
	{
		return new HashSet<>(this.subscriptions.getOrDefault(playerUUID, new HashSet<>()));
	}

	@Override
	public void loadSubscriptions() 
	{
		this.subscriptionsConfig.getKeys(false).stream()
		.map(UUID::fromString)
		.collect(toMap(Function.identity(), playerUUID -> parseMaterials(this.subscriptionsConfig.getString(playerUUID.toString()))))
		.forEach(this.subscriptions::put);
	}

	@Override
	public void saveSubscriptions() 
	{
		this.subscriptions.keySet()
		.stream()
		.collect(toMap(UUID::toString, this::getSubscribedMaterialsNames))
		.forEach(this.subscriptionsConfig::set);

		try 
		{
			this.subscriptionsConfig.save();
		}
		catch(IOException exception) 
		{
			exception.printStackTrace();
		}
	}
	
	private String getSubscribedMaterialsNames(UUID playerUUID) 
	{
		return this.subscriptions.get(playerUUID).stream()
				.map(Material::name)
				.collect(joining(", "));
	}

	private static Set<Material> parseMaterials(String materialsNames)
	{
		return Arrays.stream(materialsNames.split(", "))
				.map(Material::valueOf)
				.collect(toSet());
	}
}
