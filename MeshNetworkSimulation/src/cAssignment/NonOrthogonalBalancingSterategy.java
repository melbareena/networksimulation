package cAssignment;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;

import dataStructure.Channel;

public class NonOrthogonalBalancingSterategy extends AssigningSterategy
{
	public NonOrthogonalBalancingSterategy()
	{
		super.InitiateVriables();
	}
	@Override
	protected Channel getActualChannel(Map<Channel, Double> channelAffectSet)
	{
		Channel min = getMinAffectance(channelAffectSet);
		min = nonOrthogonalBalancing(min);
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
	protected Channel nonOrthogonalBalancing(Channel min)
	{
		if(min.getChannel() == 2) 
		{
			int occ2 = ChannelOccurance.getOccurance(min);
			
			int occ10 = ChannelOccurance.getOccurance(ApplicationSettingFacade.Channel.getChannel().get(9));
			
			if(occ2 < occ10)
				return min;
			return ApplicationSettingFacade.Channel.getChannel().get(9);

		}
		if(min.getChannel() == 10) 
		{
			int occ10 = ChannelOccurance.getOccurance(min);
			
			int occ2 = ChannelOccurance.getOccurance(ApplicationSettingFacade.Channel.getChannel().get(1));
			
			if(occ10 < occ2)
				return min;
			return ApplicationSettingFacade.Channel.getChannel().get(1);

		}
		if(min.getChannel() == 3) 
		{
			int occ3 = ChannelOccurance.getOccurance(min);
			
			int occ9 = ChannelOccurance.getOccurance(ApplicationSettingFacade.Channel.getChannel().get(8));
			
			if(occ3 < occ9)
				return min;
			return ApplicationSettingFacade.Channel.getChannel().get(8);

		}
		if(min.getChannel() == 9) 
		{
			int occ9 = ChannelOccurance.getOccurance(min);
			
			int occ3 = ChannelOccurance.getOccurance(ApplicationSettingFacade.Channel.getChannel().get(2));
			
			if(occ9 < occ3)
				return min;
			return ApplicationSettingFacade.Channel.getChannel().get(2);

		}
		if(min.getChannel() == 4) 
		{
			int occ4 = ChannelOccurance.getOccurance(min);
			
			int occ8 = ChannelOccurance.getOccurance(ApplicationSettingFacade.Channel.getChannel().get(7));
			
			if(occ4 < occ8)
				return min;
			return ApplicationSettingFacade.Channel.getChannel().get(7);

		}
		if(min.getChannel() == 8) 
		{
			int occ8 = ChannelOccurance.getOccurance(min);
			
			int occ4 = ChannelOccurance.getOccurance(ApplicationSettingFacade.Channel.getChannel().get(3));
			
			if(occ8 < occ4)
				return min;
			return ApplicationSettingFacade.Channel.getChannel().get(3);

		}
		if(min.getChannel() == 5) 
		{
			int occ5 = ChannelOccurance.getOccurance(min);
			
			int occ7 = ChannelOccurance.getOccurance(ApplicationSettingFacade.Channel.getChannel().get(6));
			
			if(occ5 < occ7)
				return min;
			return ApplicationSettingFacade.Channel.getChannel().get(6);

		}
		if(min.getChannel() == 7) 
		{
			int occ7 = ChannelOccurance.getOccurance(min);
			
			int occ5 = ChannelOccurance.getOccurance(ApplicationSettingFacade.Channel.getChannel().get(4));
			
			if(occ7 < occ5)
				return min;
			return ApplicationSettingFacade.Channel.getChannel().get(4);

		}
		return min;
		
	}


}
