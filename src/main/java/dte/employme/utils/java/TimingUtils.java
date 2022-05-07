package dte.employme.utils.java;

import java.time.Duration;

public class TimingUtils 
{
	public static Duration time(Runnable runnable) 
	{
		long before = System.currentTimeMillis();
		runnable.run();
		long elapsed = System.currentTimeMillis() - before;
		
		return Duration.ofMillis(elapsed);
	}
}