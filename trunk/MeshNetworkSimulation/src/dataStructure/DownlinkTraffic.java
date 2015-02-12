package dataStructure;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class DownlinkTraffic
{
	private Map<Vertex, TreeMap<Vertex, Double>> downLinkTraffic = new TreeMap<Vertex,TreeMap<Vertex, Double>>();
	
	
	public void add(Vertex gateway, TreeMap<Vertex,Double> value)
	{
	
		downLinkTraffic.put(gateway, value);;
	}
	public void addAll(Map<Vertex, TreeMap<Vertex, Double>> map)
	{
	
		for (Entry<Vertex, TreeMap<Vertex, Double>> parentItem : map.entrySet())
		{
			if(downLinkTraffic.containsKey(parentItem.getKey()))
			{
				TreeMap<Vertex,Double> preChild = downLinkTraffic.get(parentItem.getKey());
				
				for (Entry<Vertex, Double> childeren : parentItem.getValue().entrySet())
				{
					if(preChild.containsKey(childeren.getKey()))
					{
						double preVal = preChild.get(childeren.getKey());
						preVal += childeren.getValue();
						preChild.put(childeren.getKey(),preVal);
					}
					else
					{
						preChild.put(childeren.getKey(),childeren.getValue());
					}
				}
				downLinkTraffic.put(parentItem.getKey(), preChild);
			}
			else
			
				downLinkTraffic.put(parentItem.getKey(),parentItem.getValue());
		}
		
	}
	
	public  Map<Vertex, TreeMap<Vertex, Double>> getTraffic()
	{
		return downLinkTraffic;
	}
	public Double getTraffic(Vertex g, Vertex v)
	{
		if(downLinkTraffic.get(g) == null) {
			return 0.0d;
		}
		return (downLinkTraffic.get(g).containsKey(v)) ? downLinkTraffic.get(g).get(v) : 0.0F;
	}
	public boolean hasTraffic(Vertex v)
	{
		return downLinkTraffic.containsKey(v);
	}
	public int size()
	{
		if(downLinkTraffic == null || downLinkTraffic.size() < 1 ) return 0;
		float sum = 0;
		for (TreeMap<Vertex, Double> vrt : downLinkTraffic.values())
		{
			for (Double size : vrt.values())
			{
				sum += size;
			}
			
		}
		return (int)sum;
	}
	
	public String toString()
	{
		if(downLinkTraffic == null) return "";
		
		String str = "";
		
		for (Entry<Vertex, TreeMap<Vertex, Double>> item : downLinkTraffic.entrySet())
		{		
			str += item.getKey().getId() + "\n";
			for (Entry<Vertex, Double> to : item.getValue().entrySet())
			{
				str += to.getKey().getId() + ": " + to.getValue() + "\n";
			}
			
		}
		str += "total uplink size: " + size();
		
		return str;
	}
}
