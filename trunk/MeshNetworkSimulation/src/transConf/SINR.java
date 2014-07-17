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
	private final IFactorMap iFactor = ApplicationSettingFacade.IFactor.getIFactorMap();
	 
	
	
	public static double calc(Link l , List<Link> L )
	{
		if(self == null)  self = new SINR();
		double i_l_lprime = self.I_l_lprime(l, L);
		double dis = self.getDistance(l);
		double Sinr  = (self.POWER * Math.pow(dis, - (self.ALPHA) )) / (self.MUE + i_l_lprime) ;	
		return Sinr;
		
	}
	
	
	public double getCrossDistance(Link l1, Link l2)
	{
		if(l1.getSource().equals(l2.getDestination()) ||
				l1.getSource().equals(l2.getSource()) ||
				l1.getDestination().equals(l2.getDestination()) ||
				l1.getDestination().equals(l2.getSource()))
				return 0.0001;
		return nodesDistances.get(l1.getSource()).get(l2.getDestination());
	}
	public double getDistance(Link l)
	{
		return this.nodesDistances.get(l.getSource()).get(l.getDestination());
	}
	
	private double I_l_lprime(Link l , List<Link> L)
	{
		LinksChannelMap channels = ChannelAssignmentFacade.getChannels();
		double i_l_lprime = 0;
		for (Link lprime : L)
		{
			int diffCH = Math.abs(channels.get(l).getChannel() - channels.get(lprime).getChannel() );
			double ifac = iFactor.get(diffCH);
			double crossDis = this.getCrossDistance(lprime, l);
			i_l_lprime += POWER * Math.pow(crossDis , (-ALPHA )) * ifac;
		}
		
		return i_l_lprime;
	}
}
