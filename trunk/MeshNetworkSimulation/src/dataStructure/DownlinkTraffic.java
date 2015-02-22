package dataStructure;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class DownlinkTraffic implements Cloneable
{
	private Map<Vertex, TreeMap<Vertex, Double>> downLinkTraffic = new TreeMap<Vertex,TreeMap<Vertex, Double>>();
	
	
	public void add(Vertex gateway, TreeMap<Vertex,Double> value)
	{
	
		downLinkTraffic.put(gateway, value);;
	}
	public void add(Vertex gateway, Vertex destination, double traffic)
	{
		if(!downLinkTraffic.containsKey(gateway))
		{
			TreeMap<Vertex, Double> trafficTO = new TreeMap<Vertex, Double>();
			trafficTO.put(destination, traffic);
			downLinkTraffic.put(gateway, trafficTO);
		}
		else
		{
			TreeMap<Vertex, Double> trafficTO = downLinkTraffic.get(gateway);
			if(!trafficTO.containsKey(destination))
				trafficTO.put(destination, traffic);
			else
			{
				double preTraffic = trafficTO.get(destination);
				double newTraffic = preTraffic+traffic;
				trafficTO.put(destination, newTraffic);
			}
			downLinkTraffic.put(gateway, trafficTO);
		}
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
	
	/**
	 * append new traffic to current traffic
	 */
	public void appendTraffic(Vertex source, Vertex destination, double traffic)
	{
		add(source, destination, traffic);			
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
	/**
	 * get the amount of traffic
	 * @return amount of traffic
	 */
	public double size()
	{
		if(downLinkTraffic == null || downLinkTraffic.size() < 1 ) return 0;
		double sum = 0;
		for (TreeMap<Vertex, Double> vrt : downLinkTraffic.values())
		{
			for (Double size : vrt.values())
			{
				sum += size;
			}
			
		}
		return sum;
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
	@Override
	public DownlinkTraffic clone()
	{
		DownlinkTraffic temp = new DownlinkTraffic();
		Map<Vertex, TreeMap<Vertex, Double>> tempDownlinkTrafficMap = new TreeMap<Vertex,TreeMap<Vertex, Double>>();
		TreeMap<Vertex,Double> destiTempMap;
		
		for (Entry<Vertex, TreeMap<Vertex, Double>> downMap : downLinkTraffic.entrySet())
		{
			Vertex key = downMap.getKey();
			destiTempMap = new TreeMap<Vertex, Double>();
			for (Entry<Vertex, Double> item : downMap.getValue().entrySet())
			{
				destiTempMap.put(item.getKey(), item.getValue());
			}
			
			tempDownlinkTrafficMap.put(key, destiTempMap);
		}
		temp.addAll(tempDownlinkTrafficMap);
		return temp;
	}
}
