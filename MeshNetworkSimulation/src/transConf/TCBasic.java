package transConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;
import dataStructure.DataRate;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.LinkType;
import dataStructure.TCUnit;
import dataStructure.Vertex;

/**
 * 
 * @author Mahdi
 * All veriables which is needed for different Transmission configuration algorithms
 */


public abstract class TCBasic
{

	public static enum DeleteAction { True, NotNecessary, Impossible}; 
	protected Map<Integer, Vertex> nodes = ApplicationSettingFacade.Nodes.getNodes();
	protected Map<Vertex, Integer> MARK = new HashMap<Vertex, Integer>();
	protected LinkType forceGatewayLinks = LinkType.Outgoing;
	protected LinkTrafficMap LinksTraffic = TrafficEstimatingFacade.getLinksTraffic();
	protected final float BETA = ApplicationSettingFacade.SINR.getBeta();
	protected int numberOfLinks = TrafficEstimatingFacade.getOptimalLinks().size(); 
	protected Map<Link, Boolean> ConsiderLinks;
	protected List<TCUnit> _TT = new ArrayList<>();
	protected PowerControlUnit _powerUnit;
	protected SINR _sinr = new SINR();
	
	
	protected abstract DeleteAction removeFromConsiderList(Link deletedLink);
	
	protected TCUnit calcDataRate(TCUnit tConfUnit)
	{
		
		List<Link>  links;
		for (Link l : tConfUnit.getLinks())
		{
			links = tConfUnit.getLinks();
			links.remove(l);
			double  sinr = _sinr.calc(l, links);
			DataRate dr = computeRate(sinr);
			tConfUnit.setSinrRate(l, dr.getRate(),sinr);
		}
		return tConfUnit;
	}
	
	
	
	protected void calcOccupiedRadio(TCUnit tcUnit)
	{
		for (Link l : tcUnit.getLinks())
		{
			setMark(l.getDestination());
			setMark(l.getSource());
		}
		
	}
	protected  DataRate computeRate(double sinr)
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
	
	protected void setMark(Vertex v)
	{
		int pre = MARK.remove(v);
		pre++;
		MARK.put(v, pre);
	}

	protected void resetMARK()
	{
		
		for (Entry<Integer, Vertex> node : nodes.entrySet())
		{
			MARK.put(node.getValue(), 0);
		}
	}
	
	protected int getRadio(Vertex v) 
	{
		Map<Integer,Vertex> routers = ApplicationSettingFacade.Router.getRouter();
		Map<Integer,Vertex> gateways = ApplicationSettingFacade.Gateway.getGateway();
		
		if(routers.containsKey(v.getId()))
			return ApplicationSettingFacade.Router.getRadio();
		else if(gateways.containsKey(v.getId()))
			return ApplicationSettingFacade.Gateway.getRadio();
		
		return 0; // never happened;
	}
	
	protected boolean checkRadio(Link l)
	{
		int rSource = getRadio(l.getSource());
		int rTarget = getRadio(l.getDestination());
		
		int rSourceMark = MARK.get(l.getSource());
		int rTargetMark = MARK.get(l.getDestination());
		
		if(rTargetMark < rTarget && rSourceMark < rSource) {
			return true;
		}
		return false;
	}
	
	protected void calcDataRate()
	{
		List<TCUnit> updateTT = new ArrayList<TCUnit>();
		TCUnit updateTUnit = null;
		for (TCUnit tcUnit : _TT)
		{
			updateTUnit = this.calcDataRate(tcUnit);
			updateTT.add(updateTUnit);
		}
		_TT = updateTT;
		
	}
	TCUnit checkAdd(Link newLink, TCUnit tConfig)
	{
		boolean add = true;
		double sinr = 0;
		TCUnit tPrime = tConfig.Clone();
		
		if(this.checkRadio(newLink))
		{
			if(!balanceGatewayLinks(newLink,tConfig)) return null;
			tPrime.putRate(newLink, 0);
			tPrime.setTCAPZero();
			
		
			for (Link currentLink : tPrime.getLinks())
			{
				List<Link> linkSet = tPrime.getLinks();
				linkSet.remove(currentLink);
				
 				sinr = _sinr.calc(currentLink,linkSet );
				
				if(sinr >= BETA)
					tPrime.putRate(currentLink, computeRate(sinr).getRate());
				else
				{
					//System.out.println("\tSINR too low : "+sinr);
					add = false;
					break;
				}
			}
		
			if(add && tPrime.getTCAP() >= tConfig.getTCAP())
			{
				ConsiderLinks.put(newLink,true);
				setMark(newLink.getDestination());
				setMark(newLink.getSource());
				return tPrime;
			}
		} 
		return null;
	}

