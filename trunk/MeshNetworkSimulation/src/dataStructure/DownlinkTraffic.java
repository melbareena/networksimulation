package dataStructure;

import java.util.Map;
import java.util.TreeMap;

public final class DownlinkTraffic
{
	private Map<Vertex, TreeMap<Vertex, Float>> downLinkTraffic = new TreeMap<Vertex,TreeMap<Vertex, Float>>();
	
	
	public void add(Vertex gateway, TreeMap<Vertex,Float> value)
	{
	
		downLinkTraffic.put(gateway, value);;
	}
	
	public  Map<Vertex, TreeMap<Vertex, Float>> getTraffic()
	{
		return downLinkTraffic;
	}
	public  Float getTraffic(Vertex g, Vertex v)
	{
		return downLinkTraffic.get(g).get(v);
	}
	public boolean hasTraffic(Vertex v)
	{
		return downLinkTraffic.containsKey(v);
	}
}
