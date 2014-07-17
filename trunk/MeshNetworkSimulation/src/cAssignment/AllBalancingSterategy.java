package cAssignment;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import dataStructure.Channel;

public class AllBalancingSterategy extends AssigningSterategy
{

	public AllBalancingSterategy()
	{
		super.InitiateVriables();
	}
	
	
	@Override
	protected Channel getActualChannel(Map<Channel, Double> channelAffectSet)
	{
		double affectanceValue = Collections.max(channelAffectSet.values());
		Channel min = null;
		for (Entry<Channel, Double> channelAff : channelAffectSet.entrySet())
		{
			if(channelAff.getValue() >= 0 && affectanceValue >= channelAff.getValue())
			{
				affectanceValue  = channelAff.getValue();
				min = channelAff.getKey();
			}
		}
		if(min.getChannel() == 1 || min.getChannel() == 6 || min.getChannel() == 11)
			min = ortogonalChannelBalancing(min);
		else
			min = nonOrthogonalBalancing(min);
		return min;
	}


	private Channel nonOrthogonalBalancing(Channel min)
	{
		NonOrthogonalBalancingSterategy onb = new NonOrthogonalBalancingSterategy();
		return onb.nonOrthogonalBalancing(min);
	}


	private Channel ortogonalChannelBalancing(Channel min)
	{
		OrthogonalBalancingSterategy ob = new OrthogonalBalancingSterategy();
		return ob.ortogonalChannelBalancing(min);
	}

}
