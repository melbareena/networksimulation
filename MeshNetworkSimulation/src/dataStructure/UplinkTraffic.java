package dataStructure;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * @author Mahdi
 *
 */
public class UplinkTraffic implements Cloneable
{
	
	private Map<Vertex, Double> sortedTraffic = null;
	
	/**
	 *  key = router Id
	 *  value = traffic 
	 */
	private Map<Vertex, Double> uplinkTraffic = new TreeMap<Vertex, Double>();
	
	public void add(Vertex key, double trf)
	{
		if(!uplinkTraffic.containsKey(key))
			uplinkTraffic.put(key, trf);
		else
		{
			double pre = uplinkTraffic.get(key);
			uplinkTraffic.put(key, pre + trf);
		}
	}
	
	public void addAll( Map<Vertex, Double> map)
	{
		
		for (Entry<Vertex, Double> item : map.entrySet())
		{
			if(uplinkTraffic.containsKey(item.getKey()))
			{
				double preVal = uplinkTraffic.get(item.getKey());
				preVal += item.getValue();
				uplinkTraffic.put(item.getKey(), preVal);
			}
			else
				uplinkTraffic.put(item.getKey(),item.getValue());
		}
		
	}
	public Map<Vertex, Double> getTraffic()
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
	
	public double getUplinkTraffic(Vertex node)
	{
		return uplinkTraffic.get(node);
	}
	public boolean hasUplinkTraffic(Vertex v)
	{
		return uplinkTraffic.containsKey(v);
	}
	
	public double size()
	{
		if(uplinkTraffic == null || uplinkTraffic.size() < 1 ) return 0;
		double sum = 0;
		for (Double size : uplinkTraffic.values())
		{
			sum += size;
		}
		return sum;
	}
	
	public String toString()
	{
		if(uplinkTraffic == null) return "";
		
		String str = "";
		for (Entry<Vertex, Double> item : uplinkTraffic.entrySet())
		{
			str += item.getKey().getId() + "-->" + item.getValue() + "\n";
		}
		str += "totol uplink size: " + size();
		
		return str;
	}
	
	@Override
	public UplinkTraffic clone()
	{
		UplinkTraffic temp = new UplinkTraffic();
		
		Map<Vertex, Double> tempMap = new TreeMap<Vertex, Double>();
		for (Entry<Vertex, Double> ut : uplinkTraffic.entrySet())
		{
			tempMap.put(ut.getKey(), ut.getValue());
		}
		temp.addAll(tempMap);
		return temp;
	}

	public void appendTraffic(Vertex source, double traffic)
	{
		add(source,traffic);
		
	}
	
	
	
	

}
