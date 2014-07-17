package cAssignment;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.Map.Entry;

import dataStructure.Channel;

public class SelectMinRandomlySterategy extends AssigningSterategy
{

	public SelectMinRandomlySterategy()
	{
		super.InitiateVriables();
	}
	@Override
	protected Channel getActualChannel(Map<Channel, Double> channelAffectSet)
	{
		int inMin = 0;
		double affectanceValue = Collections.max(channelAffectSet.values());
		Channel min = null;
		for (Entry<Channel, Double> channelAff : channelAffectSet.entrySet())
		{
			if(channelAff.getValue() >= 0 && affectanceValue >= channelAff.getValue())
			{
				inMin++;
				affectanceValue  = channelAff.getValue();
				min = channelAff.getKey();
			}
		}
		if(inMin > 1 )
			min = getRandom(channelAffectSet, affectanceValue);
		return min;
	}
	private Channel getRandom(Map<Channel, Double> channelAffectSet, double affectanceValue)
	{
		Vector<Channel> minChannel = new Vector<>();
		int index = 0;
		for (Entry<Channel, Double> current : channelAffectSet.entrySet())
		{
			if(current.getValue() == affectanceValue)
			{
				index++;
				minChannel.add(current.getKey());
			}
		}
		Random rand = new Random();
		return minChannel.get(rand.nextInt(index));
		
	}

}
