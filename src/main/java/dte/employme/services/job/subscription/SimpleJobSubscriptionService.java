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

import dte.employme.config.ConfigFile;

public class SimpleJobSubscriptionService implements JobSubscriptionService
{
	private final Map<UUID, Set<Material>> subscriptions = new HashMap<>();
	private final ConfigFile subscriptionsConfig;
	
	public SimpleJobSubscriptionService(ConfigFile subscriptionsConfig) 
	{
		this.subscriptionsConfig = subscriptionsConfig;
	}

	@Override
	public void subscribe(UUID playerUUID, Material goalMaterial)
	{
		this.subscriptions.computeIfAbsent(playerUUID, u -> new HashSet<>()).add(goalMaterial);
	}

	@Override
	public void unsubscribe(UUID playerUUID, Material goalMaterial) 
	{
		Set<Material> materials = this.subscriptions.getOrDefault(playerUUID, new HashSet<>());
		materials.remove(goalMaterial);
		
		if(materials.isEmpty())
			this.subscriptions.remove(playerUUID);
	}

	@Override
	public boolean isSubscribedTo(UUID playerUUID, Material goalMaterial)
	{
		return this.subscriptions.getOrDefault(playerUUID, new HashSet<>()).contains(goalMaterial);
	}

	@Override
	public Set<Material> getSubscriptions(UUID playerUUID) 
	{
		return new HashSet<>(this.subscriptions.getOrDefault(playerUUID, new HashSet<>()));
	}
	
	@Override
	public Map<UUID, Set<Material>> getSubscriptions() 
	{
		return new HashMap<>(this.subscriptions);
	}

	@Override
	public void loadSubscriptions() 
	{
		this.subscriptionsConfig.getConfig().getKeys(false).stream()
		.map(UUID::fromString)
		.collect(toMap(Function.identity(), playerUUID -> parseMaterials(this.subscriptionsConfig.getConfig().getString(playerUUID.toString()))))
		.forEach(this.subscriptions::put);
	}

	@Override
	public void saveSubscriptions() 
	{
		this.subscriptions.keySet()
		.stream()
		.collect(toMap(UUID::toString, this::getSubscribedMaterialsNames))
		.forEach((stringUUID, goalsNames) -> this.subscriptionsConfig.getConfig().set(stringUUID, goalsNames));

		this.subscriptionsConfig.save(IOException::printStackTrace);
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
