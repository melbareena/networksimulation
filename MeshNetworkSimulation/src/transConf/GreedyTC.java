package transConf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;
import common.FileGenerator;
import dataStructure.Link;
import dataStructure.LinkType;
import dataStructure.TCUnit;
import dataStructure.Triple;
import dataStructure.Vertex;


/**
 * 
 * @author Mahdi
 *
 */
public class GreedyTC extends TCBasic {
	

	protected GreedyTC() 
	{
		super._powerUnit = new PowerControlUnit(this);
		resetMARK();
	}
	
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
		
			tConfUnit = _sinr.calcDataRate(tConfUnit); // make sure data rates are correct.	

			
			
			_TT.add(tConfUnit);
			resetMARK();		
		}
		//*******************************************SETP 3****************************************************
		Enlarge();	
		
		
		System.out.println("Number of TC: " + _TT.size() + ", Summation of Capacity:" + getTotalCapacity() + ", Average Capacity: " + getAverageCapacity());
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
			copyTC.putRate(maxTriple.getA(), _sinr.calcDataRate(maxTriple.getC()).getRate());
									
		
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

	@Override
	protected DeleteAction removeFromConsiderList(Link deletedLink)
	{		
		for (TCUnit unit : _TT)
		{
			if(unit.containsKey(deletedLink))
				//return false;
				return DeleteAction.NotNecessary; // the link is in the others Transmission configuration, hence, it is not necessary to remove 
			//form consider links
		}
		ConsiderLinks.remove(deletedLink);
		//return true;
		return DeleteAction.True;
			
	}

	
	
	
	
 	
	
 	

	

	


	

	

	
	
	
	
}
