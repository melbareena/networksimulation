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
import dataStructure.Link;
import dataStructure.Packet;
import dataStructure.SchedulingResult;
import dataStructure.TCUnit;

public class DynamicRRStrategy extends DynamicAbstract
{
	private int sourcePositionIndex;
	private int tranmissionPositionIndex;

	public DynamicRRStrategy(int instanceIndex)
	{
		sourcePositionIndex = 0;
		tranmissionPositionIndex = 0;
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
			
			
			int numberOfOriginalReceivedPacket = 0;
			double totalDelayInTS = 0;
			double slotThroughtput = 0;

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
		 				numberOfOriginalReceivedPacket = 0;
		 				totalDelayInTS = 0;
						slotThroughtput = 0;
						for (Link link : tcunit.getLinks()) 
						{
							if(sourceBuffers.containsKey(link)) 
							{
								double dataRate = (double)tcunit.getRate(link) ;															
								List<Packet> movedPackets = sourceBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
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
										tcunit.addThroughput(movedTraffic);
									}						
							}
						}
						throughput.add(slotThroughtput);
						AddAverageDelay(numberOfOriginalReceivedPacket, totalDelayInTS);
						super.totalDelayPerTimeSlot.add(totalDelayInTS);
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
						slotThroughtput = 0;
						numberOfOriginalReceivedPacket = 0;
		 				totalDelayInTS = 0;
						for (Link link : tcunit.getLinks())
						{
							if(transmitBuffers.containsKey(link)) 
							{
								double dataRate = (double) tcunit.getRate(link);
								List<Packet> movedPackets = transmitBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
								
								for (Packet moved : movedPackets)
								{
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
										tcunit.addThroughput(movedTraffic);
									}
								}
								
							}
						}
						throughput.add(slotThroughtput);
						AddAverageDelay(numberOfOriginalReceivedPacket, totalDelayInTS);
						super.totalDelayPerTimeSlot.add(totalDelayInTS);
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
	
	
	

	protected Vector<Link> getBufferStrategy(boolean isSourceBuffer) {
		if (sourcePositionIndex >= super.sourceBuffers.size()) {
			sourcePositionIndex = 0;
		}
		if (tranmissionPositionIndex >= super.transmitBuffers.size()) {
			tranmissionPositionIndex = 0;
		}

		BufferMap targetBuffer;

		int roundRobinIndex = 0;

		if (isSourceBuffer) {
			targetBuffer = super.sourceBuffers;
			roundRobinIndex = sourcePositionIndex;
		} else {
			targetBuffer = super.transmitBuffers;
			roundRobinIndex = tranmissionPositionIndex;
		}

		Vector<Link> selectedLinks = new Vector<>();

		TreeMap<Link, Buffer> sortedBuffer = targetBuffer.sortByTraffic();
		int index = 0;
		int inserted = 0;
		for (Entry<Link, Buffer> lb : sortedBuffer.entrySet()) {
			if (index == roundRobinIndex) {
				selectedLinks.add(lb.getKey());
				inserted++;
				roundRobinIndex++;
				if (isSourceBuffer) {
					sourcePositionIndex++;
				} else {
					tranmissionPositionIndex++;
				}
			}
			if (super.k == inserted) {
				break;
			}
			index++;
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
	
	@Override
	protected String getName()
	{
		return "DynamicRR";
	}


}
