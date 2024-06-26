package dte.employme.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public abstract class AbstractPlaceholderExpansion extends PlaceholderExpansion
{
	private final String identifier, version, author;
	
	protected AbstractPlaceholderExpansion(String identifier, String version, String author)
	{
		this.identifier = identifier;
		this.version = version;
		this.author = author;
	}

	@Override
	public String getIdentifier() 
	{
		return this.identifier;
	}
	
	@Override
	public String getVersion() 
	{
		return this.version;
	}
	
	@Override
	public String getAuthor() 
	{
		return this.author;
	}
}
