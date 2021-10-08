package dte.employme.utils.java;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator 
{
	private static final Map<Class<?>, Object> SERVICES = new HashMap<>();
	
	public static <T> void register(Class<T> clazz, T instance) 
	{
		SERVICES.put(clazz, instance);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> clazz)
	{
		return (T) SERVICES.get(clazz);
	}
}
