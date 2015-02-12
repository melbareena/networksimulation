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
public class LinkTrafficMap implements Map<Link,Double>
{
	
	private Map<Link, Double> collection;
	
	public LinkTrafficMap(){collection = new TreeMap<>();}
	public LinkTrafficMap( Map<Link, Double> sorted){ collection = sorted;}

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
	public Set<java.util.Map.Entry<Link, Double>> entrySet()
	{
		return collection.entrySet();
	}

	@Override
	public Double get(Object key)
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
	public Double put(Link key, Double value)
	{
		if(collection.containsKey(key))
		{
			Double preValue = collection.remove(key);
			return collection.put(key, value + preValue);
		}
		Double var = collection.put(key, value);
		return var;
	}

	@Override
	public void putAll(Map<? extends Link, ? extends Double> m)
	{
		collection.putAll(m);
		
	}

	@Override
	public Double remove(Object key)
	{
		return collection.remove(key);
	}

	@Override
	public int size()
	{
		return collection.size();
	}

	@Override
	public Collection<Double> values()
	{
		return collection.values();
	}
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		
		for (Entry<Link, Double> iter : collection.entrySet())
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
	public TreeMap<Link, Double> Sort()
	{
		//LinksTrafficComparator com = new LinksTrafficComparator(this.collection);
		TreeMap<Link, Double> sorted = new TreeMap<Link, Double>(new Comparator<Link>()
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
