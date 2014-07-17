package dataStructure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public class IFactorMap implements  Map<Integer, Double>
{
	private Map<Integer, Double> collection = new HashMap<Integer, Double>();
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
	public Set<java.util.Map.Entry<Integer, Double>> entrySet()
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
	public Set<Integer> keySet()
	{
		return collection.keySet();
	}

	@Override
	public Double put(Integer key, Double value)
	{
		return collection.put(key, value);
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends Double> m)
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
}
