package dte.employme.rewards;

public interface PartialReward extends Reward
{
	/**
	 * Returns this reward but the represented value is reduced by the provided {@code percentage} - which represents a job's partial completion.
	 * 
	 * @param percentage The completion percentage to reduce (from 1 to 100)
	 * @return This reward after owning job would be partially completed.
	 */
	PartialReward afterPartialCompletion(double percentage);
}