package transConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import common.FileGenerator;
import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;
import dataStructure.DataRate;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.LinkType;
import dataStructure.TCUnit;
import dataStructure.Triple;
import dataStructure.Vertex;

public class TransmissionConfiguration
{
	Map<Integer, Vertex> nodes = ApplicationSettingFacade.Nodes.getNodes();
	private Map<Vertex, Integer> MARK;
	LinkType forceGatewayLinks = LinkType.Incoming;
	private LinkTrafficMap LinksTraffic = TrafficEstimatingFacade.getLinksTraffic();
	private final float BETA = ApplicationSettingFacade.SINR.getBeta();
	private int numberOfLinks = TrafficEstimatingFacade.getOptimalLinks().size(); 
	private Map<Link, Boolean> ConsiderLinks;
	
	protected TransmissionConfiguration()
	{
		InitiateVariable();
	}
		
	protected List<TCUnit> Configuring()
	{
		
		TCUnit tConfUnit;
		ConsiderLinks = new HashMap<>(); // Lc <- Nil;
		List<TCUnit> TT = new ArrayList<>();
	
		while(ConsiderLinks.size() < numberOfLinks)
		{	
			
			//**************************  SETP ONE   ************************************
			tConfUnit = new TCUnit();
			for (Entry<Link, Float> links : LinksTraffic.Sort().entrySet())
			{
			
				Link l = links.getKey();
				if(!ConsiderLinks.containsKey(l))
				{
					if(tConfUnit.size() == 0 )
					{
						setMark(l.getDestination());
						setMark(l.getSource());
						tConfUnit.put(l, setting.ApplicationSettingFacade.DataRate.getMax());
						
						Vertex isGatway = isEndpointsGateway(l);
						
						if(isGatway != null)
							tConfUnit.addLinktoGatway(isGatway, l);
						
						ConsiderLinks.put(l,true);
					}
					else 
					{
						TCUnit modifiedTC = checkAdd(l, tConfUnit.Clone());
						if(modifiedTC != null)
							tConfUnit = modifiedTC;
					}
				}			
			}
			//*******************************************************************************
			
			//*****STEP TWO**************************************************************************
			if(tConfUnit.size() > 0)
			{
				//-------------------------PHASE 1---------------------------------------
				
				for(Vertex g : ApplicationSettingFacade.Gateway.getGateway().values())
				{
					if(tConfUnit.getCounter_g(g) == 0)
					{
						tConfUnit = addGatewayLinks(tConfUnit, g);
					}
					System.out.println(tConfUnit.getCounter_g(g));
					if(tConfUnit.getCounter_g(g) == 0)
					{
						tConfUnit = exchangeLinks(tConfUnit, g);
					}
					System.out.println(tConfUnit.getCounter_g(g));
				} 
			}	
		
			
			//*******************************************SETP 3****************************************************
			tConfUnit = calcDataRate(tConfUnit); // make sure data rates are correct.
			tConfUnit = Enlarge(tConfUnit);			
			//*******************************************SETP 4****************************************************
			tConfUnit = calcDataRate(tConfUnit);	
			//*****************************************************************************************************		
			TT.add(tConfUnit);
			resetMARK();
		}	
		
		
		FileGenerator.TransmissionConfige(TT);
		FileGenerator.DataRate(TT);
		return TT;
	}

	private TCUnit addGatewayLinks(TCUnit tConfUnit, Vertex g)
	{
		for (Link link : TrafficEstimatingFacade.getOptimalLinks(g,forceGatewayLinks))
		{							
			TCUnit modifiedTC = checkAdd(link, tConfUnit.Clone());
			if(modifiedTC != null)
			{
				tConfUnit = modifiedTC;
				//System.out.println("Step 2..... Phase 1");
				break; // next g;
			}
				
		}
		return tConfUnit;
	}

