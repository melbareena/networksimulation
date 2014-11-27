package dataStructure;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * @author Mahdi
 *
 */
public class UplinkTraffic
{
	
	private Map<Vertex, Integer> sortedTraffic = null;
	
	/**
	 *  key = router Id
	 *  value = traffic 
	 */
	private Map<Vertex, Integer> uplinkTraffic = new TreeMap<Vertex, Integer>();
	
	public void add(Vertex key, int trf)
	{
		uplinkTraffic.put(key, trf);

	}
	
	public void addAll( Map<Vertex, Integer> map)
	{
		
		for (Entry<Vertex, Integer> item : map.entrySet())
		{
			if(uplinkTraffic.containsKey(item.getKey()))
			{
				int preVal = uplinkTraffic.get(item.getKey());
				preVal += item.getValue();
				uplinkTraffic.put(item.getKey(), preVal);
			}
			else
				uplinkTraffic.put(item.getKey(),item.getValue());
		}
		
	}
	public Map<Vertex, Integer> getTraffic()
	{
		if(sortedTraffic == null)
		{
			sortedTraffic = new TreeMap<>(new Comparator<Vertex>()
			{
				@Override
				public int compare(Vertex o1, Vertex o2)
				{
					if(uplinkTraffic.size() == 0) return 0;
					if (uplinkTraffic.get(o1) >= uplinkTraffic.get(o2))
						return -1;
					else
						return 1;
				}
			});
			sortedTraffic.putAll(uplinkTraffic);
		}
		return sortedTraffic;
		
	}
	
	public int getUplinkTraffic(Vertex node)
	{
		return uplinkTraffic.get(node);
	}
	public boolean hasUplinkTraffic(Vertex v)
	{
		return uplinkTraffic.containsKey(v);
	}
	
	public int size()
	{
		if(uplinkTraffic == null || uplinkTraffic.size() < 1 ) return 0;
		int sum = 0;
		for (Integer size : uplinkTraffic.values())
		{
			sum += size;
		}
		return sum;
	}
	
	public String toString()
	{
		if(uplinkTraffic == null) return "";
		
		String str = "";
		for (Entry<Vertex, Integer> item : uplinkTraffic.entrySet())
		{
			str += item.getKey().getId() + "-->" + item.getValue() + "\n";
		}
		str += "totol uplink size: " + size();
		
		return str;
	}
	
	
	
	

}
