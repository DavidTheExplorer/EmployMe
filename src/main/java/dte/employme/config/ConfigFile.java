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
	
	private static File pluginFolder;

	private ConfigFile(File file, YamlConfiguration config) 
	{
		this.file = file;
		this.config = config;
	}

	public static ConfigFile byPath(String path) 
	{
		if(pluginFolder == null)
		{
			pluginFolder = EmployMe.getInstance().getDataFolder();
			pluginFolder.mkdirs();
		}
		
		if(!path.endsWith(".yml"))
			path += ".yml";

		File file = new File(pluginFolder + File.separator + path);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		return new ConfigFile(file, config);
	}
	
	public <T> List<T> getList(String path, Class<T> type)
	{
		return this.config.getList(path, new ArrayList<>()).stream()
				.map(type::cast)
				.collect(toList());
	}

	public YamlConfiguration getConfig() 
	{
		return this.config;
	}
	
	public void createIfAbsent() throws IOException
	{
		if(this.file.exists()) 
			return;
		
		this.file.getParentFile().mkdirs();
		this.file.createNewFile();
	}
	
	public void createIfAbsent(Consumer<IOException> exceptionHandler)
	{
		try
		{
			createIfAbsent();
		} 
		catch (IOException exception) 
		{
			exceptionHandler.accept(exception);
		}
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
