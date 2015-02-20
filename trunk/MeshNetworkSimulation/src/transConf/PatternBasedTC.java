
package transConf;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


import setting.ApplicationSettingFacade;
import sinr.SINR;
import trafficEstimating.TrafficEstimatingFacade;
import common.FileGenerator;
import dataStructure.Link;
import dataStructure.LinkType;
import dataStructure.TCUnit;
import dataStructure.Vertex;

/**
 * 
 * @author Benjamin
 *
 */
public class PatternBasedTC extends TCBasic
{

	protected PatternBasedTC()
	{
		super._sinr = new SINR();
		super.LinksTraffic = TrafficEstimatingFacade.getLinksTraffic();
		super._powerUnit = new PowerControlUnit(this);
		resetMARK();
	}
	
	protected HashSet<Link> _selectedLinksSet = new HashSet<Link>();
	protected List<TCUnit> _patterns;
	/** Create a list of transmission configurations, as short as possible.
	 * Based on 2 main steps :
	 * <ul>
	 * <li>The gateways step: Starts by creating a list of configurations patterns
	 * containing every link of every gateway.</li>
	 * <li>The remaining links step: Uses these patterns to add the other links of the
	 * network, and creates other configurations if necessary.</li>
	 * </ul>
	 * @see GreedyTC#gatewaysStep(List, int, boolean, HashSet, boolean)
	 * @see GreedyTC#remainingLinksStep(List, HashSet, boolean, List, int)
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
	protected List<TCUnit> patternBasedConfiguration(int downOverUpRatio) 
	{
		ConsiderLinks = new HashMap<>(); // Lc <- Nil;
		
		List<Vertex> gateways = new ArrayList<Vertex>(ApplicationSettingFacade.Gateway.getGateway().values());
		
		_patterns = gatewaysStep(gateways, downOverUpRatio, _selectedLinksSet);
		
		List<TCUnit> finalList = remainingLinksStep(_patterns, _selectedLinksSet, gateways, downOverUpRatio);
		_TT = finalList;
		System.out.println("Number of TC: " + _TT.size() + ", Summation of Capacity:" + getTotalCapacity() + ", Average Capacity: " + getAverageCapacity());
		FileGenerator.TransmissionConfige(_TT);
		FileGenerator.DataRate(_TT);
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
	protected List<TCUnit> gatewaysStep(List<Vertex> gateways, int downOverUpRatio, HashSet<Link> selectedLinksSet) 
	{
		List<TCUnit> listTCU = new ArrayList<TCUnit>();
		int downlinksNumber = 0;
		int uplinksNumber = 0;
		/* Get the number of gateway links */
		int numberOfGatewaysLink = 0;
		for(Vertex g : gateways) 
		{
			numberOfGatewaysLink += TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Incoming).size();
			numberOfGatewaysLink += TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Outgoing).size();
		}
		
		/* */
		while(selectedLinksSet.size() < numberOfGatewaysLink) 
		{
			TCUnit tcu = new TCUnit();
			/* For each gateway */
			for(Vertex g : gateways) 
			{
				List<Link> downlinks = TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Outgoing);
				List<Link> uplinks = TrafficEstimatingFacade.getOptimalLinks(g, LinkType.Incoming);
				
				LinkedHashSet<Link> downlinksKeySet = new LinkedHashSet<Link>(LinksTraffic.Sort().keySet());
				// Retain only the concerned downlinks from the set
				downlinksKeySet.retainAll(downlinks);
				
				
				// For each downlink
				int preSize = tcu.size();
				tcu = tryAddingLinks(downlinksKeySet, tcu, 1, selectedLinksSet);
				downlinksNumber += tcu.size() - preSize;
				/* END downlink loop */
				
				LinkedHashSet<Link> uplinksKeySet = new LinkedHashSet<Link>(LinksTraffic.Sort().keySet());
				// Retain only the concerned uplinks from the set
				uplinksKeySet.retainAll(uplinks);
				
				preSize = tcu.size();
				tcu = tryAddingLinks(uplinksKeySet, tcu, 1, selectedLinksSet);
				uplinksNumber += tcu.size() - preSize;
				
				if((uplinksNumber > 0)
						&& ((downlinksNumber / uplinksNumber) < downOverUpRatio)) {
					int newRatio = downOverUpRatio - (downlinksNumber / uplinksNumber);
					tcu = tryAddingLinks(downlinksKeySet, tcu, newRatio, selectedLinksSet);
				}
			}

			if(tcu.size() > 0) 
			{
				tcu = _sinr.calcDataRate(tcu);
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
	 * @see GreedyTC#checkAdd(Link, TCUnit)
	 * @param linksSet the set of links to try to add to the configuration
	 * @param tcu the configuration to try to add links to
	 * @param numberOfLinksToAdd the number of links to try to add
	 * @param selectedLinksSet the set containing the links already added to
	 * another configuration
	 * @return the transmission configuration tcu, modified or not
	 */
	private TCUnit tryAddingLinks(LinkedHashSet<Link> linksSet, TCUnit tcu, int numberOfLinksToAdd,	 Set<Link> selectedLinksSet) {
		int numberOfLinksAdded = 0;
		boolean newLinkAdded = true;
		while(newLinkAdded && (numberOfLinksAdded < numberOfLinksToAdd)) 
		{
			newLinkAdded = false;
			for(Link l : linksSet) 
			{
				if((!selectedLinksSet.contains(l))	&& !tcu.containsKey(l))
				{
					TCUnit modifiedTC = checkAdd(l, tcu.Clone());
					// Check if the link can be added to the current TCU
					if(modifiedTC != null)
					{
						// Then add it
						tcu = modifiedTC;
						selectedLinksSet.add(l);
						numberOfLinksAdded++;
						newLinkAdded = true;
						if(numberOfLinksAdded == numberOfLinksToAdd) 
							return tcu;
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
	 * @see GreedyTC#Enlarge(TCUnit)
	 * @see GreedyTC#enlargeByGateways(TCUnit, HashSet, List, int)
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
	protected List<TCUnit> remainingLinksStep(List<TCUnit> patterns, Set<Link> selectedLinksSet, List<Vertex> gateways, int downOverUpRatio)
			
	{
		
		IncreaseDatarate increaser = new IncreaseDatarate(this);
		List<TCUnit> listTCU = new ArrayList<TCUnit>(patterns);
		// Try adding links using generated patterns
		boolean newLinkAdded = true;
		while((selectedLinksSet.size() < numberOfLinks) && newLinkAdded)
		{
			newLinkAdded = false;
			for(TCUnit tcu : listTCU) 
			{
				int currentTCUIndex = listTCU.indexOf(tcu);
				for(Link l : LinksTraffic.Sort().keySet()) 
				{
					
					if(!selectedLinksSet.contains(l)) 
					{
						TCUnit modifiedTC = checkAdd(l, tcu.Clone());
						// Check if the link can be added to the current TCU
						if(modifiedTC != null) 
						{
							// Then add it
							tcu = modifiedTC;
							selectedLinksSet.add(l);
							newLinkAdded = true;
						}
					}
				}
				tcu = _sinr.calcDataRate(tcu);
				tcu = Enlarge(tcu);
				tcu = _sinr.calcDataRate(tcu);		
				if(listTCU.size() <= currentTCUIndex)
					listTCU.add(currentTCUIndex, tcu);
				else 
					listTCU.set(currentTCUIndex, tcu);
				
				resetMARK();
			}
		}
		// Create other TCU if necessary, to add remaining links
		while(selectedLinksSet.size() < numberOfLinks) 
		{
			TCUnit newTCU = new TCUnit();
			for(Link l : LinksTraffic.Sort().keySet()) 
			{
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
			if(newTCU.size() > 0) 
			{
				newTCU = _sinr.calcDataRate(newTCU);
				newTCU = Enlarge(newTCU);
				newTCU = _sinr.calcDataRate(newTCU);
				listTCU.add(newTCU);
			}
			resetMARK();
		}
		if(ApplicationSettingFacade.PowerControl.isEnable())
		{
			List<TCUnit> pTCs = new ArrayList<TCUnit>();
			for (TCUnit tcUnit : listTCU)
			{
				TCUnit poweredTC = _powerUnit.powerControl(tcUnit);
				TCUnit improve = increaser.increaser(poweredTC);
				if(improve != null)
					poweredTC = improve;
				pTCs.add(poweredTC);
			}
			
			listTCU = pTCs;
		}
		return listTCU;
	}


	@Override
	protected DeleteAction removeFromConsiderList(Link deletedLink)
	{
		int patternConter = 0;
		for (TCUnit patt : _patterns)
		{
			if(patt.containsKey(deletedLink))
				patternConter++;
				//return false;
		}
		if(patternConter > 1)
			return DeleteAction.Impossible;
		if(_selectedLinksSet.contains(deletedLink))
		{
			_selectedLinksSet.remove(deletedLink);
			return DeleteAction.True;
		//	return true;
		}
		
		return DeleteAction.NotNecessary;
		//return true;
		
	}

	@Override
	protected TCUnit checkAdd(Link newLink, TCUnit tConfig)
	{
		boolean add = true;
		double sinr = 0;
		TCUnit tPrime = tConfig.Clone();
		
		if(this.checkRadio(newLink))
		{
			//if(!balanceGatewayLinks(newLink,tConfig)) return null;
			tPrime.putRate(newLink, 0);
			tPrime.setTCAPZero();
			
		
			for (Link currentLink : tPrime.getLinks())
			{
				List<Link> linkSet = tPrime.getLinks();
				linkSet.remove(currentLink);
				
 				sinr = _sinr.calc(currentLink,linkSet );
				
				if(sinr >= BETA)
					tPrime.setSinrRate(currentLink,  _sinr.calcDataRate(sinr).getRate(), sinr);
				else
				{
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
}
