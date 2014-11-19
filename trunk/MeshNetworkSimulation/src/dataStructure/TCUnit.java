package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Vector;



/**
 * @author Mahdi
 * Transmission Configuration Unit corresponding to T in the algorithm.
 */
public class TCUnit
{	
	private Map<Link, Integer> _rateCollection = new HashMap<Link, Integer>();
	private Map<Link, Double> _sinrCollection = new HashMap<Link, Double>();
	private Map<Link, Double> _powerCollection = new HashMap<Link, Double>(); 
	private Map<Link,Double> _linkWeight = new HashMap<Link, Double>();
	
	/**
	 * show the power control assign for the current T and calculation data rate not be perform to this TC;
	 */
	private boolean isLock = false;
	
	public boolean isLock()
	{
		return isLock;
	}

	public void setLock(boolean isLock)
	{
		this.isLock = isLock;
	}

	private boolean needAdjusmentPower = false;
	
	/**
	 * there is no feasible solution for the current TC
	 */
	private boolean isDead = false;
	
	
	public boolean isDead()
	{
		return isDead;
	}

	public void setDead(boolean isDead)
	{
		this.isDead = isDead;
		if(isDead)
			this.isLock = false;
	}

	public boolean needAdjusmentPower()
	{
		return needAdjusmentPower;
	}

	public void setNeedAdjusmentpower(boolean canAdjusmentpower)
	{
		this.needAdjusmentPower = canAdjusmentpower;
		if(canAdjusmentpower)
			this.isLock = false;
			
	}
	public Map<Link, Integer> getRateMap()
	{
		return _rateCollection;
	}
	
	public Map<Link, Double> getSinrMap()
	{
		return _sinrCollection;
	}
	
	
	
	public Map<Link,Double> getLinkWeight()
	{
		return _linkWeight;
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
	 * @param linksTraffic whole of traffic which is available in the configuation's links.
	 */
	public void calcLinkWeight(Map<Link,Double> linksTraffic)
	{
			
		for (Entry<Link, Double> traffic : linksTraffic.entrySet())
		{
			Link l = traffic.getKey();
			double traff = traffic.getValue();
			int r = this.getRate(l);
			_linkWeight.put(l, traff/r);
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
			if(_linkWeight.containsKey(l))
				rate += _linkWeight.get(l);
				
		return rate;
		
	}
	
	public boolean isLinkAvailable(Link l)
	{
		return _rateCollection.containsKey(l);
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
			for (Link l : _rateCollection.keySet())
			{
				if(l.getDestination() == g)
					counter++;
			}
		}
		else if(ty == LinkType.Outgoing)
		{
			for (Link l : _rateCollection.keySet())
			{
				if(l.getSource() == g)
					counter++;
			}
		}
		return counter;
		
	}
	
	public void putRate(Link l, int rate)
	{
		_rateCollection.put(l, rate);
	}	
	
	public List<Link> getLinks()
	{
		ArrayList<Link> links = new ArrayList<>();
		links.addAll(_rateCollection.keySet());
		return links;
	}
	
	public int getRate(Link l)
	{
		return _rateCollection.get(l);
	}	
	
	public double getSinr(Link l)
	{
		return _sinrCollection.get(l);
	}
	public double getPower(Link l)
	{
		return _powerCollection.get(l);
	}
	private void setDataRate(Link l , int dataRate)
	{
		//_rateCollection.remove(l);
		_rateCollection.put(l, dataRate);
	}
	private void setSinr(Link l , double sinr)
	{
		//_rateCollection.remove(l);
		_sinrCollection.put(l, sinr);
	}
	
	public void setPower(Link l , double power)
	{
		//_rateCollection.remove(l);
		_powerCollection.put(l, power);
	}
	public void setSinrRate(Link l, int dataRate,double sinr)
	{
		this.setDataRate(l, dataRate);
		this.setSinr(l, sinr);
	}
	
	public void removeLink(Link l)
	{
		if(_rateCollection.containsKey(l))
			_rateCollection.remove(l);
		if(_sinrCollection.containsKey(l))
			_sinrCollection.remove(l);	
		if(_powerCollection.containsKey(l))
			_powerCollection.remove(l);
		if(_linkWeight.containsKey(l))
			_linkWeight.remove(l);
			
	}

	/**
	 * @return Integer value is data rate.
	 */
	public Set<Entry<Link, Integer>> entrySetRate()
	{
		return _rateCollection.entrySet();
	}
	
	public boolean containsKey(Link key)
	{
		return _rateCollection.containsKey(key);
	}
	
	/**
	 * 
	 * @return number of links in the T.
	 */
	public int size()
	{
		return _rateCollection.size();
	}
	
	public int getTCAP()
	{
		if(_rateCollection.size() == 0 ) return 0;
		int tcap = 0;
		for (Entry<Link, Integer> linkDataRate : _rateCollection.entrySet())
		{
			tcap += linkDataRate.getValue();
		}
		return tcap;
	}
	
	@Override
	public String toString()
	{
		if(_rateCollection.size() == 0 ) return "";
		
		String out = "";
		for (Entry<Link, Integer> linkDataRate : _rateCollection.entrySet())
		{
			out += "Link: " + linkDataRate + ", rate:" + linkDataRate.getValue() + "\n";
		}
		return out;
	}
	
	public TCUnit Clone()
	{
		TCUnit copy = new TCUnit();
		for (Entry<Link, Integer> currentLink : this.entrySetRate())
		{
			copy.putRate(currentLink.getKey(), currentLink.getValue());
		}	
		return copy;
	}
	
	public void setTCAPZero()
	{
		Set<Link> links = _rateCollection.keySet();
		for (Link link : links)
		{
			_rateCollection.put(link, 0);
		}
		
	}

	public Link removeLinkRandomly()
	{	
		int i = getLinks().size();
		Random rand = new Random();
		Link deleted = getLinks().get(rand.nextInt(i));
		removeLink(deleted);
		return deleted;
	}
}
