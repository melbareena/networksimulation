package cAssignment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;


import dataStructure.Channel;
import dataStructure.Link;

public class SINRSterategy extends AssigningSterategy
{
	
	
	public SINRSterategy()
	{
		super.InitiateVriables();
	}
	
	@Override
	protected Channel calc(List<Link> linkSet, Link currentLink)
	{
		
	
		Map<Channel,Double> channelAffectSet = new TreeMap<Channel, Double>();
		double i_l_lprime = 0;
		for(Channel chan : availableChannels)
		{
			i_l_lprime = 0;
			int currentChannel = chan.getChannel();
			for(Link l : linkSet)
			{
				int chn_sep = Math.abs(currentChannel - linksChannel.get(l).getChannel() );
 				double overlap = iFactor.get(chn_sep);
				double cross_distance = getCrossDistance(l,currentLink);			
				i_l_lprime += POWER * Math.pow(cross_distance, (-ALPHA)) * overlap ; 
			}
			double distance = getDistance(currentLink);
			double sinr = (POWER * Math.pow(distance, -(ALPHA) )) / (MUE + i_l_lprime);
			channelAffectSet.put(chan,sinr);
		}
	
		Channel actualChannel =  getActualChannel(channelAffectSet);
		return actualChannel;
		
	}
	
	
	@Override
	protected Channel getActualChannel(Map<Channel, Double> channelAffectSet)
	{
		Channel max = getMaxSINR(channelAffectSet);
		return max;
	}
	protected Channel getMaxSINR(Map<Channel, Double> channelAffectSet)
	{
		
		
		double MaxValue = Collections.max(channelAffectSet.values());
		Channel max = null;
		for (Entry<Channel, Double> channelSINRs : channelAffectSet.entrySet())
		{
			if(MaxValue == channelSINRs.getValue())
			{
				MaxValue  = channelSINRs.getValue();
				max = channelSINRs.getKey();
			}
		}
		return max;
	}
}