	private TCUnit exchangeLinks(TCUnit tConfUnit, Vertex g)
	{
		
			ArrayList<Triple<Link, Link, Double>> tripleLists = new ArrayList<>();
			Triple<Link,Link, Double> triple; // add,remove, sinr
			//boolean small_sinr = true;
			//while(small_sinr)
			//{
				for (Link link : TrafficEstimatingFacade.getOptimalLinks(g,forceGatewayLinks))
				{
					for(Link lprime : tConfUnit.getLinks())
					{
						List<Link> links = tConfUnit.getLinks();
						links.remove(lprime);
						links.add(link);
						double sinr = SINR.calc(lprime, links);
							
						if(sinr >= BETA)
						{
							triple = new Triple<>(link, lprime, sinr);
							tripleLists.add(triple);
						}
					}
				}
				if(tripleLists.size() > 0)
				{
					double max = 0d;
					Triple<Link, Link, Double> maxTriple = null;
					for (Triple<Link, Link, Double> currentTriple : tripleLists)
					{
						if(max < currentTriple.getC().doubleValue())
						{
							max = currentTriple.getC().doubleValue();
							maxTriple = currentTriple;
						}
					}
					tConfUnit.removeLink(maxTriple.getB());
					ConsiderLinks.remove(maxTriple.getB());
					tConfUnit.put(maxTriple.getA(), computeRate(maxTriple.getC()).getRate());
					Vertex isGatway = isEndpointsGateway(maxTriple.getA());
					if(isGatway != null)
						tConfUnit.addLinktoGatway(g, maxTriple.getA());
											
					System.out.println("EXCHANGEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
					
				//	small_sinr = false;
				}
			//}//end of while
			
			return tConfUnit;
		
	}
	private TCUnit checkAdd(Link newLink, TCUnit tConfig)
	{
		boolean add = true;
		double sinr = 0;
		TCUnit tPrime = tConfig.Clone();

		if(this.checkRadio(newLink))
		{
			
			
			/*for (Entry<Link, Integer> linkRate : tConfig.entrySet())
			{
				tPrime.put(linkRate.getKey(), 0);
			}*/
			tPrime.put(newLink, 0);
			tPrime.setTCAPZero();
			
		
			for (Link currentLink : tPrime.getLinks())
			{
				List<Link> linkSet = tPrime.getLinks();
				linkSet.remove(currentLink);
				
 				sinr = SINR.calc(currentLink,linkSet );
				
				if(sinr >= BETA)
					tPrime.put(currentLink, computeRate(sinr).getRate());
				else
				{
					add = false;
					break;
				}
			}
		}
		if(add && tPrime.getTCAP() > tConfig.getTCAP())
		{
			ConsiderLinks.put(newLink,true);
			setMark(newLink.getDestination());
			setMark(newLink.getSource());
			//this.tConfUnit.put(newLink, newConfUnit.getRate(newLink));
			tPrime.setGatewayLink(tConfig.getGatewayLink());
			Vertex isGatway = isEndpointsGateway(newLink);
			if(isGatway != null)
				tPrime.addLinktoGatway(isGatway, newLink);
			return tPrime;
		}
		
		return null;
	}
	
	/*
	 * calculate all links' data rate in a transfer configuration 
	 */
	private TCUnit calcDataRate(TCUnit tConfUnit)
	{
		
		List<Link>  links;
		for (Link l : tConfUnit.getLinks())
		{
			links = tConfUnit.getLinks();
			links.remove(l);
			double  sinr = SINR.calc(l, links);
			DataRate dr = computeRate(sinr);
			tConfUnit.setDataRate(l, dr.getRate());
		}
		return tConfUnit;
	}


	
	
	private TCUnit Enlarge(TCUnit tConfUnit)
	{
	
		Link[] links = TrafficEstimatingFacade.getSourceBuffers().sort().keySet().toArray(new Link[0]);

		
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
					T_prime.put(lprime, 0); // add new link which wants to add to the tc
					T_prime = calcDataRate(T_prime); // calculate tc data rate
					
					
					
					boolean add = true;
					for(Link l : tConfUnit.getLinks())
					{
						TCUnit T = tConfUnit.Clone();
							
						T.removeLink(l);
						T.put(lprime, 0);
						sinr = SINR.calc(l, T.getLinks());
						T = calcDataRate(T);
						if(sinr  <= BETA || T_prime.getTCAP() < tConfUnit.getTCAP() )
							add = false;
					}
					if(add)
					{
						sinr = SINR.calc(lprime, tConfUnit.getLinks());
						tConfUnit.put(lprime, computeRate(sinr).getRate());
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
						T.put(lprime, 0);
						sinr = SINR.calc(l, T.getLinks());
						T = calcDataRate(T);
						if(sinr  < BETA && T.getTCAP() > tConfUnit.getTCAP() )
						{				
							//System.err.println("TC #" + TCCounter + " add link " + lprime +" by Enlarg....");
							tConfUnit.put(lprime, computeRate(sinr).getRate());
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

	
	private Vertex isEndpointsGateway(Link l)
	{
		
		if(forceGatewayLinks == LinkType.Outgoing && ApplicationSettingFacade.Gateway.isGateway(l.getSource()))
			return l.getSource();
		if(forceGatewayLinks == LinkType.Incoming && ApplicationSettingFacade.Gateway.isGateway(l.getDestination()))
			return l.getDestination();
		return null;
	}

	private DataRate computeRate(double sinr)
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

	private void setMark(Vertex v)
	{
		int pre = MARK.remove(v);
		pre++;
		MARK.put(v, pre);
	}
	private void InitiateVariable()
	{
		MARK = new HashMap<Vertex, Integer>();
		resetMARK();
	}

	private void resetMARK()
	{
		
		for (Entry<Integer, Vertex> node : nodes.entrySet())
		{
			MARK.put(node.getValue(), 0);
		}
	}
	
	private int getRadio(Vertex v) 
	{
		Map<Integer,Vertex> routers = ApplicationSettingFacade.Router.getRouter();
		Map<Integer,Vertex> gateways = ApplicationSettingFacade.Gateway.getGateway();
		
		if(routers.containsKey(v.getId()))
			return ApplicationSettingFacade.Router.getRadio();
		else if(gateways.containsKey(v.getId()))
			return ApplicationSettingFacade.Gateway.getRadio();
		
		return 0; // never happened;
	}
	
	private boolean checkRadio(Link l)
	{
		int rSource = getRadio(l.getSource());
		int rTarget = getRadio(l.getDestination());
		
		int rSourceMark = MARK.get(l.getSource());
		int rTargetMark = MARK.get(l.getDestination());
		
		if(rTargetMark < rTarget && rSourceMark < rSource)
			return true;
		return false;
	}
	
	

	
	
	
}
