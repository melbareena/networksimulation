package dataStructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author Mahdi
 * set of shortest path in a graph.
 */
public class PathMap implements Map<Vertex, List<Path>>
{
	private Map<Vertex, List<Path>> collection = new HashMap<Vertex, List<Path>>();
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
	public Set<java.util.Map.Entry<Vertex, List<Path>>> entrySet()
	{
		return collection.entrySet();
	}

	@Override
	public List<Path> get(Object key)
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
	public List<Path> put(Vertex key, List<Path> paths)
	{
		Collections.sort(paths);
		return collection.put(key, paths);
	}

	public List<Path> put(Vertex key, Path value)
	{
		if(!collection.containsKey(key))
		{
			List<Path> paths =  new ArrayList<>();
			paths.add(value);
			Collections.sort(paths);
			return collection.put(key,paths);
		}
		else
		{
			List<Path> paths = collection.get(key);
			for (Path currentPath : paths)
			{
				if(currentPath == value)
					return null;
			}
			paths = collection.get(key);
			paths.add(value);
			Collections.sort(paths);
			collection.remove(key);
			return collection.put(key,paths);
		}
	}
	
	@Override
	public void putAll(Map<? extends Vertex, ? extends List<Path>> m)
	{
		collection.putAll(m);
		
	}

	@Override
	public List<Path> remove(Object key)
	{
		return collection.remove(key);
	}

	@Override
	public int size()
	{
		return collection.size();
	}

	@Override
	public Collection<List<Path>> values()
	{
		return collection.values();
	}
	
	@Override
	public String toString()
	{
		String s = "";
		
		for (Entry<Vertex, List<Path>> list : collection.entrySet())
		{
			s += list.getKey().getId() + "\n\t";
			
			for (Path p : list.getValue())
			{
				s += p;
			}
			
			s+="\n";
		}
		
		return s;
	}

}
