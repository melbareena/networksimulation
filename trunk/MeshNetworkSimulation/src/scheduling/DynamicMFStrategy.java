package scheduling;

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;

import luncher.Luncher;
import trafficGenerator.DTGFacade;
import transConf.TCFacade;
import common.FileGenerator;
import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.DeliverPacketResult;
import dataStructure.Link;
import dataStructure.Packet;
import dataStructure.SchedulingResult;
import dataStructure.TCUnit;

public class DynamicMFStrategy extends DynamicAbstract
{
	
	public DynamicMFStrategy(int instanceIndex)
	{
		super.transmitBuffers = new BufferMap();
		super.throughput = new Vector<Double>();
		super.trafficSource = new Vector<Double>();
		super.trafficTransit = new Vector<Double>();
		super.maxTrafficSource = -1.0;
		super.maxTrafficTransmit = -1.0;
		super.instanceIndex = instanceIndex;
	}

	@Override
	public SchedulingResult doDeliveryPackets()
	{
		Vector<Link> selectedBuffers = null;
		Vector<TCUnit> transmissionConfigurations = null;
		DeliverPacketResult dResult = null;
		this.configurations = TCFacade.getConfigurations(0, _redoTimeSlot , sourceBuffers, transmitBuffers);
		
		
		int timeSlot = -1; // Current number of time slot
		
		
		do 
		{
			timeSlot++;
			totalTrafficGenerated = updateTraffic(timeSlot); // Fill the source buffers with random traffic
			
		} while(sourceBuffers.trafficSize() == 0);

		while(sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 || timeSlot < durationOfTrafficGenerating) 
		{
			if(timeSlot!= 0 && timeSlot % _redoTimeSlot == 0)
				this.configurations = TCFacade.getConfigurations(timeSlot + 1, timeSlot + _redoTimeSlot , sourceBuffers, transmitBuffers);
			
			
			//both of buffers are empty 
			if(sourceBuffers.trafficSize() == 0 && transmitBuffers.trafficSize() == 0)
			{
				timeSlot++;
				throughput.add(0d);
				averageDelayPerTimeSlot.add(0d);
				trafficSource.add(sourceBuffers.trafficSize());
				trafficTransit.add(transmitBuffers.trafficSize());
				if(timeSlot < durationOfTrafficGenerating)
					totalTrafficGenerated += updateTraffic(timeSlot);
				continue;
			}
			 
		
			if(sourceBuffers.trafficSize() > 0)
			{
				this.calcWeight(true);
				selectedBuffers = getBufferStrategy(true);
				if(selectedBuffers.size() > 0)
				{
					
		 			transmissionConfigurations = matching(selectedBuffers);
		 			for (TCUnit tcunit : transmissionConfigurations) 
		 			{
		 				dResult = delivery(tcunit, timeSlot, true);
		 				
						if(dResult.remainedRate > 0) // we still have some capacities which are not used , so we use it to transfer transmit buffer 
						{
							if(transmitBuffers.trafficSize() > 0)
							   dResult =	delivery(tcunit, timeSlot, dResult.remainedRate , dResult.numberOfOriginalPakcets,dResult.delayInTs, dResult.throughputInTS, false);				
						}
						
						
						throughput.add(dResult.throughputInTS);
						AddAverageDelay(dResult.numberOfOriginalPakcets, dResult.delayInTs);
						super.totalDelayPerTimeSlot.add(dResult.delayInTs);
						trafficSource.add(sourceBuffers.trafficSize());
						trafficTransit.add(transmitBuffers.trafficSize());
						timeSlot++;
						if(timeSlot < durationOfTrafficGenerating) 
						{
							totalTrafficGenerated += updateTraffic(timeSlot);
							Luncher.loadingDialog.setProgress(this.instanceIndex,
									(int) (99*timeSlot/durationOfTrafficGenerating),
									"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
						}
						else
							updateProgress(timeSlot);
					}
				}
			}
			if(transmitBuffers.trafficSize() > 0)
			{
				if(selectedBuffers.size() > 0)
	 			{
					
		 			transmissionConfigurations = matching(selectedBuffers);
		 			for (TCUnit tcunit : transmissionConfigurations)
		 			{
		 				dResult = delivery(tcunit, timeSlot, false);
		 				if(dResult.remainedRate > 0) // we still have some capacities which are not used , so we use it to transfer packets in source buffer 
						{
							if(transmitBuffers.trafficSize() > 0)
							   dResult =	delivery(tcunit, timeSlot, dResult.remainedRate , dResult.numberOfOriginalPakcets,dResult.delayInTs, dResult.throughputInTS, true);				
						}
		 				
				

						throughput.add(dResult.throughputInTS);
						AddAverageDelay(dResult.numberOfOriginalPakcets, dResult.delayInTs);
						super.totalDelayPerTimeSlot.add(dResult.delayInTs);
						trafficSource.add(sourceBuffers.trafficSize());
						trafficTransit.add(transmitBuffers.trafficSize());
						timeSlot++;
						if(timeSlot < durationOfTrafficGenerating) 
						{
							totalTrafficGenerated += updateTraffic(timeSlot);
							Luncher.loadingDialog.setProgress(this.instanceIndex,
									(int) (99*timeSlot/durationOfTrafficGenerating),
									"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
						} else 
							updateProgress(timeSlot);
					}
				}
			}
 			this.calcWeight(false);
 			selectedBuffers = getBufferStrategy(false);
 			
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
		
		assert(accumulationOfThroughput() == DTGFacade.getTotalTraffic())
		: "throughput is not valid \n traffic in MAP:" +  DTGFacade.getTotalTraffic() + " Throughput =" + accumulationOfThroughput();

		
		return getResults();
	}

	private DeliverPacketResult delivery(TCUnit tc, int timeSlot, boolean transferFromSourceBuffer)
	{
		BufferMap targetBuffer = sourceBuffers;
		
		if(!transferFromSourceBuffer)
			targetBuffer = transmitBuffers;
		
		double 	summationOfRate = tc.getTCAP();
		int	numberOfOriginalReceivedPacket = 0;
		double totalDelayInTS = 0;
		double slotThroughtput = 0;
		for (Link link : tc.getLinks()) 
		{
			if(targetBuffer.containsKey(link)) 
			{
				double dataRate = (double)tc.getRate(link) ;	
				summationOfRate -= dataRate;
				List<Packet> movedPackets = targetBuffer.sendPacket(link,dataRate,transmitBuffers, timeSlot);
				for (Packet moved : movedPackets) 
					if(moved.isReceived())
					{
						double movedTraffic = moved.getTraffic();
						if(moved.isOrginalPacket())
						{
							_totalpacketNumber++;
							numberOfOriginalReceivedPacket++;
							totalDelayInTS += moved.getDelay();
						}
						slotThroughtput += movedTraffic;
						tc.addThroughput(movedTraffic);
					}						
			}
		}
		
		return new DeliverPacketResult(numberOfOriginalReceivedPacket, totalDelayInTS, slotThroughtput, summationOfRate);
		
	}
	
	private DeliverPacketResult delivery(TCUnit tc, int timeSlot,double remainedRate, int numberOfOriginalReceivedPacket,
			double totalDelayInTS, double slotThroughtput  , boolean transferFromSourceBuffer)
	{
		BufferMap targetBuffer = sourceBuffers;
		
		if(!transferFromSourceBuffer)
			targetBuffer = transmitBuffers;
		
		for (Link link : tc.getLinks()) 
		{
			if(remainedRate <= 0) break;
			if(targetBuffer.containsKey(link)) 
			{
				double dataRate = (double)tc.getRate(link) ;
				double rate = dataRate < remainedRate ? dataRate : remainedRate;
				remainedRate -= dataRate;
				
				List<Packet> movedPackets = targetBuffer.sendPacket(link, rate ,transmitBuffers, timeSlot);
				for (Packet moved : movedPackets) 
					if(moved.isReceived())
					{
						double movedTraffic = moved.getTraffic();
						if(moved.isOrginalPacket())
						{
							_totalpacketNumber++;
							numberOfOriginalReceivedPacket++;
							totalDelayInTS += moved.getDelay();
						}
						slotThroughtput += movedTraffic;
						tc.addThroughput(movedTraffic);
					}						
			}
		}
		
		return new DeliverPacketResult(numberOfOriginalReceivedPacket, totalDelayInTS, slotThroughtput, 0);
		
	}
	
	@Override
	protected String getName()
	{
		return "DynamicMF";
	}
	protected Vector<Link> getBufferStrategy(boolean isSourceBuffer) 
	{
		BufferMap targetBuffer;
		
		if(isSourceBuffer)
			targetBuffer = super.sourceBuffers;
		else
			targetBuffer = super.transmitBuffers;
		
		
		Vector<Link> selectedLinks = new Vector<>();
		
		
		TreeMap<Link, Buffer> sortedBuffer = targetBuffer.sortByTraffic();
		int index = 0;
		for (Entry<Link, Buffer> lb : sortedBuffer.entrySet())
		{
			index++;
			
			
			selectedLinks.add(lb.getKey() );
			
			if(super.k == index)
				break;

		}
		return selectedLinks;

		
	}
	
	private Vector<TCUnit> matching(Vector<Link> maxKBuffer)
	{
		Vector<TCUnit> result = new Vector<>();
		Vector<TCUnit> allInOneTC = new Vector<>();
		for (TCUnit tc : configurations)
		{
			if(tc.containsLinks(maxKBuffer))
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
	

}
