package transConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;

import common.FileGenerator;

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
			//tConfUnit = calcDataRate(tConfUnit); // make sure data rates are correct.
			//tConfUnit = Enlarge(tConfUnit);			
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
	
	
	/*----------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------*/
	
	
	/** Create a list of transmission configurations, as short as possible.
	 * Based on 2 main steps :
	 * <ul>
	 * <li>The gateways step: Starts by creating a list of configurations patterns
	 * containing every link of every gateway.</li>
	 * <li>The remaining links step: Uses these patterns to add the other links of the
	 * network, and creates other configurations if necessary.</li>
	 * </ul>
	 * @see TransmissionConfiguration#gatewaysStep(List, int, boolean, HashSet, boolean)
	 * @see TransmissionConfiguration#remainingLinksStep(List, HashSet, boolean, List, int)
	 * @param downOverUpRatio the ratio for the number of downlinks, over the number
	 * of uplinks by configuration
	 * @param alternateOrder specify whether or not the order of selecting links by
	 * the amount of traffic should be alterned
	 * @param repeatLinksToRespectRatio specify whether or not links can be repeated in
	 * more than one configuration to respect the ratio
	 * @param enlargeByGateways specify whether or not the enlarge algortihm should focus
	 * on gateways links
	 * @return the list of transmission configurations created
	 */
	protected List<TCUnit> ConfiguringBenjamin(int downOverUpRatio, boolean alternateOrder,
			boolean repeatLinksToRespectRatio, boolean enlargeByGateways) {
		ConsiderLinks = new HashMap<>(); // Lc <- Nil;
		
		List<Vertex> gateways = new ArrayList<Vertex>(ApplicationSettingFacade.Gateway.getGateway().values());
		
		HashSet<Link> selectedLinksSet = new HashSet<Link>();
		
		List<TCUnit> patterns = gatewaysStep(gateways, downOverUpRatio, alternateOrder,
				selectedLinksSet, repeatLinksToRespectRatio);
		
		System.out.println("\n\n\nNumber of patterns: "+patterns.size()
				+"\n\n\n");
		
		List<TCUnit> finalList = remainingLinksStep(patterns, selectedLinksSet, enlargeByGateways,
				gateways, downOverUpRatio);
		
		return finalList;
	}

	/** Create a list of transmission configurations containing every link of every
	 * gateway in the <code>gateways</code> list, trying to respect the ratio
	 * <code>downOverUpRatio</code>.
	 * @param gateways the list of gateways
	 * @param downOverUpRatio the ratio for the number of downlinks, over the number
	 * of uplinks by configuration
	 * @param alternateOrder specify whether or not the order of selecting links by
	 * the amount of traffic should be alterned
	 * @param selectedLinksSet the set of links selected, in other words the links
	 * already in at least 1 configuration
	 * @param repeatLinksToRespectRatio specify whether or not links can be repeated in
	 * more than one configuration to respect the ratio
	 * @return the list of transmission configurations created
	 */
	protected List<TCUnit> gatewaysStep(List<Vertex> gateways, int downOverUpRatio,
			boolean alternateOrder, HashSet<Link> selectedLinksSet,
			boolean repeatLinksToRespectRatio) {
		List<TCUnit> listTCU = new ArrayList<TCUnit>();
		int downlinksNumber = 0;
		int uplinksNumber = 0;
		/* Get the number of gateway links */
		int numberOfGatewaysLink = 0;
		for(Vertex g : gateways) {
			numberOfGatewaysLink += TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Incoming).size();
			numberOfGatewaysLink += TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Outgoing).size();
		}
		
		/* */
		while(selectedLinksSet.size() < numberOfGatewaysLink) {
			TCUnit tcu = new TCUnit();
			/* For each gateway */
			for(Vertex g : gateways) {
				List<Link> downlinks = TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Outgoing);
				List<Link> uplinks = TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Incoming);
				
				boolean reverseOrder = false; 
				// Alternate the link sorting by traffic
				Set<Link> downlinksKeySet;
				if(reverseOrder) {
					downlinksKeySet = LinksTraffic.Sort().descendingKeySet();
					reverseOrder = false;
				} else {
					downlinksKeySet = LinksTraffic.Sort().keySet();
					reverseOrder = true && alternateOrder;
				}
				// Retain only the concerned downlinks from the set
				downlinksKeySet.retainAll(downlinks);
				// For each downlink
				int preSize = tcu.size();
				tcu = tryAddingLinks(downlinksKeySet, tcu, 1, false, selectedLinksSet);
				downlinksNumber += tcu.size() - preSize;
				/* END downlink loop */
				
				reverseOrder = false; 
				// Alternate the link sorting by traffic
				Set<Link> uplinksKeySet;
				if(reverseOrder) {
					uplinksKeySet = LinksTraffic.Sort().descendingKeySet();
					reverseOrder = false;
				} else {
					uplinksKeySet = LinksTraffic.Sort().keySet();
					reverseOrder = true && alternateOrder;
				}
				// Retain only the concerned uplinks from the set
				uplinksKeySet.retainAll(uplinks);
				preSize = tcu.size();
				tcu = tryAddingLinks(uplinksKeySet, tcu, 1, false, selectedLinksSet);
				uplinksNumber += tcu.size() - preSize;
				
				if((uplinksNumber > 0)
						&& ((downlinksNumber / uplinksNumber) < downOverUpRatio)) {
					int newRatio = downOverUpRatio - (downlinksNumber / uplinksNumber);
					tcu = tryAddingLinks(downlinksKeySet, tcu, newRatio, repeatLinksToRespectRatio, selectedLinksSet);
				}
			}
			/* END for each gateway */
			if(tcu.size() > 0) {
				tcu = calcDataRate(tcu);
				listTCU.add(tcu);
			}
			resetMARK();
		}
		return listTCU;
	}
	
	/** Try to add links from the <code>linksSet</code> to the configuration <code>tcu</code>.
	 * The <code>selectedLinksSet</code> is taken into account only if
	 * <code>evenIfAlreadySelected</code> is false, meaning that a link is added to 
	 * <code>tcu</code> only if it is not contained in <code>selectedLinksSet</code>.
	 * Otherwise, if <code>evenIfAlreadySelected</code> is true, a link can be added 
	 * unconditionnally (<i>except of course if the <code>checkAdd</i> function
	 * prevent it</em>).
	 * @see TransmissionConfiguration#checkAdd(Link, TCUnit)
	 * @param linksSet the set of links to try to add to the configuration
	 * @param tcu the configuration to try to add links to
	 * @param numberOfLinksToAdd the number of links to try to add
	 * @param evenIfAlreadySelected specify whether or not a link can be added even
	 * if it has already been added to another configuration
	 * @param selectedLinksSet the set containing the links already added to
	 * another configuration
	 * @return the transmission configuration tcu, modified or not
	 */
	private TCUnit tryAddingLinks(Set<Link> linksSet, TCUnit tcu, int numberOfLinksToAdd,
			boolean evenIfAlreadySelected, HashSet<Link> selectedLinksSet) {
		int numberOfLinksAdded = 0;
		boolean newLinkAdded = true;
		while(newLinkAdded && (numberOfLinksAdded < numberOfLinksToAdd)) {
			newLinkAdded = false;
			for(Link l : linksSet) {
				if(!selectedLinksSet.contains(l) || evenIfAlreadySelected) {
					TCUnit modifiedTC = checkAdd(l, tcu.Clone());
					// Check if the link can be added to the current TCU
					if(modifiedTC != null) {
						// Then add it
						tcu = modifiedTC;
						selectedLinksSet.add(l);
						numberOfLinksAdded++;
						newLinkAdded = true;
						if(numberOfLinksAdded == numberOfLinksToAdd) {
							return tcu;
						}
					}
				}
			}
		}
		return tcu;
	}
	
	/** Create a list of transmission configurations containing every link not included
	 * in <code>selectedLinksSet</code>, which means not contained in any configuration.
	 * The configurations are created first based on the given <code>patterns</code>,
	 * adding as many links as possible to these.Then, other configurations are created
	 * to involve the never selected links.
	 * This step uses the <code>enlarge</code> algorithm to extend the configurations created.
	 * Note: It is possible to use a variant of this algorithm, focusing on the <code>gateways</code>
	 * links, and trying to respect the ratio <code>downOverUpRatio</code>.
	 * @see TransmissionConfiguration#Enlarge(TCUnit)
	 * @see TransmissionConfiguration#enlargeByGateways(TCUnit, HashSet, List, int)
	 * @param patterns list of transmission configurations used as patterns
	 * @param selectedLinksSet the set of links selected, in other words the links
	 * already in at least 1 configuration
	 * @param enlargeByGateways specify whether or not the enlarge algortihm should focus
	 * on gateways links
	 * @param gateways the list of gateways
	 * @param downOverUpRatio the ratio for the number of downlinks, over the number
	 * of uplinks by configuration
	 * @return the list of transmission configurations created
	 */
	protected List<TCUnit> remainingLinksStep(List<TCUnit> patterns, HashSet<Link> selectedLinksSet,
			boolean enlargeByGateways, List<Vertex> gateways, int downOverUpRatio) {
		List<TCUnit> listTCU = new ArrayList<TCUnit>(patterns);
		// Try adding links using generated patterns
		boolean newLinkAdded = true;
		while((selectedLinksSet.size() < numberOfLinks) && newLinkAdded) {
			newLinkAdded = false;
			for(TCUnit tcu : listTCU) {
				int currentTCUIndex = listTCU.indexOf(tcu);
				for(Link l : LinksTraffic.Sort().keySet()) {
					if(!selectedLinksSet.contains(l)) {
						TCUnit modifiedTC = checkAdd(l, tcu.Clone());
						// Check if the link can be added to the current TCU
						if(modifiedTC != null) {
							// Then add it
							tcu = modifiedTC;
							selectedLinksSet.add(l);
							newLinkAdded = true;
						}
					}
				}
				tcu = calcDataRate(tcu);
				tcu = Enlarge(tcu);
				tcu = calcDataRate(tcu);
				if(listTCU.size() <= currentTCUIndex) {
					listTCU.add(currentTCUIndex, tcu);
				} else {
					listTCU.set(currentTCUIndex, tcu);
				}
				resetMARK();
			}
		}
		// Create other TCU if necessary, to add remaining links
		while(selectedLinksSet.size() < numberOfLinks) {
			TCUnit newTCU = new TCUnit();
			for(Link l : LinksTraffic.Sort().keySet()) {
				if(!selectedLinksSet.contains(l)) {
					TCUnit modifiedTC = checkAdd(l, newTCU.Clone());
					// Check if the link can be added to the current TCU
					if(modifiedTC != null) {
						// Then add it
						newTCU = modifiedTC;
						selectedLinksSet.add(l);
					}
				}
			}
			if(newTCU.size() > 0) {
				newTCU = calcDataRate(newTCU);
				if(enlargeByGateways) {
					newTCU = enlargeByGateways(newTCU, selectedLinksSet, gateways, downOverUpRatio);
				} else {
					newTCU = Enlarge(newTCU);
				}
				newTCU = calcDataRate(newTCU);
				listTCU.add(newTCU);
			}
			resetMARK();
		}
		
		return listTCU;
	}
	
	/** Try to extend the configuration <code>tcu</code> by adding as many gateways
	 * links as possible.
	 * Note: This is a variant of the <code>enlarge</code> algorithm.
	 * @see TransmissionConfiguration#Enlarge(TCUnit)
	 * @param tcu the transmission configuration to extend
	 * @param selectedLinksSet the set of links selected, in other words the links
	 * already in at least 1 configuration
	 * @param gateways the list of gateways
	 * @param downOverUpRatio the ratio for the number of downlinks, over the number
	 * of uplinks for the configuration <code>tcu</code>
	 * @return the extended configuration (or the same if extension what not successful)
	 */
	private TCUnit enlargeByGateways(TCUnit tcu, HashSet<Link> selectedLinksSet, 
			List<Vertex> gateways, int downOverUpRatio) {
		int downlinksNumber = 0;
		int uplinksNumber = 0;
		for(Vertex g : gateways) {
			List<Link> downlinks = TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Outgoing);
			List<Link> uplinks = TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Incoming);
			
			Set<Link> downlinksKeySet = LinksTraffic.Sort().descendingKeySet();
			// Retain only the concerned downlinks from the set
			downlinksKeySet.retainAll(downlinks);
			int preSize = tcu.size();
			tcu = tryAddingLinks(downlinksKeySet, tcu, 1, true, selectedLinksSet);
			downlinksNumber += tcu.size() - preSize;

			Set<Link> uplinksKeySet = LinksTraffic.Sort().descendingKeySet();
			// Retain only the concerned uplinks from the set
			uplinksKeySet.retainAll(uplinks);
			preSize = tcu.size();
			tcu = tryAddingLinks(uplinksKeySet, tcu, 1, true, selectedLinksSet);
			uplinksNumber += tcu.size() - preSize;
			
			if((uplinksNumber > 0)
				&& ((downlinksNumber / uplinksNumber) < downOverUpRatio)) {
				int newRatio = downOverUpRatio - (downlinksNumber / uplinksNumber);
				tcu = tryAddingLinks(downlinksKeySet, tcu, newRatio, true, selectedLinksSet);
			}
		}
		return tcu;
	}
	
	/*----------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------*/
	
	
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
					//System.out.println("\tSINR too low : "+sinr);
					add = false;
					break;
				}
			}
		} else {
			//System.out.println("\tRadio problem");
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
		} else if (add && (tPrime.getTCAP() <= tConfig.getTCAP())) {
			//System.out.println("\tTCAP too low");
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
		
		if(rTargetMark < rTarget && rSourceMark < rSource) {
			return true;
		}
		return false;
	}
	
	

	
	
	
}