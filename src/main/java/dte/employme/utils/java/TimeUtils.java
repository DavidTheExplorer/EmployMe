package dte.employme.utils.java;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TimeUtils 
{
	public static Duration time(Runnable runnable) 
	{
		long before = System.currentTimeMillis();
		runnable.run();
		long elapsed = System.currentTimeMillis() - before;
		
		return Duration.ofMillis(elapsed);
	}
	
	public static Duration toDuration(String time) 
	{
		String[] components = time.split(" ");
		int amount = Integer.parseInt(components[0]);
		ChronoUnit unit = ChronoUnit.valueOf(components[1].toUpperCase());
		
		if(unit.isDurationEstimated() && unit != DAYS)
			throw new IllegalArgumentException(String.format("The time unit cannot be above days('%s' provided)", unit.name().toLowerCase()));
		
		return Duration.of(amount, unit);
	}
}