package dataStructure;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author umroot
 *
 */
public class LinkTrafficMap implements Map<Link,Float>
{
	
	private Map<Link, Float> collection;
	
	public LinkTrafficMap(){collection = new TreeMap<>();}
	public LinkTrafficMap( Map<Link, Float> sorted){ collection = sorted;}

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
		return containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<Link, Float>> entrySet()
	{
		return collection.entrySet();
	}

	@Override
	public Float get(Object key)
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

	/*
    * if the key is exist in the map the value add to the previous value.
	 */
	@Override
	public Float put(Link key, Float value)
	{
		if(collection.containsKey(key))
		{
			 Float preValue = collection.remove(key);
			return collection.put(key, value + preValue);
		}
		Float var = collection.put(key, value);
		return var;
	}

	@Override
	public void putAll(Map<? extends Link, ? extends Float> m)
	{
		collection.putAll(m);
		
	}

	@Override
	public Float remove(Object key)
	{
		return collection.remove(key);
	}

	@Override
	public int size()
	{
		return collection.size();
	}

	@Override
	public Collection<Float> values()
	{
		return collection.values();
	}
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		
		for (Entry<Link, Float> iter : collection.entrySet())
		{
			builder.append(iter.getKey().getId());
			builder.append(": ");
			builder.append(iter.getValue());
			builder.append("\n");
		}
		return builder.toString();
	}
	
	/**
	 * @return return a stored tree map. this output sorted based on map's values.
	 */
	public TreeMap<Link, Float> Sort()
	{
		//LinksTrafficComparator com = new LinksTrafficComparator(this.collection);
		TreeMap<Link, Float> sorted = new TreeMap<Link, Float>(new Comparator<Link>()
		{

			@Override
			public int compare(Link o1, Link o2)
			{
				if(collection.size() == 0 || o1.getId() == o2.getId()) return 0;
				if (collection.get(o1) >= collection.get(o2))
					return -1;
				else
					return 1;
			}
			
		});
		sorted.putAll(this.collection);
		return sorted;
		
	}
}
