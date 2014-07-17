package dataStructure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class OutcomingLinksMap implements Map<Vertex, Vector<Link>>
{
	
	private Map<Vertex, Vector<Link>> collection = new HashMap<Vertex, Vector<Link>>();

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
	public Set<java.util.Map.Entry<Vertex, Vector<Link>>> entrySet()
	{
		return collection.entrySet();
	}

	@Override
	public Vector<Link> get(Object key)
	{
		return collection.get(key);
	}

	@Override
	public boolean isEmpty()
	{
		return collection.isEmpty();
	}

	@Override
	public Set<Vertex> keySet()
	{
		return collection.keySet();
	}

	@Override
	public Vector<Link> put(Vertex key, Vector<Link> value)
	{
		
		return collection.put(key,value);
	}
	public Vector<Link> put(Vertex key, Link value)
	{
		if(collection.containsKey(key))
		{
			Vector<Link> incoming = collection.remove(key);
			incoming.add(value);
			return collection.put(key,incoming);
		}
		Vector<Link> l = new Vector<>();
		l.add(value);
		return collection.put(key,l);
	}
	@Override
	public void putAll(Map<? extends Vertex, ? extends Vector<Link>> m)
	{
		collection.putAll(m);	
	}

	@Override
	public Vector<Link> remove(Object key)
	{
		return collection.remove(key);
	}

	@Override
	public int size()
	{
		return collection.size();
	}

	@Override
	public Collection<Vector<Link>> values()
	{
		return collection.values();
	}
	public boolean isOutcomingLink(Vertex x, Link l)
	{
		Vector<Link> links = collection.get(x);
		return links.contains(l);
	}

}
