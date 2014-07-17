package dataStructure;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LinksChannelMap implements Map<Link, Channel>
{

	private Map<Link, Channel> collection = new TreeMap<Link, Channel>();

	@Override
	public void clear()
	{
		collection.clear();
		
	}

	@Override
	public boolean containsKey(Object key)
	{
		return collection.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return collection.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<Link, Channel>> entrySet()
	{
		return collection.entrySet();
	}

	@Override
	public Channel get(Object key)
	{
		return collection.get(key);
	}

	@Override
	public boolean isEmpty()
	{
		return collection.isEmpty();
	}

	@Override
	public Set<Link> keySet()
	{
		return collection.keySet();
	}

	@Override
	public Channel put(Link key, Channel value)
	{
		if(collection.containsKey(key))
		{
			this.remove(key);
		}
		return collection.put(key, value);
	}

	@Override
	public void putAll(Map<? extends Link, ? extends Channel> m)
	{
		collection.putAll(m);
		
	}

	@Override
	public Channel remove(Object key)
	{
		return collection.remove(key);
	}

	@Override
	public int size()
	{
		return collection.size();
	}

	@Override
	public Collection<Channel> values()
	{
		return collection.values();
	}
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		
		for (Entry<Link, Channel> iter : collection.entrySet())
		{
			builder.append(iter.getKey().getId());
			builder.append(": ");
			builder.append(iter.getValue());
			builder.append("\n");
		}
		return builder.toString();
	}
	
}
