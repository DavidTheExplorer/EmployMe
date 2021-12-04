package dte.employme.config;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.configuration.file.YamlConfiguration;

import dte.employme.EmployMe;

public class ConfigFile 
{
	private final File file;
	private final YamlConfiguration config;
	
	private static final File PLUGIN_FOLDER = EmployMe.getInstance().getDataFolder();
	
	static 
	{
		PLUGIN_FOLDER.mkdirs();
	}

	private ConfigFile(File file, YamlConfiguration config) 
	{
		this.file = file;
		this.config = config;
	}
	
	/*
	 * factory methods
	 */
	public static ConfigFile byPath(String path) 
	{
		return byPath(path, false);
	}
	
	public static ConfigFile loadResource(String path) 
	{
		return byPath(path, true);
	}

	public static ConfigFile byPath(String path, boolean isResource)
	{
		if(!path.endsWith(".yml"))
			path += ".yml";

		File file = new File(PLUGIN_FOLDER, path);
		
		if(isResource && !file.exists())
			EmployMe.getInstance().saveResource(path, false);
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		return new ConfigFile(file, config);
	}
	
	/*
	 * creation methods
	 */
	public static void createIfAbsent(ConfigFile config) throws IOException
	{
		if(config.exists()) 
			return;
		
		File file = config.getFile();
		file.getParentFile().mkdirs();
		file.createNewFile();
	}
	
	public static void createIfAbsent(ConfigFile config, Consumer<IOException> exceptionHandler)
	{
		try
		{
			createIfAbsent(config);
		} 
		catch (IOException exception) 
		{
			exceptionHandler.accept(exception);
		}
	}
	
	public YamlConfiguration getConfig() 
	{
		return this.config;
	}
	
	public File getFile() 
	{
		return this.file;
	}
	
	public <T> List<T> getList(String path, Class<T> type)
	{
		return this.config.getList(path, new ArrayList<>()).stream()
				.map(type::cast)
				.collect(toList());
	}

	public boolean exists() 
	{
		return this.file.exists();
	}

	public void save() throws IOException
	{
		this.config.save(this.file);
	}
	
	public void save(Consumer<IOException> exceptionHandler) 
	{
		try 
		{
			save();
		}
		catch (IOException exception) 
		{
			exceptionHandler.accept(exception);
		}
	}
}
