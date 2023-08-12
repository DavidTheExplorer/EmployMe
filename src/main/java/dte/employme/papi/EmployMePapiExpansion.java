package dte.employme.papi;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import dte.employme.board.JobBoard;

public class EmployMePapiExpansion extends AbstractPlaceholderExpansion
{
	private final JobBoard globalJobBoard;

	public EmployMePapiExpansion(JobBoard globalJobBoard)
	{
		super("employme", "1.0.0", "HorrendousEntity");

		this.globalJobBoard = globalJobBoard;
	}
	
	@Override
	public String onRequest(OfflinePlayer player, String placeholder) 
	{
		//handles %employme_jobs_amount%
		if(placeholder.equals("jobs_amount"))
			return String.valueOf(this.globalJobBoard.size());

		//handles %employme_jobs_amount_COUNTER_MATERIAL%
		else if(placeholder.startsWith("jobs_amount_")) 
			return handleCounterPlaceholder(placeholder);

		return null;
	}

	private String handleCounterPlaceholder(String placeholder) 
	{
		String counterName = placeholder.substring(12, placeholder.indexOf('_', 12));
		String materialName = placeholder.substring(placeholder.indexOf(counterName) + counterName.length() +1).toUpperCase();
		
		JobCounter jobCounter = JobCounter.getByName(counterName);

		Material material = Optional.ofNullable(Material.getMaterial(materialName))
				.orElseThrow(() -> new IllegalArgumentException(String.format("Could not parse placeholder because '%s' is not an item!", materialName)));

		return String.valueOf(jobCounter.count(this.globalJobBoard, material));
	}

	private enum JobCounter
	{
		BY_GOAL
		{
			@Override
			public long count(JobBoard jobBoard, Material goalMaterial)
			{
				return jobBoard.getOfferedJobs().stream()
						.filter(job -> job.getGoal().getType() == goalMaterial)
						.count();
			}
		};

		public abstract long count(JobBoard jobBoard, Material contextMaterial);

		public static JobCounter getByName(String name)
		{
			if(name.equalsIgnoreCase("byGoal"))
				return BY_GOAL;

			throw new IllegalArgumentException(String.format("Cannot find a counter named '%s'!", name));
		}
	}
}