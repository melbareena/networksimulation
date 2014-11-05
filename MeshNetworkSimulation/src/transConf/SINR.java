package transConf;

import java.util.List;
import java.util.Map;

import cAssignment.ChannelAssignmentFacade;

import setting.ApplicationSettingFacade;
import topology.TopologyFacade;
import dataStructure.IFactorMap;
import dataStructure.Link;
import dataStructure.LinksChannelMap;
import dataStructure.Vertex;

public class SINR
{
	
	private static SINR self;
	private Map<Vertex,Map<Vertex,Double>> nodesDistances = TopologyFacade.getDistanceForEachNode();
	private final float ALPHA = ApplicationSettingFacade.SINR.getAlpha();
	private final double MUE = ApplicationSettingFacade.SINR.getMue();
	private final float POWER = ApplicationSettingFacade.SINR.getPower();
	private static final IFactorMap iFactor = ApplicationSettingFacade.IFactor.getIFactorMap();
	private static final LinksChannelMap channels = ChannelAssignmentFacade.getChannels();
	
	
	public static double calc(Link l , List<Link> L )
	{
		if(self == null)  self = new SINR();
		double i_l_lprime = self.I_l_lprime(l, L);
		double dis = l.getDistance();
		double Sinr  = (self.POWER * Math.pow(dis, - (self.ALPHA) )) / (self.MUE + i_l_lprime) ;	
		return Sinr;
		
	}
	
	
	private double getCrossDistance(Link l, Link currentLink)
	{
		if (l.getSource().equals(currentLink.getDestination())) return  0.001;
			return l.getCrossDistance(currentLink);
	}
	
	private double I_l_lprime(Link l , List<Link> L)
	{
		
		double i_l_lprime = 0;
		for (Link lprime : L)
		{
			double ifac = SINR.getIFactorValue(l, lprime);
			double crossDis = this.getCrossDistance(lprime, l);
			i_l_lprime += POWER * Math.pow(crossDis , (-ALPHA )) * ifac;
		}
		
		return i_l_lprime;
	}
	
	
	public static double getIFactorValue(Link l_i, Link l_j)
	{
		int diffCH = Math.abs(channels.get(l_i).getChannel() - channels.get(l_j).getChannel() );
		double ifac = iFactor.get(diffCH);
		return ifac;
	}
}
