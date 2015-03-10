package scheduling;

import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

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
		Link selectedBuffers = null;
		TCUnit bestTC = null;
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
			if((timeSlot!= 0 && timeSlot % _redoTimeSlot == 0) )
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
				selectedBuffers = getBufferStrategy(true);
				this.calcWeight(true);
				
				if(selectedBuffers != null)
				{
					
		 			bestTC = matching(selectedBuffers, timeSlot);
		 			
	 				dResult = delivery(bestTC, timeSlot, true);
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
			if(transmitBuffers.trafficSize() > 0)
			{
				if((timeSlot!= 0 && timeSlot % _redoTimeSlot == 0) )
					this.configurations = TCFacade.getConfigurations(timeSlot + 1, timeSlot + _redoTimeSlot , sourceBuffers, transmitBuffers);
				
				this.calcWeight(false);
	 			selectedBuffers = getBufferStrategy(false);
				if(selectedBuffers != null)
	 			{
					
		 			bestTC = matching(selectedBuffers, timeSlot);
		
		 			dResult = delivery(bestTC, timeSlot, false);
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
	
	
	
	@Override
	protected String getName()
	{
		return "DynamicMF";
	}
	protected Link getBufferStrategy(boolean isSourceBuffer) 
	{
		BufferMap targetBuffer;
		
		if(isSourceBuffer)
			targetBuffer = super.sourceBuffers;
		else
			targetBuffer = super.transmitBuffers;
		
		TreeMap<Link, Buffer> sortedBuffer = targetBuffer.sortByTraffic();
		return sortedBuffer.keySet().iterator().next();	
	}
	
	private TCUnit matching(Link maxKBuffer, int timeSlot)
	{
		double max = 0;
		double maxNumberOfLink = 0;
		TCUnit maxTC;
		max = 0;
		maxTC = null;
		for (TCUnit tc : configurations)
		{
			if(tc.containsKey(maxKBuffer))
			{
				double rate = tc.getMatchingRate(maxKBuffer);
				if(rate >= max && tc.getLinks().size() >= maxNumberOfLink)
				{
					max = rate;
					maxTC = tc;
					maxNumberOfLink =  tc.getLinks().size();
				}
			}
		}
		assert(maxTC != null ) : "\n\n\n max buffer is NULL \n\n\n";
		return maxTC;	
	}
}
