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
	
	public  Map<Vertex, TreeMap<Vertex, Float>> getDownLinkTraffic()
	{
		return downLinkTraffic;
	}
	public  Float getDownLinkTraffic(Vertex g, Vertex v)
	{
		return downLinkTraffic.get(g).get(v);
	}
	public boolean hasDownLinkTraffic(Vertex v)
	{
		return downLinkTraffic.containsKey(v);
	}
}
