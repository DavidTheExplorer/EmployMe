package dte.employme.job.creation;

import org.bukkit.entity.Player;

import dte.employme.board.JobBoard;
import dte.employme.rewards.Reward;

/**
 * This object gathers information about a job that a player wants to create.
 * <p>
 * This class allows the gathering to be in any order - so it's easier to refactor code that's related to creating jobs.
 */
public class JobCreationContext 
{
	private Player employer;
	private Reward reward;
	private JobBoard destinationBoard;
	
	public Reward getReward() 
	{
		return this.reward;
	}
	
	public JobBoard getDestinationBoard() 
	{
		return this.destinationBoard;
	}

	public Player getEmployer()
	{
		return this.employer;
	}

	public void setReward(Reward reward)
	{
		this.reward = reward;
	}
	
	public void setDestinationBoard(JobBoard board) 
	{
		this.destinationBoard = board;
	}
	
	public void setEmployer(Player employer) 
	{
		this.employer = employer;
	}

	@Override
	public String toString()
	{
		return String.format("JobCreationContext [reward=%s, destinationBoard=Global]", this.reward.toString(), this.destinationBoard);
	}
}