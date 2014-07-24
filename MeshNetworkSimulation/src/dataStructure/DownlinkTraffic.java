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
}
