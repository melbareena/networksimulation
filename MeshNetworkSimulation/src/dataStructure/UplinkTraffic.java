package dataStructure;

import java.util.Comparator;
import java.util.Map;
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
	public Map<Vertex, Integer> getUplinkTraffic()
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
	
	
	

}
