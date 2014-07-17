package dataStructure;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
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
	public Buffer put(Link key, Buffer value)
	{
		return collections.put(key, value);
	}
	
	public Buffer put(Link key, Packet value)
	{
		if(!this.containsKey(key))
		{
			Buffer b = new Buffer();
			b.add(value);
			return collections.put(key, b);
		}
		Buffer b = this.get(key);
		b.add(value);
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
		return size;
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
	
	public TreeMap<Link,Buffer> sort()
	{
		 TreeMap<Link,Buffer> sorted = new TreeMap<Link,Buffer>(new Comparator<Link>()
		{

			@Override
			public int compare(Link o1, Link o2)
			{
				if(collections.size() == 0 || o1.getId() == o2.getId()) return 0;
				if (collections.get(o1).size() >= collections.get(o2).size())
					return -1;
				else
					return 1;
			}
		});
		 
		 sorted.putAll(collections);
		 return sorted;
	}

	public Packet sendPacket(Link link, int dataRate, BufferMap transmissionBuffer)
	{
		Buffer b = this.get(link);	
 		Packet movedPacket = b.send(dataRate);
		
		if(!movedPacket.isReceived())
			transmissionBuffer.put(movedPacket.getCurrentLink(), movedPacket);
		this.update();
		return movedPacket;
		
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
}
