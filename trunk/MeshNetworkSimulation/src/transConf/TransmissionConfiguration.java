package transConf;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;
import org.jblas.Solve;

import setting.ApplicationSettingFacade;
import sinr.SINR;
import trafficEstimating.TrafficEstimatingFacade;
import cAssignment.ChannelAssignmentFacade;
import common.FileGenerator;
import common.PrintConsole;
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
	LinkType forceGatewayLinks = LinkType.Outgoing;
	private LinkTrafficMap LinksTraffic = TrafficEstimatingFacade.getLinksTraffic();
	private final float BETA = ApplicationSettingFacade.SINR.getBeta();
	private int numberOfLinks = TrafficEstimatingFacade.getOptimalLinks().size(); 
	private Map<Link, Boolean> ConsiderLinks;
	
	private SINR _sinr = new SINR();
	protected TransmissionConfiguration() {
		InitiateVariable();
	}
	
	List<TCUnit> _TT = new ArrayList<>();
	protected List<TCUnit> originalConfiguring()
	{
		
		TCUnit tConfUnit;
		ConsiderLinks = new HashMap<>(); // Lc <- Nil;
		
	
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
						tConfUnit.putRate(l, setting.ApplicationSettingFacade.DataRate.getMax());
								
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
				TCUnit original = tConfUnit.Clone(); // copy the links which are added in first step
													 // these links are the originals ones which must be checked at least one of 
													 // them are in the updated TC after the phase 2 is ran.
				
				for(Vertex g : ApplicationSettingFacade.Gateway.getGateway().values())
				{
					
					//-------------------------PHASE 1----------------------------------------
					if(tConfUnit.getCounter_g(g, LinkType.Incoming) == 0)				
						tConfUnit = addIncomingLinks(tConfUnit, g);
					
					if(tConfUnit.getCounter_g(g, LinkType.Outgoing) == 0)
						tConfUnit = addOutgoingLinks(tConfUnit, g);
					//------------------------------------------------------------------------
						
					
					//-------------------------PHASE 2----------------------------------------
					if(tConfUnit.getCounter_g(g, LinkType.Incoming) == 0 )
						tConfUnit = exchangeIncoming(tConfUnit, original, g);
					//---------------------------------------------------------------------------
				} 
			}	
		
			tConfUnit = calcDataRate(tConfUnit); // make sure data rates are correct.	
			tConfUnit = powerControl(tConfUnit);
			
			
			_TT.add(tConfUnit);
			resetMARK();		
		}
		//*******************************************SETP 3****************************************************
		Enlarge();		
		FileGenerator.TransmissionConfige(_TT);
		FileGenerator.DataRate(_TT);
		return _TT;
	}

	private TCUnit exchangeIncoming(TCUnit tConfUnit, TCUnit original, Vertex g)
	{
		ArrayList<Triple<Link, Link, Double>> tripleLists = new ArrayList<>();
		Triple<Link,Link, Double> triple; // add,remove, sinr
		
		TCUnit copyTC = tConfUnit.Clone();
		

		
		for (Link link : TrafficEstimatingFacade.getOptimalLinks(g,LinkType.Incoming))
		{
			for(Link lprime : copyTC.getLinks())
			{
				List<Link> links = copyTC.getLinks();
				links.remove(lprime);
				links.add(link);
				double sinr = _sinr.calc(link, links);
					
				if(sinr >= BETA)
				{
					triple = new Triple<>(link, lprime, sinr);
					tripleLists.add(triple);
				}
			}
		}
		Link deletedLink = null;
		if(tripleLists.size() > 0 )
		{
			Triple<Link, Link, Double> maxTriple = maxmizing(tripleLists);
			copyTC.removeLink(maxTriple.getB());
			deletedLink = maxTriple.getB();
			copyTC.putRate(maxTriple.getA(), computeRate(maxTriple.getC()).getRate());
									
		
		}
		Set<Link> updatedLinks = new HashSet<Link>(copyTC.getLinks());
		Set<Link> originalLinks = new HashSet<Link>(original.getLinks());
		
		updatedLinks.retainAll(originalLinks);
		
		if(updatedLinks.size() > 0 ) // check at least one of the original links 
									 //(original link is the link which is already added by first phase) remains in 
									 // in transmission configuration.
		{	
			removeFromConsiderList(deletedLink);
			return copyTC;
		}
		return tConfUnit;
	}
	
	
	private void removeFromConsiderList(Link deletedLink)
	{
		for (TCUnit unit : _TT)
		{
			if(unit.containsKey(deletedLink))
				return;
		}
		ConsiderLinks.remove(deletedLink);
		
	}

	private Triple<Link, Link, Double> maxmizing(
			ArrayList<Triple<Link, Link, Double>> tripleLists)
	{
		double max = Double.MIN_VALUE;
		Triple<Link, Link, Double> maxTriple = null;
		for (Triple<Link, Link, Double> currentTriple : tripleLists)
		{
			if(max < currentTriple.getC().doubleValue())
			{
				max = currentTriple.getC().doubleValue();
				maxTriple = currentTriple;
			}
		}
		return maxTriple;
	}
	
	private TCUnit addIncomingLinks(TCUnit tConfUnit, Vertex g)
	{
		for (Link link : TrafficEstimatingFacade.getOptimalLinks(g,LinkType.Incoming))
		{							
			TCUnit modifiedTC = checkAdd(link, tConfUnit.Clone());
			if(modifiedTC != null)
			{
				tConfUnit = modifiedTC;
				break; // next g;
			}
				
		}
		return tConfUnit;
	}
	
	private TCUnit addOutgoingLinks(TCUnit tConfUnit, Vertex g)
	{
		for (Link link : TrafficEstimatingFacade.getOptimalLinks(g,LinkType.Outgoing))
		{							
			TCUnit modifiedTC = checkAdd(link, tConfUnit.Clone());
			if(modifiedTC != null)
			{
				tConfUnit = modifiedTC;
				break; // next g;
			}
				
		}
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
	 * @param priorityToOrthogonal specify whether or not orhtognoal links should be considered
	 * with priority over overlapping links
	 * @param repeatLinksToRespectRatio specify whether or not links can be repeated in
	 * more than one configuration to respect the ratio
	 * @param enlargeByGateways specify whether or not the enlarge algortihm should focus
	 * on gateways links
	 * @return the list of transmission configurations created
	 */
	protected List<TCUnit> patternBasedConfiguration(int downOverUpRatio, boolean priorityToOrthogonal,
			boolean repeatLinksToRespectRatio, boolean enlargeByGateways) {
		ConsiderLinks = new HashMap<>(); // Lc <- Nil;
		
		List<Vertex> gateways = new ArrayList<Vertex>(ApplicationSettingFacade.Gateway.getGateway().values());
		
		HashSet<Link> selectedLinksSet = new HashSet<Link>();
		
		List<TCUnit> patterns = gatewaysStep(gateways, downOverUpRatio, priorityToOrthogonal,
				selectedLinksSet, repeatLinksToRespectRatio);
		
		System.out.println("Number of patterns: "+patterns.size());
		
		List<TCUnit> finalList = remainingLinksStep(patterns, selectedLinksSet, enlargeByGateways,
				gateways, downOverUpRatio);
		
		FileGenerator.TransmissionConfige(finalList);
		FileGenerator.DataRate(finalList);
		return finalList;
	}

	/** Create a list of transmission configurations containing every link of every
	 * gateway in the <code>gateways</code> list, trying to respect the ratio
	 * <code>downOverUpRatio</code>.
	 * @param gateways the list of gateways
	 * @param downOverUpRatio the ratio for the number of downlinks, over the number
	 * of uplinks by configuration
	 * @param priorityToOrthogonal specify whether or not orhtognoal links should be considered
	 * with priority over overlapping links
	 * @param selectedLinksSet the set of links selected, in other words the links
	 * already in at least 1 configuration
	 * @param repeatLinksToRespectRatio specify whether or not links can be repeated in
	 * more than one configuration to respect the ratio
	 * @return the list of transmission configurations created
	 */
	protected List<TCUnit> gatewaysStep(List<Vertex> gateways, int downOverUpRatio,
			boolean priorityToOrthogonal, HashSet<Link> selectedLinksSet,
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
				
				LinkedHashSet<Link> downlinksKeySet = new LinkedHashSet<Link>(LinksTraffic.Sort().keySet());
				// Retain only the concerned downlinks from the set
				downlinksKeySet.retainAll(downlinks);
				// Sort by orthogonal channels
				if(priorityToOrthogonal) {
					downlinksKeySet = sortByOrthogonalChannel(downlinksKeySet);
				}
				// For each downlink
				int preSize = tcu.size();
				tcu = tryAddingLinks(downlinksKeySet, tcu, 1, false, selectedLinksSet);
				downlinksNumber += tcu.size() - preSize;
				/* END downlink loop */
				
				LinkedHashSet<Link> uplinksKeySet = new LinkedHashSet<Link>(LinksTraffic.Sort().keySet());
				// Retain only the concerned uplinks from the set
				uplinksKeySet.retainAll(uplinks);
				// Sort by orthogonal channels
				if(priorityToOrthogonal) {
					uplinksKeySet = sortByOrthogonalChannel(uplinksKeySet);
				}
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
	 * unconditionnally (<em>except of course if the <code>checkAdd</code> function
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
	private TCUnit tryAddingLinks(LinkedHashSet<Link> linksSet, TCUnit tcu, int numberOfLinksToAdd,
			boolean evenIfAlreadySelected, Set<Link> selectedLinksSet) {
		int numberOfLinksAdded = 0;
		boolean newLinkAdded = true;
		while(newLinkAdded && (numberOfLinksAdded < numberOfLinksToAdd)) {
			newLinkAdded = false;
			for(Link l : linksSet) {
				if((!selectedLinksSet.contains(l) || evenIfAlreadySelected)
						&& !tcu.containsKey(l)) {
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
	protected List<TCUnit> remainingLinksStep(List<TCUnit> patterns, Set<Link> selectedLinksSet,
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
	private TCUnit enlargeByGateways(TCUnit tcu, Set<Link> selectedLinksSet, 
			List<Vertex> gateways, int downOverUpRatio) {
		int downlinksNumber = 0;
		int uplinksNumber = 0;
		for(Vertex g : gateways) {
			List<Link> downlinks = TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Outgoing);
			List<Link> uplinks = TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Incoming);
			
			LinkedHashSet<Link> downlinksKeySet = new LinkedHashSet<Link>(LinksTraffic.Sort().keySet());
			// Retain only the concerned downlinks from the set
			downlinksKeySet.retainAll(downlinks);
			int preSize = tcu.size();
			tcu = tryAddingLinks(downlinksKeySet, tcu, 1, true, selectedLinksSet);
			downlinksNumber += tcu.size() - preSize;

			LinkedHashSet<Link> uplinksKeySet = new LinkedHashSet<Link>(LinksTraffic.Sort().keySet());
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
			double  sinr = _sinr.calc(l, links);
			DataRate dr = computeRate(sinr);
			tConfUnit.setSinrRate(l, dr.getRate(),sinr);
		}
		return tConfUnit;
	}
		
	private void calcDataRate()
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

	private void Enlarge()
	{
		List<TCUnit> updateTT = new ArrayList<TCUnit>();
		TCUnit updateTUnit = null;
		for (TCUnit tcUnit : _TT)
		{
			tcUnit = calcDataRate(tcUnit);
			calcOccupiedRadio(tcUnit);
			updateTUnit = this.Enlarge(tcUnit);
			updateTUnit = this.calcDataRate(updateTUnit);
			updateTT.add(updateTUnit);
			resetMARK();
		}
		_TT = updateTT;
		calcDataRate();
	}
	
	private void calcOccupiedRadio(TCUnit tcUnit)
	{
		for (Link l : tcUnit.getLinks())
		{
			setMark(l.getDestination());
			setMark(l.getSource());
		}
		
	}

	private TCUnit Enlarge(TCUnit tConfUnit)
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
	
	private TCUnit powerControl(TCUnit unit)
	{
		
		unit = calcDataRate(unit);
		
		List<Link> links = unit.getLinks();
		
		double[][] arr_D = new double[unit.size()][unit.size()];
		double[][] arr_G = new double[unit.size()][unit.size()];
		for(int i = 0 ; i < unit.size() ; i++)
			for(int j=0; j < unit.size() ; j++)
			{
				Link ell_i = links.get(i);
				Link ell_j = links.get(j);
				if(i==j)	
					arr_D[i][j] = unit.getSinr(ell_i); // get gamma
				else
				{
					double d =  (Math.pow(links.get(j).getCrossDistance(links.get(i)),-ApplicationSettingFacade.SINR.getAlpha()) /
							Math.pow(links.get(i).getDistance(), -ApplicationSettingFacade.SINR.getAlpha()));
					double IfactorValue = _sinr.getIFactorValue(ell_i, ell_j);
					d = d * IfactorValue;
					double rounded = (double) Math.round(d * 10000) / 10000;
					arr_G[i][j] = rounded;
				}
			}
		
		
		DoubleMatrix A = new DoubleMatrix(unit.size(), unit.size());
		DoubleMatrix D = new DoubleMatrix(arr_D);
		DoubleMatrix G = new DoubleMatrix(arr_G);		
		
		 D.mmuli(G,A);	
		ComplexDoubleMatrix cdm = Eigen.eigenvalues(A);
		 ComplexDouble[] cd = new ComplexDouble[unit.size()];
		  for (int i=0;i<unit.size();i++) cd[i]=cdm.get(i);
		  double perron_eigenvalue=Double.MIN_VALUE;
		  	  for (int i=0;i<unit.size();i++) {
			    if (cd[i].isReal() && cd[i].real()>perron_eigenvalue) perron_eigenvalue=cd[i].real();
		}
		  	  
		  	  if(perron_eigenvalue < 1)
		  	  {
		  		  double[][] h = new double[unit.size()][unit.size()];
		  		  for (int i=0;i<unit.size();i++)
					  for (int j=0;j<unit.size();j++) 
						  h[i][j]= i==j ? 1-A.get(i,j) : -1*A.get(i,j);
						  
				  DoubleMatrix H = new DoubleMatrix(h); //(I-A)	
				  
				  DoubleMatrix b = new DoubleMatrix(unit.size());
				  for (int i=0;i<unit.size();i++) 
					  b.put(i,0, ApplicationSettingFacade.SINR.getMue() /
							  Math.pow(links.get(i).getDistance(),-ApplicationSettingFacade.SINR.getAlpha()));
				  
				  
				  DoubleMatrix H_inv=Solve.solve(H,DoubleMatrix.eye(unit.size()));
				  DoubleMatrix P = H_inv.mmul(b);
				  double[] power = P.toArray();
				  
				  int i = 0;
				for (double e : power)
				{
					
					double power_watTOmWat = e * 1000;
					if(power_watTOmWat < ApplicationSettingFacade.SINR.getPower())
					{
						unit.setPower(links.get(i), power_watTOmWat) ;
						PrintConsole.print(links.get(i) + ": " +  power_watTOmWat + "mW");
					}
					else
						PrintConsole.print("exteed the max power:"+ links.get(i) + ": " + power_watTOmWat);
					i++;
				}
		  	  }
		  	  else
		  		  PrintConsole.print("Dead");
		
		
		return unit;
	}
	
	
	private LinkedHashSet<Link> sortByOrthogonalChannel(Set<Link> linkSet) {
		TreeSet<Link> result = new TreeSet<Link>(new Comparator<Link>() {
			@Override
			public int compare(Link l1, Link l2) {
				if(l1.compareTo(l2) == 0) {
					return 0;
				}
				LinkTrafficMap trMap = TrafficEstimatingFacade.getLinksTraffic();
				if(isOrthogonalChannel(l1)) {
					if(isOrthogonalChannel(l2)) {
						if(trMap.get(l1) > trMap.get(l2)) {
							return -1;
						} else {
							return 1;
						}
					} else {
						return -1;
					}
				} else {
					if(isOrthogonalChannel(l2)) {
						return 1;
					} else {
						if(trMap.get(l1) > trMap.get(l2)) {
							return -1;
						} else {
							return 1;
						}
					}
				}
			}
			
			private boolean isOrthogonalChannel(Link l) {
				int channel = ChannelAssignmentFacade.getChannels().get(l).getChannel();
				return channel == 1 || channel == 6 || channel == 11;
			}
		});
		result.addAll(linkSet);
		printLinkSetChannels(result);
		return new LinkedHashSet<Link>(result);
	}
	
	protected void printLinkSetChannels(Set<Link> linkSet) {
		int i = 0;
		for(Link l : linkSet) {
			System.out.println((i++)+": L"+l.getId()+"; "+ChannelAssignmentFacade.getChannels().get(l)+
					"; T"+TrafficEstimatingFacade.getLinksTraffic().get(l));
		}
		System.out.println("");
	}
	
}
