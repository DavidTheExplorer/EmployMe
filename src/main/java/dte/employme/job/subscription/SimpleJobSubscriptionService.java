package dte.employme.job.subscription;

import static dte.employme.utils.ChatColorUtils.createSeparationLine;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.bukkit.ChatColor.GRAY;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dte.employme.EmployMe;
import dte.employme.board.JobBoard;
import dte.employme.config.ConfigFile;
import dte.employme.job.Job;
import dte.employme.job.rewards.ItemsReward;
import dte.employme.messages.Message;
import dte.employme.utils.java.EnumUtils;

public class SimpleJobSubscriptionService implements JobSubscriptionService
{
	private final Map<UUID, Set<Material>> subscriptions = new HashMap<>();

	private ConfigFile subscriptionsConfig;

	@Override
	public void onJobAdded(JobBoard jobBoard, Job job) 
	{
		if(!(job.getReward() instanceof ItemsReward))
			return;
		
		ItemsReward itemsReward = (ItemsReward) job.getReward();
		
		itemsReward.getItems().stream()
		.map(ItemStack::getType)
		.forEach(this::notifySubscribersOf);
	}

	@Override
	public void subscribe(UUID playerUUID, Material goalMaterial)
	{
		this.subscriptions.computeIfAbsent(playerUUID, u -> new HashSet<>()).add(goalMaterial);
	}

	@Override
	public void unsubscribe(UUID playerUUID, Material goalMaterial) 
	{
		this.subscriptions.getOrDefault(playerUUID, new HashSet<>()).remove(goalMaterial);
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
	public void loadSubscriptions() 
	{
		this.subscriptionsConfig = ConfigFile.byPath("subscriptions");
		this.subscriptionsConfig.createIfAbsent(IOException::printStackTrace);

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
	
	private void notifySubscribersOf(Material rewardMaterial) 
	{
		this.subscriptions.keySet().stream()
		.map(Bukkit::getPlayer)
		.filter(Objects::nonNull)
		.filter(player -> isSubscribedTo(player.getUniqueId(), rewardMaterial))
		.forEach(player -> 
		{
			playTickSound(player);

			player.sendMessage(createSeparationLine(GRAY, 45));
			Message.sendGeneralMessage(player, Message.SUBSCRIBED_TO_GOAL_NOTIFICATION, EnumUtils.fixEnumName(rewardMaterial));
			player.sendMessage(createSeparationLine(GRAY, 45));
		});
	}

	private static void playTickSound(Player player) 
	{
		new BukkitRunnable()
		{
			private int timesLeft = 3;

			@Override
			public void run()
			{
				this.timesLeft--;

				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

				if(this.timesLeft == 0)
					cancel();
			}
		}.runTaskTimer(EmployMe.getInstance(), 0, 5);
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
