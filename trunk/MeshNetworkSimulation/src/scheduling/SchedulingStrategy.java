package scheduling;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import common.FileGenerator;
import common.PrintConsole;
import trafficEstimating.TrafficEstimatingFacade;
import transConf.TCFacade;
import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.Link;
import dataStructure.Packet;
import dataStructure.TCUnit;


public abstract class SchedulingStrategy
{

	protected int k = 3;
	protected BufferMap sourceBuffers;
	protected BufferMap transmitBuffers;
	private List<TCUnit> configurations;
	private Vector<Double> throughput;
	
	private void Initiation()
	{
 		sourceBuffers = TrafficEstimatingFacade.getSourceBuffers();
		configurations = TCFacade.getConfigurations();
		transmitBuffers = new BufferMap();
		throughput = new Vector<>();
	}
	
	protected SchedulingStrategy()
	{
		this.Initiation();
	}
	
	
	public void scheduling()
	{
		Vector<Link> selectedBuffers = null;
		Vector<TCUnit> transmissionConfiguraions = null;
		//System.err.println( sourceBuffers.trafficSize()) ;
		while( sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 )
		{
			this.calcWeight(true);
			selectedBuffers = getBufferStrategy(true);
			
			double slotThroughtput = 0;

 			transmissionConfiguraions = matching(selectedBuffers);
 			//System.out.println( sourceBuffers.trafficSize()) ;
 			for (TCUnit tcunit : transmissionConfiguraions)
			{
				slotThroughtput = 0;
			
				
				for (Link link : tcunit.getLinks())
				{
					if(sourceBuffers.containsKey(link))
					{
						int dataRate = tcunit.getRate(link);
						Packet moved = sourceBuffers.sendPacket(link,dataRate,transmitBuffers);
						if(moved.isReceived())
						{
							double movedTraffic = moved.getTraffic();
							slotThroughtput += movedTraffic;
							tcunit.addThroughput(movedTraffic);
						}
					}
				}
				throughput.add(slotThroughtput);
			}
 			this.calcWeight(false);
 			selectedBuffers = getBufferStrategy(false);
 			
 			transmissionConfiguraions = matching(selectedBuffers);
 			//System.err.println(transmitBuffers.trafficSize()) ;
 			for (TCUnit tcunit : transmissionConfiguraions)
			{
				slotThroughtput = 0;
				
				for (Link link : tcunit.getLinks())
				{
					if(transmitBuffers.containsKey(link))
					{
						int dataRate = tcunit.getRate(link);
						Packet moved = transmitBuffers.sendPacket(link,dataRate,transmitBuffers);
						if(moved.isReceived())
						{
							double movedTraffic = moved.getTraffic();
							slotThroughtput += movedTraffic;
							tcunit.addThroughput(movedTraffic);
						}
					}
				}
				throughput.add(slotThroughtput);
			}
 			
 			PrintConsole.print(sourceBuffers.trafficSize() + "");
 			PrintConsole.printErr(sourceBuffers.trafficSize() + "");
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
	}
	private void calcWeight(boolean isSourceBufferTraffic)
	{
		BufferMap targetMap;		
		if(isSourceBufferTraffic)
			targetMap = this.sourceBuffers;
		else
			targetMap = this.transmitBuffers;
		
		for(TCUnit tc : configurations)
		{
			Map<Link, Double> linksTraffic = new HashMap<Link, Double>();
			for (Link l : tc.getLinks())
			{
				double traffic = 0;
					
				if(targetMap.containsKey(l))
				{
					Buffer bs = targetMap.get(l);
					traffic = bs.size();
					linksTraffic.put(l, traffic);
				}
			}
			tc.calcLinkWeight(linksTraffic);
		}
		
	}
	private Vector<TCUnit> matching(Vector<Link> maxKBuffer)
	{
		Vector<TCUnit> result = new Vector<>();
		Vector<TCUnit> allInOneTC = new Vector<>();
		for (TCUnit tc : configurations)
		{
			if(tc.isLinksAvailable(maxKBuffer))
				allInOneTC.add(tc);
		}
		if(allInOneTC.size() >= k)
		{
			TreeSet<Double> weights = new TreeSet<>(Collections.reverseOrder());
			for (TCUnit tcUnit : allInOneTC)
			{
				weights.add(tcUnit.getMatchingRate(maxKBuffer));
			}
			int indexK = 0;
			for (Double rate : weights)
			{
				if(indexK == k) break;
				for (TCUnit tcUnit : allInOneTC)
				{
					if(tcUnit.getMatchingRate(maxKBuffer) == rate)
						result.add(tcUnit);
				}
				indexK++;
			}
			
		}
		else if(allInOneTC.size() > 0 && allInOneTC.size() <= k)
			result = allInOneTC;
		
		else if(allInOneTC.size() == 0)
		{
			double max = 0;
			TCUnit maxTC;
			for (Link l : maxKBuffer)
			{
				max = 0;
				maxTC = null;
				for (TCUnit tc : configurations)
				{
					double rate = tc.getMatchingRate(l);
					if(rate > max)
					{
						max = rate;
						maxTC = tc;
					}
				}
				if(maxTC != null)
					result.add(maxTC);
			}
		}
		
		return result;
		
	}
	
	protected abstract Vector<Link> getBufferStrategy(boolean isSourceBuffer);
	
	public Vector<Double> getThroughput()
	{
		return throughput;
	}
}
