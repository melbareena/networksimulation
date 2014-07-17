package dataStructure;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class ChannelOccuranceMap
{
	private Map<Channel, Integer> collection = new TreeMap<>(Collections.reverseOrder());
	
	public ChannelOccuranceMap()
	{
		for (Channel availableChannel : setting.ApplicationSettingFacade.Channel.getChannel())
		{
			collection.put(availableChannel, 0);
		}
	}
	public void Increase(Channel c)
	{
		if(collection.containsKey(c))
		{
			int pre  = collection.remove(c);
			pre++;
			collection.put(c, pre);
		}
		else
			collection.put(c, 1);
	}
	
	public int getOccurance(Channel c)
	{
		return collection.get(c);
	}
	public Set<Entry<Channel, Integer>> entrySet()
	{
		return collection.entrySet();
	}
}
