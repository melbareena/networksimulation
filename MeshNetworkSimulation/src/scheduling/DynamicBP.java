package scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import luncher.Luncher;
import common.FileGenerator;
import common.PrintConsole;
import trafficEstimating.TrafficEstimatingFacade;
import transConf.TCFacade;
import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.Link;
import dataStructure.LinkType;
import dataStructure.Packet;
import dataStructure.SchedulingResult;
import dataStructure.TCUnit;
import dataStructure.Vertex;

public class DynamicBP 
{
	public DynamicBP(int instanceIndex)
	{
		PrintConsole.printErr("Intiate Scheduling..........");
		this.transmitBuffers = new BufferMap();
		this.throughput = new Vector<Double>();
		this.trafficSource = new Vector<Double>();
		this.trafficTransit = new Vector<Double>();
		this.packetsDelay = new Vector<Integer>();
		this.maxTrafficSource = -1.0;
		this.maxTrafficTransmit = -1.0;
		this.instanceIndex = instanceIndex;
	}
	private int instanceIndex;
	protected int k = 3;
	protected BufferMap sourceBuffers;
	protected BufferMap transmitBuffers;
	protected List<TCUnit> configurations;

	
	/* For collecting results */
	protected Vector<Double> throughput;
	protected Vector<Double> trafficSource;
	protected Vector<Double> trafficTransit;
	protected Vector<Integer> packetsDelay;
	protected double totalTrafficGenerated;
	protected String trafficGenerator;
	protected double maxTrafficSource;
	protected double maxTrafficTransmit;

	
	private Set<Link> getAllLinksWithTraffic() 
	{
		HashSet<Link> linkSet = new HashSet<Link>();
		linkSet.addAll(this.sourceBuffers.keySet());
		linkSet.addAll(this.transmitBuffers.keySet());
		return linkSet;
	}
	public SchedulingResult dynamicScheduling(long durationOfTrafficGenerating) 
	{
		this.configurations = TCFacade.getConfigurations(0, 10 , sourceBuffers, transmitBuffers);

		trafficGenerator = "Dynamic";

		int timeSlot = -1; // Current number of time slot
		
		sourceBuffers = null;
		do 
		{
			timeSlot++;
			totalTrafficGenerated = updateTraffic(timeSlot); // Fill the source buffers with random traffic
			
		} while (sourceBuffers.trafficSize() == 0);

		
		
		
		while (sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0	|| timeSlot < durationOfTrafficGenerating) 
		{	
			
			if(timeSlot!= 0 && timeSlot % 10 == 0)
				this.configurations = TCFacade.getConfigurations(timeSlot + 1, timeSlot + 10 , sourceBuffers, transmitBuffers);
			
		
			disposeOfTraffic(timeSlot);
			timeSlot++;
			if (timeSlot < durationOfTrafficGenerating)
			{
				totalTrafficGenerated += updateTraffic(timeSlot);
				Luncher.loadingDialog.setProgress(this.instanceIndex,
						(int) (99 * timeSlot / durationOfTrafficGenerating),
						"Generating traffic (slot " + timeSlot + " over " + durationOfTrafficGenerating + ")");
			} 
			else 
				updateProgress(timeSlot);
				
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
	
		return getResults();
	}
	
	
	protected void updateProgress(int timeSlot)
	{
		if (maxTrafficSource < 0) 
		{
			maxTrafficSource = sourceBuffers.trafficSize();
			Luncher.loadingDialog.setProgress(this.instanceIndex, 0);
		}
		if (sourceBuffers.trafficSize() == 0) 
		{
			if (maxTrafficTransmit < 0) 
			{
				maxTrafficTransmit = transmitBuffers.trafficSize();
				Luncher.loadingDialog.setProgress(this.instanceIndex, 0);
			}
			Luncher.loadingDialog.setProgress(this.instanceIndex,
					(int) (100 - (99 * transmitBuffers.trafficSize() / maxTrafficTransmit)),
					"Disposing of transmit traffic ("+ transmitBuffers.trafficSize()
							+ " remaining, timeslot " + timeSlot + ")");
		} 
		else 
		{
			Luncher.loadingDialog.setProgress(this.instanceIndex,
					(int) (100 - (99 * sourceBuffers.trafficSize() / maxTrafficSource)),
					"Disposing of source traffic (" + sourceBuffers.trafficSize()
							+ " remaining, timeslot " + timeSlot + ")");
		}
	}
	
	
	
	/**Tries to dispose of as much traffic as it can in a given timeslot.
	 * It is done by selecting the best matching transmission configuration,
	 * and sending packets on the available links	 * (source buffers and
	 * transmit buffers).
	 * @param timeSlot The current timeslot.
	 */
	private void disposeOfTraffic(int timeSlot) {
		double slotThroughtput = 0;
		TCUnit tcu = getMatchingTC(getOptimalWeightMap());
		for (Link link : tcu.getLinks()) {
			// Source buffers
			calcWeight(true);
			if (sourceBuffers.containsKey(link))
			{
				double dataRate = tcu.getRate(link);
				List<Packet> movedPackets = sourceBuffers.sendPacket(link, dataRate,	transmitBuffers, timeSlot);
				for (Packet moved : movedPackets)
				{
					if (moved.isReceived())
					{
						double movedTraffic = moved.getTraffic();
						if(!moved.isFragment()) 
							packetsDelay.add(moved.getDelay());
						slotThroughtput += movedTraffic;
						tcu.addThroughput(movedTraffic);
					}
				}
				
			}
			// Transit buffers
			calcWeight(false);
			if (transmitBuffers.containsKey(link)) 
			{
				double dataRate = tcu.getRate(link);
				List<Packet> movedPackets = transmitBuffers.sendPacket(link, dataRate, transmitBuffers, timeSlot);
				for (Packet moved : movedPackets)
				{
					if (moved.isReceived())
					{
						double movedTraffic = moved.getTraffic();
						if(!moved.isFragment())
							packetsDelay.add(moved.getDelay());
						slotThroughtput += movedTraffic;
						tcu.addThroughput(movedTraffic);
					}
				}
				
			}
		}
		throughput.add(slotThroughtput);
		trafficSource.add(sourceBuffers.trafficSize());
		trafficTransit.add(transmitBuffers.trafficSize());
	}

	/** 1st phase: Selecting the optimal commodity and computing the weight map.<br/>
	 * Note: A "commodity c data" is considered to be the data destined to node c.
	 * @return The weight map: <em>Key: link (a,b) -> Value: weight</em>.
	 */
	private Map<Link, Double> getOptimalWeightMap() 
	{
		// Collecting the buffers with traffic from a node
		Map<Vertex, List<Buffer>> nodeBuffersMap = getNodeBuffersMap();
		// Initializing the weight map:
		// Key: link (a,b) -> Value: weight
		Map<Link, Double> weightsMap = new HashMap<Link, Double>();
		// Considering each node a with traffic
		for (Vertex a : nodeBuffersMap.keySet()) 
		{
			Buffer aBuffer = nodeBuffersMap.get(a).get(0);
			Buffer secondABuffer = null;
			Set<Vertex> aPacketDestSet = new HashSet<Vertex>(aBuffer.getPacketDestinationMap().keySet());
			if(nodeBuffersMap.get(a).size() > 1)
			{ 
				secondABuffer = nodeBuffersMap.get(a).get(1);
				aPacketDestSet.addAll(secondABuffer.getPacketDestinationMap().keySet());
			}
			// Looking at each a's neighbors b
			List<Link> neighborsLinks = TrafficEstimatingFacade.getOptimalLinks(a, LinkType.Outgoing);
			for (Link l : neighborsLinks) 
			{
				Vertex b = l.getDestination();
				Buffer bBuffer = (nodeBuffersMap.containsKey(b)) ? nodeBuffersMap.get(b).get(0) : null;
				Buffer secondBBuffer = null;
				if(nodeBuffersMap.containsKey(b) && nodeBuffersMap.get(b).size() > 1)
					secondBBuffer = nodeBuffersMap.get(b).get(1);
				// Initializing the weight for link (a,b)
				double abWeight = 0.0;
				// Searching the optimal commodity for the link (a,b)
				for (Vertex destination : aPacketDestSet)
				{
					// a's commodity is the sum of all a's buffers traffic amount toward destination
					double aCommodityAmount = aBuffer.getTrafficTowardDestination(destination) +
							((secondABuffer == null) ?
								0.0 : secondABuffer.getTrafficTowardDestination(destination));
					// b's commodity is the sum of all b's buffers traffic amount toward destination
					double bCommodityAmount = (bBuffer != null) ?
							bBuffer.getTrafficTowardDestination(destination) : 0.0 +
							((secondBBuffer == null) ?
									0.0 : secondBBuffer.getTrafficTowardDestination(destination));
					// Refreshing the weight of link (a,b)
					abWeight = Math.max(aCommodityAmount - bCommodityAmount, abWeight);
				}
				// Adding weight for optimal commodity
				weightsMap.put(l, abWeight);
			}
		}
		return weightsMap;
	}

	/** 2nd phase: Selecting the TCUnit that matches best the current weight map.
	 * @param weightsMap The weight map: <em>Key: link (a,b) -> Value: weight</em>.
	 * @return The selected TCUnit.
	 */
	private TCUnit getMatchingTC(Map<Link, Double> weightsMap) 
	{
		TCUnit selectedTCU = null;
		double matchingFactor = 0.0;
		for (TCUnit tcu : this.configurations) {
			double sum = 0.0;
			for (Link l : weightsMap.keySet()) {
				/* Only add optimal path links */
				if (this.sourceBuffers.containsKey(l) || this.transmitBuffers.containsKey(l)) {
					if (tcu.containsKey(l)) {
						sum += weightsMap.get(l) * tcu.getRate(l);
					}
				}
			}
			if (sum >= matchingFactor) {
				matchingFactor = sum;
				selectedTCU = tcu;
			}
		}
		return selectedTCU;
	}
	private Map<Vertex, List<Buffer>> getNodeBuffersMap() 
	{
		Map<Vertex, List<Buffer>> nodeBuffersMap = new HashMap<Vertex, List<Buffer>>();
		for (Link l : getAllLinksWithTraffic()) {
			ArrayList<Buffer> buffers = new ArrayList<Buffer>();
			if(this.sourceBuffers.containsKey(l)) {
				buffers.add(this.sourceBuffers.get(l));
			}
			if(this.transmitBuffers.containsKey(l)) {
				buffers.add(this.transmitBuffers.get(l));
			}
			nodeBuffersMap.put(l.getSource(), buffers);
		}
		return nodeBuffersMap;
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
	private double updateTraffic(int currentTimeSlot)
	{
		double currentTrafficAmount = 0.0;
		if(sourceBuffers != null) {
			currentTrafficAmount = sourceBuffers.trafficSize();
		}
		
	
		BufferMap timesoltBuffer = TrafficEstimatingFacade.getDynamicSourceBuffers(currentTimeSlot);
		if(timesoltBuffer != null && timesoltBuffer.size() > 0)
		{
			if(sourceBuffers == null) sourceBuffers = new BufferMap();
			sourceBuffers.Append(timesoltBuffer);
			return sourceBuffers.trafficSize() - currentTrafficAmount;
		}
		
		return 0;
		
	}
	protected SchedulingResult getResults() 
	{
		SchedulingResult results = new SchedulingResult();
		results.setSchedulingStrategy(getName());
		results.setTrafficGenerator(trafficGenerator);
		results.setThroughputData(throughput);
		results.setSourceBufferData(trafficSource);
		results.setTransmitBufferData(trafficTransit);
		results.setTotalTrafficGenerated(totalTrafficGenerated);
		double sum = 0;
		for(int i = 0; i < packetsDelay.size(); i++) {
			sum += packetsDelay.get(i);
		}
		results.setAveragePacketDelay(sum );
		return results;
	}
	protected String getName() {
		return "BPSS";
	}
}
