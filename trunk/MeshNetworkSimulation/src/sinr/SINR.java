package sinr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cAssignment.ChannelAssignmentFacade;
import setting.ApplicationSettingFacade;
import dataStructure.DataRate;
import dataStructure.IFactorMap;
import dataStructure.Link;
import dataStructure.LinksChannelMap;
import dataStructure.TCUnit;

public class SINR
{
	

	private final float ALPHA = ApplicationSettingFacade.SINR.getAlpha();
	private final double MUE = ApplicationSettingFacade.SINR.getMue();
	private final float POWER = ApplicationSettingFacade.SINR.getPower();
	private static final IFactorMap iFactor = ApplicationSettingFacade.IFactor.getIFactorMap();
	private final LinksChannelMap channels = ChannelAssignmentFacade.getChannels();
	
	/**
	 * calculate the SINR with maximum power
	 * @param l: the current link which want to calculate the SINR
	 * @param L: the set of links which want to calculate the interference of this set on the current link 
	 * @return
	 */
	public double calc(Link l , List<Link> L )
	{

		double i_l_lprime = I_l_lprime(l, L);
		double dis = l.getDistance();
		double Sinr  = (POWER * Math.pow(dis, - (ALPHA) )) / (MUE + i_l_lprime) ;	
		return Sinr;
		
	}
	
	/**
	 *  calculate the SINR with calculated power
	 * @param l: the current link which want to calculate the SINR
	 * @param L: the set of links which want to calculate the interference of this set on the current link 
	 * @param power : the value of the power of the current link @see PowerControlUnit class
	 * @return
	 */
	public double calc(Link l , List<Link> L, double power )
	{
		double i_l_lprime = I_l_lprime(l, L,power);
		double dis = l.getDistance();
		double Sinr  = (power * Math.pow(dis, - (ALPHA) )) / (MUE + i_l_lprime) ;	
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
			double ifac = getIFactorValue(l, lprime);
			double crossDis = this.getCrossDistance(lprime, l);
			i_l_lprime += POWER * Math.pow(crossDis , (-ALPHA )) * ifac;
		}
		
		return i_l_lprime;
	}
	private double I_l_lprime(Link l , List<Link> L, double power)
	{
		
		double i_l_lprime = 0;
		for (Link lprime : L)
		{
			double ifac = getIFactorValue(l, lprime);
			double crossDis = this.getCrossDistance(lprime, l);
			i_l_lprime += power * Math.pow(crossDis , (-ALPHA )) * ifac;
		}
		
		return i_l_lprime;
	} 
	
	public  double getIFactorValue(Link l_i, Link l_j)
	{
		int diffCH = Math.abs(channels.get(l_i).getChannel() - channels.get(l_j).getChannel() );
		double ifac = iFactor.get(diffCH);
		return ifac;
	}
	public  DataRate calcDataRate(double sinr)
	{
		List<DataRate> dataRates = ApplicationSettingFacade.DataRate.getDataRate();
		DataRate result = dataRates.get(0);
		for (DataRate dataRate : dataRates)
		{
			if(sinr > dataRate.getSINR())
				result = dataRate;
		}
		return result;
	}
	public TCUnit calcDataRate(TCUnit tConfUnit)
	{
		// when a TC is locked means the power control set data rate; hence, the data rate should not be changed 
		if(tConfUnit.isLock()) return tConfUnit;
		
		List<Link>  links;
		for (Link l : tConfUnit.getLinks())
		{
			links = tConfUnit.getLinks();
			links.remove(l);
			double  sinr = calc(l, links);
			DataRate dr = calcDataRate(sinr);
			tConfUnit.setSinrRate(l, dr.getRate(),sinr);
		}
		return tConfUnit;
	}

	public Map<Link, Integer> calcDataRate(TCUnit tConfUnit, Map<Link, Double> powerCollection)
	{
		Map<Link,Integer> rateMap = new HashMap<Link, Integer>();
		List<Link>  links;
		for (Link l : tConfUnit.getLinks())
		{
			links = tConfUnit.getLinks();
			links.remove(l);
			double  sinr = 0;
			if(powerCollection.containsKey(l))
				 sinr = calc(l, links,powerCollection.get(l));
			else
				sinr = calc(l, links);
			
			tConfUnit.setSinrRate(l, calcDataRate(sinr).getRate(), sinr);
			rateMap.put(l, calcDataRate(sinr).getRate());
		}
		return rateMap;
	}
	

}
