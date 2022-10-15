package dte.employme.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class AutoUpdater 
{
	//plugin's data
	private final Plugin plugin;
	private final int pluginID;

	private Consumer<String> newVersionAction;
	private Consumer<IOException> requestErrorHandler;

	private AutoUpdater(Plugin plugin, int pluginID) 
	{
		this.plugin = plugin;
		this.pluginID = pluginID;
	}

	public static AutoUpdater forPlugin(Plugin plugin, int pluginID) 
	{
		Objects.requireNonNull(plugin, "The plugin must be provided!");
		Objects.requireNonNull(pluginID, "The ID for the plugin in Spigot's website must be provided!");
		
		return new AutoUpdater(plugin, pluginID);
	}

	public AutoUpdater onNewUpdate(Consumer<String> newVersionAction) 
	{
		this.newVersionAction = newVersionAction;
		return this;
	}

	public AutoUpdater onFailedRequest(Consumer<IOException> errorHandler) 
	{
		this.requestErrorHandler = errorHandler;
		return this;
	}

	public void check() 
	{
		Objects.requireNonNull(this.requestErrorHandler, "The request error handler must be provided!");
		Objects.requireNonNull(this.newVersionAction, "The action to perform if there's a new version must be provided!");
		
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> 
		{
			String remoteVersion;
			
			//request the current plugin version from spigot's website
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(getVersionURL().openConnection().getInputStream(), UTF_8))) 
			{
				remoteVersion = reader.readLine();
			}
			catch(IOException exception) 
			{
				this.requestErrorHandler.accept(exception);
				return;
			}

			if(!this.plugin.getDescription().getVersion().equals(remoteVersion))
				this.newVersionAction.accept(remoteVersion);
		});
	}

	private URL getVersionURL() throws MalformedURLException 
	{
		return new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.pluginID);
	}
}