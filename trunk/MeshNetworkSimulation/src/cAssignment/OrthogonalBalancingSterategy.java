package cAssignment;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;

import dataStructure.Channel;

public class OrthogonalBalancingSterategy extends AssigningSterategy
{

	public OrthogonalBalancingSterategy()
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
			if(channelAff.getValue() >= 0 && affectanceValue > channelAff.getValue())
			{
				affectanceValue  = channelAff.getValue();
				min = channelAff.getKey();
			}
		}
		if(min.getChannel() == 1 || min.getChannel() == 6 || min.getChannel() == 11)
			min = ortogonalChannelBalancing(min);
		return min;
	}
	protected Channel ortogonalChannelBalancing(Channel min)
	{
		Channel balancedChannel = min;
		int OccurranceChannelMin = ChannelOccurance.getOccurance(min);
		for (Channel availableChannel : ApplicationSettingFacade.Channel.getChannel())
		{
			if(availableChannel.getChannel() == 1 || availableChannel.getChannel() == 6 || availableChannel.getChannel() == 11)
				if(ChannelOccurance.getOccurance(availableChannel) < OccurranceChannelMin)
				{
					balancedChannel = availableChannel;
					OccurranceChannelMin = ChannelOccurance.getOccurance(availableChannel);
				}
		}
		return balancedChannel;
	}

}