 	/**
 	 * this method check if the new link is incoming or outgoing link to a gateway , the gateway do not have any IN or OUT
 	 * link to the gateway
 	 * @param tConfig 
 	 * @param a new link which wants to add the TC.
 	 * @return can be added or not
 	 */
	private boolean balanceGatewayLinks(Link newLink, TCUnit tConfig)
	{
	
		for (Vertex gateway : 	ApplicationSettingFacade.Gateway.getGateway().values())
		{
			if(newLink.getDestination() != gateway && newLink.getSource() != gateway)
				continue;
			else 
			{
				if(newLink.getDestination() == gateway)
				{
					if(tConfig.getCounter_g(gateway, LinkType.Incoming) > 0)
						return false;
					else
						return true;
				}
				else if(newLink.getSource() == gateway)
				{
					if(tConfig.getCounter_g(gateway, LinkType.Outgoing) > 0)
						return false;
					else
						return true;
				}
			}
				
		}
		return true;
	}
	
	protected void Enlarge()
	{
		
		List<TCUnit> updateTT = new ArrayList<TCUnit>();
		TCUnit updateTUnit = null;
		for (TCUnit tcUnit : _TT)
		{
			tcUnit = calcDataRate(tcUnit);
			calcOccupiedRadio(tcUnit);
			updateTUnit = this.Enlarge(tcUnit);
			updateTUnit = this.calcDataRate(updateTUnit);
			if(ApplicationSettingFacade.PowerControl.isEnable())
				updateTUnit = _powerUnit.powerControl(updateTUnit);
			updateTT.add(updateTUnit);
			resetMARK();
		}
		_TT = updateTT;

	}
	
	

	protected TCUnit Enlarge(TCUnit tConfUnit)
	{
	
		Link[] links = TrafficEstimatingFacade.getSourceBuffers(0).sortByTraffic().keySet().toArray(new Link[0]);

		
		//List<Link> links = TrafficEstimatingFacade.getOptimalLinks();
		double sinr = 0;
		int numLinks = links.length;
		//Random rand = new Random();
		//int randNum = rand.nextInt(numLinks);
		int randNum = 0;
		
		for (int index = randNum; index <numLinks ; index++)
		{
			Link lprime = links[index];
			if(!tConfUnit.containsKey(lprime))
			{
				Vertex u = lprime.getDestination();
				Vertex v = lprime.getSource();

				if(checkRadio(lprime))
				{
					TCUnit T_prime = tConfUnit.Clone(); // clone new TC and add the new link to it
					T_prime.putRate(lprime, 0); // add new link which wants to add to the tc
					T_prime = calcDataRate(T_prime); // calculate tc data rate
					
					
					
					boolean add = true;
					for(Link l : tConfUnit.getLinks())
					{
						TCUnit T = tConfUnit.Clone();
							
						T.removeLink(l);
						T.putRate(lprime, 0);
						sinr = _sinr.calc(l, T.getLinks());
						T = calcDataRate(T);
						if(sinr  <= BETA || T_prime.getTCAP() < tConfUnit.getTCAP() )
							add = false;
					}
					if(add)
					{
						sinr = _sinr.calc(lprime, tConfUnit.getLinks());
						tConfUnit.putRate(lprime, computeRate(sinr).getRate());
						setMark(u);
						setMark(v);
						break;
					}
				}
			}
		}
		
		for(int index = 0 ; index < randNum; index++ )
		{
			Link lprime = links[index];
			if(!tConfUnit.containsKey(lprime))
			{
				Vertex u = lprime.getDestination();
				Vertex v = lprime.getSource();
				setMark(u);
				setMark(v);
				
				if(checkRadio(lprime))
				{
					for(Link l : tConfUnit.getLinks())
					{
						TCUnit T = tConfUnit.Clone();
						T.removeLink(l);
						T.putRate(lprime, 0);
						sinr = _sinr.calc(l, T.getLinks());
						T = calcDataRate(T);
						if(sinr  < BETA && T.getTCAP() > tConfUnit.getTCAP() )
						{				
							//System.err.println("TC #" + TCCounter + " add link " + lprime +" by Enlarg....");
							tConfUnit.putRate(lprime, computeRate(sinr).getRate());
							setMark(u);
							setMark(v);
							break;
							
						}				
					}
				}
			}
		}
	
		return tConfUnit;
	}
}
