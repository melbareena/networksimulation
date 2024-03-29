package dataStructure;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class BufferMap implements Map<Link, Buffer>
{
	
	private Map<Link, Buffer> collections  = new HashMap<Link, Buffer>();

	@Override
	public void clear()
	{
		collections.clear();
		
	}

	@Override
	public boolean containsKey(Object key)
	{
		return collections.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return collections.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<Link, Buffer>> entrySet()
	{
		return collections.entrySet();
	}

	@Override
	public Buffer get(Object key)
	{
		return collections.get(key);
	}

	@Override
	public boolean isEmpty()
	{
		return collections.isEmpty();
	}

	@Override
	public Set<Link> keySet()
	{
		return collections.keySet();
	}

	@Override
	public Buffer put(Link key, Buffer newBuffer)
	{
		if(!collections.containsKey(key))
			return collections.put(key, newBuffer);
		
		for (Packet p : newBuffer.getPackets())
		{
			this.put(key, p);
		}
		return collections.get(key);
	}
	
	public Buffer put(Link key, Packet newPacket)
	{
		if(!this.containsKey(key))
		{
			Buffer b = new Buffer();
			b.add(newPacket);
			return collections.put(key, b);
		}
		Buffer b = this.get(key);
		b.add(newPacket);
		return collections.put(key, b);
	
	}

	@Override
	public void putAll(Map<? extends Link, ? extends Buffer> m)
	{
		collections.putAll(m);
		
	}

	@Override
	public Buffer remove(Object key)
	{
		return collections.remove(key);
	}

	@Override
	public int size()
	{
		return collections.size();
	}

	public double trafficSize()
	{
		double size = 0;
		for (Entry<Link, Buffer> lb : collections.entrySet())
		{
			size += lb.getValue().size();
		}
		return (double)Math.round(size * 100000) / 100000;
	}
	public double trafficSize(Link L)
	{
		if(this.containsKey(L))
			return collections.get(L).size();
		return 0;
	}
	
	@Override
	public Collection<Buffer> values()
	{
		return collections.values();
	}
	
	/**Sort the BufferMap looking at the amount of traffic in the buffers.
	 * The first element will be the one with the largest amount of traffic.
	 * @return A TreeMap containing the whole BufferMap sorted.
	 */
	public TreeMap<Link, Buffer> sortByTraffic() {
		TreeMap<Link, Buffer> sorted = new TreeMap<Link, Buffer>(new Comparator<Link>() {
			@Override
			public int compare(Link o1, Link o2) {
				if (collections.size() == 0 || o1.getId() == o2.getId())
					return 0;
				if (collections.get(o1).size() >= collections.get(o2)
						.size())
					return -1;
				else
					return 1;
			}
		});
		sorted.putAll(collections);
		return sorted;
	}

	public List<Packet> sendPacket(Link link, double dataRate, BufferMap transmissionBuffer, int currentTimeSlot)
	{
		Buffer b = this.get(link);	
 		List<Packet> movedPackets = b.send(dataRate, currentTimeSlot);
 		assert(movedPackets.size() > 0) : "No packets, class: BufferMap, Method: send";
 		
 		for (Packet p : movedPackets)
 		{
 			if(!p.isReceived())
 				transmissionBuffer.put(p.getCurrentLink(), p);
 			this.update();
		}
		
		return movedPackets;
		
	}
	
	private void update()
	{
		Map<Link, Buffer> updated  = new HashMap<Link, Buffer>();
		for (Entry<Link, Buffer> lb : this.collections.entrySet())
		{
			if(lb.getValue().size() > 0)
				updated.put(lb.getKey(), lb.getValue());
		}
		collections.clear();
		collections.putAll(updated);
	}
	
	@Override
	public String toString()
	{
		
		StringBuilder builder = new StringBuilder();
		for (java.util.Map.Entry<Link, Buffer> lb : collections.entrySet())
		{
			builder.append("Buffer Link: #" + lb.getKey().getId() + "\n");
			builder.append(lb.getValue().toString() + "\n");
		}
		
		return builder.toString();
	}

	public void Append(BufferMap newBuffer)
	{
		for (java.util.Map.Entry<Link, Buffer> lb : newBuffer.entrySet())
			this.put(lb.getKey(), lb.getValue());
	
	}
}
