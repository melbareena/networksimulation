package cAssignment;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import dataStructure.Channel;

/**
 * 
 * @author Mahdi
 * this orginal of this algorithm is belong to Aravind and Professor.
 */
public class OriginalSterategy extends AssigningSterategy
{
	
	public OriginalSterategy()
	{
		super.InitiateVriables();
	}

	@Override
	protected Channel getActualChannel(Map<Channel, Double> channelAffectSet)
	{
		Channel min = getMinAffectance(channelAffectSet);
		return min;
	}
	protected Channel getMinAffectance(Map<Channel, Double> channelAffectSet)
	{
		
		
		double affectanceValue = Collections.max(channelAffectSet.values());
		Channel min = null;
		for (Entry<Channel, Double> channelAff : channelAffectSet.entrySet())
		{
			if(channelAff.getValue() >= 0 && affectanceValue > channelAff.getValue())
			{
				affectanceValue  = channelAff.getValue();
				min = channelAff.getKey();
			}
		}
		return min;
	}

}
