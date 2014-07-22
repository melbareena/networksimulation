package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;



/**
 * @author Mahdi
 * Transmission Configuration Unit
 */
public class TCUnit
{	
	private Map<Link, Integer> collection = new HashMap<Link, Integer>();
	
	private Map<Link,Double> linkWeight = new HashMap<Link, Double>();
	
	public Map<Link,Double> getLinkWeight()
	{
		return linkWeight;
	}
	
	private double throughput = 0;
	
	/**
	 * By calling this method the TCAP of the TC add to the throughput of current TC.
	 */
	public void addThroughput(double val)
	{
		throughput += val;
	}
	
	public double getThroughput()
	{
		return throughput;
	}
	
	/**
	 * Calculating the weight of each link in the current configuration
	 * @param linksTraffic: whole of traffic which is available in the configuation's links.
	 */
	public void calcLinkWeight(Map<Link,Double> linksTraffic)
	{
			
		for (Entry<Link, Double> traffic : linksTraffic.entrySet())
		{
			Link l = traffic.getKey();
			double traff = traffic.getValue();
			int r = this.getRate(l);
			linkWeight.put(l, traff/r);
		}
	}
	
	
	public double getMatchingRate(Vector<Link> links)
	{
		double rate = 0;
		
		for (Link link : links)
		{
			rate += getMatchingRate(link);
		}
		
		return rate;
		
	}
	public double getMatchingRate(Link l)
	{
		double rate = 0;		
			if(linkWeight.containsKey(l))
				rate += linkWeight.get(l);
				
		return rate;
		
	}
	
	public boolean isLinkAvailable(Link l)
	{
		return collection.containsKey(l);
	}
	
	public boolean isLinksAvailable(Vector<Link> links)
	{
		boolean is  = true;
		for (Link l : links)
		{
			if(!isLinkAvailable(l))
				is = false;
		}
		
		return is;
	}
	
	
	public int getCounter_g(Vertex g , LinkType ty)
	{
		int counter = 0; 
		if(ty == LinkType.Incoming)
		{
			for (Link l : collection.keySet())
			{
				if(l.getDestination() == g)
					counter++;
			}
		}
		else if(ty == LinkType.Outgoing)
		{
			for (Link l : collection.keySet())
			{
				if(l.getSource() == g)
					counter++;
			}
		}
		return counter;
		
	}
	public void put(Link l, int rate)
	{
		collection.put(l, rate);
	}	
	public List<Link> getLinks()
	{
		ArrayList<Link> links = new ArrayList<>();
		links.addAll(collection.keySet());
		return links;
	}
	public int getRate(Link l)
	{
		return collection.get(l);
	}	
	
	public void setDataRate(Link l , int dataRate)
	{
		collection.remove(l);
		collection.put(l, dataRate);
	}
	
	public void removeLink(Link l)
	{
		collection.remove(l);
	}
	
	
	/**
	 * @return Integer value is data rate.
	 */
	public Set<Entry<Link, Integer>> entrySet()
	{
		return collection.entrySet();
	}
	public boolean containsKey(Link key)
	{
		return collection.containsKey(key);
	}
	public int size()
	{
		return collection.size();
	}
	
	public int getTCAP()
	{
		if(collection.size() == 0 ) return 0;
		int tcap = 0;
		for (Entry<Link, Integer> linkDataRate : collection.entrySet())
		{
			tcap += linkDataRate.getValue();
		}
		return tcap;
	}
	
	@Override
	public String toString()
	{
		if(collection.size() == 0 ) return "";
		
		String out = "";
		for (Entry<Link, Integer> linkDataRate : collection.entrySet())
		{
			out += "Link: " + linkDataRate + ", rate:" + linkDataRate.getValue() + "\n";
		}
		return out;
	}
	
	public TCUnit Clone()
	{
		TCUnit copy = new TCUnit();
		for (Entry<Link, Integer> currentLink : this.entrySet())
		{
			copy.put(currentLink.getKey(), currentLink.getValue());
		}	
		return copy;
	}
	public void setTCAPZero()
	{
		Set<Link> links = collection.keySet();
		for (Link link : links)
		{
			collection.put(link, 0);
		}
		
	}
}
