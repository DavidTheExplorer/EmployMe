package dte.employme.utils.java;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<K, V>
{
	private final Map<K, V> map = new LinkedHashMap<>();
	
	public MapBuilder<K, V> put(K key, V value)
	{
		this.map.put(key, value);
		return this;
	}
	
	public MapBuilder<K, V> putAll(Map<K, V> map)
	{
		this.map.putAll(map);
		return this;
	}
	
	public Map<K, V> build()
	{
		return this.map;
	}
	
	public Map<K, V> buildView()
	{
		return Collections.unmodifiableMap(this.map);
	}
	
	public <T extends Map<K, V>> T buildTo(T emptyBase)
	{
		emptyBase.putAll(this.map);

		return emptyBase;
	}
}