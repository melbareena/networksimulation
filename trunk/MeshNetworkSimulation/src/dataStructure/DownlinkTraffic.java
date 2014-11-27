package dataStructure;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class DownlinkTraffic
{
	private Map<Vertex, TreeMap<Vertex, Float>> downLinkTraffic = new TreeMap<Vertex,TreeMap<Vertex, Float>>();
	
	
	public void add(Vertex gateway, TreeMap<Vertex,Float> value)
	{
	
		downLinkTraffic.put(gateway, value);;
	}
	public void addAll(Map<Vertex, TreeMap<Vertex, Float>> map)
	{
	
		for (Entry<Vertex, TreeMap<Vertex, Float>> parentItem : map.entrySet())
		{
			if(downLinkTraffic.containsKey(parentItem.getKey()))
			{
				TreeMap<Vertex,Float> preChild = downLinkTraffic.get(parentItem.getKey());
				
				for (Entry<Vertex, Float> childeren : parentItem.getValue().entrySet())
				{
					if(preChild.containsKey(childeren.getKey()))
					{
						float preVal = preChild.get(childeren.getKey());
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
	
	public  Map<Vertex, TreeMap<Vertex, Float>> getTraffic()
	{
		return downLinkTraffic;
	}
	public float getTraffic(Vertex g, Vertex v)
	{
		if(downLinkTraffic.get(g) == null) {
			return 0.0F;
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
		for (TreeMap<Vertex, Float> vrt : downLinkTraffic.values())
		{
			for (Float size : vrt.values())
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
		
		for (Entry<Vertex, TreeMap<Vertex, Float>> item : downLinkTraffic.entrySet())
		{		
			str += item.getKey().getId() + "\n";
			for (Entry<Vertex, Float> to : item.getValue().entrySet())
			{
				str += to.getKey().getId() + ": " + to.getValue() + "\n";
			}
			
		}
		str += "total uplink size: " + size();
		
		return str;
	}
}
