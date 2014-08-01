package scheduling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import GraphicVisualization.GraphViewer;
import launcher.Program;
import common.FileGenerator;
import trafficEstimating.TrafficEstimatingFacade;
import trafficGenerator.DynamicTrafficGenerator;
import dataStructure.Buffer;
import dataStructure.Link;
import dataStructure.LinkType;
import dataStructure.Packet;
import dataStructure.TCUnit;
import dataStructure.Vertex;

/** Scheduling strategy for dynamic routing.
 * Every time slot it seeks to route data in directions that maximize the
 * differential backlog between neighboring nodes.
 * @see <a href="http://en.wikipedia.org/wiki/Backpressure_routing">Backpressure Routing</a>
 * @author Benjamin
 */
public class BackPressureSchedulingStrategy extends SchedulingStrategy {
	
	public BackPressureSchedulingStrategy(DynamicTrafficGenerator dtg) {
		super(dtg);
	}

	@Override
	protected Vector<Link> getBufferStrategy(boolean isSourceBuffer) {
		return null;
	}
	
	@Override
	public void scheduling() {
		GraphViewer.showErrorDialog("Error using Back Pressure Scheduling Strategy",
				"Back Pressure Scheduling Strategy only\n"
				+ "implemented for dynamic traffic.\n"
				+ "Unable to use it for static traffic.");
		System.exit(0);
	}
	
	@Override
	public void dynamicScheduling(long durationOfTrafficGenerating) {
		
					System.out.println("Starting dynamic scheduling...");
		trafficGenerator = "Dynamic";
		
		int timeSlot = 0; // Current number of time slot
		
		sourceBuffers = null;
		do {
			totalTrafficGenerated = updateTraffic(0); // Fill the source buffers with random traffic
		} while(sourceBuffers.trafficSize() == 0);

		double maxTrafficSource = -1.0;
		double maxTrafficTransmit = -1.0;
		double slotThroughtput = 0;
		
		while(sourceBuffers.trafficSize() > 0 || transmitBuffers.trafficSize() > 0 || timeSlot < durationOfTrafficGenerating) {
			slotThroughtput = 0;
			TCUnit tcu = getMatchingTC(getOptimalWeightMap());
			for (Link link : tcu.getLinks()) {
				// Source buffers
				calcWeight(true);
				if(sourceBuffers.containsKey(link)) {
					int dataRate = tcu.getRate(link);
					Packet moved = sourceBuffers.sendPacket(link,dataRate,transmitBuffers,timeSlot);
					if(moved.isReceived()) {
						double movedTraffic = moved.getTraffic();
						packetsDelay.add(moved.getDelay());
						slotThroughtput += movedTraffic;
						tcu.addThroughput(movedTraffic);
					}
				}
				// Transit buffers
				calcWeight(false);
				if(transmitBuffers.containsKey(link)) {
					int dataRate = tcu.getRate(link);
					Packet moved = transmitBuffers.sendPacket(link,dataRate,transmitBuffers, timeSlot);
					if(moved.isReceived()) {
						double movedTraffic = moved.getTraffic();
						packetsDelay.add(moved.getDelay());
						slotThroughtput += movedTraffic;
						tcu.addThroughput(movedTraffic);
					}
				}
			}
			throughput.add(slotThroughtput);
			trafficSource.add(sourceBuffers.trafficSize());
			trafficTransit.add(transmitBuffers.trafficSize());
			
			/*----------------------*
			 * Generate new traffic *
			 * And display progress *
			 *----------------------*/
			timeSlot++;
			if(timeSlot < durationOfTrafficGenerating) {
				totalTrafficGenerated += updateTraffic(timeSlot);
				Program.loadingDialog.setProgress((int) (99*timeSlot/durationOfTrafficGenerating),
						"Generating traffic (slot "+timeSlot+" over "+durationOfTrafficGenerating+")");
			} else {
				if(maxTrafficSource < 0) {
					maxTrafficSource = sourceBuffers.trafficSize();
					Program.loadingDialog.setProgress(0);
				}
				if(sourceBuffers.trafficSize() == 0) {
					if(maxTrafficTransmit < 0) {
						maxTrafficTransmit = transmitBuffers.trafficSize();
						Program.loadingDialog.setProgress(0);
					}
					Program.loadingDialog.setProgress((int) (100-(99*transmitBuffers.trafficSize()/maxTrafficTransmit)),
							"Disposing of transmit traffic ("+transmitBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
				} else {
					Program.loadingDialog.setProgress((int) (100-(99*sourceBuffers.trafficSize()/maxTrafficSource)),
							"Disposing of source traffic ("+sourceBuffers.trafficSize()+" remaining, timeslot "+timeSlot+")");
				}
			}
		}
		FileGenerator.TCThroughput(configurations);
		FileGenerator.Throughput(throughput);
		
		int sum = 0;
		for(int i = 0; i < packetsDelay.size(); i++) {
			sum += packetsDelay.get(i);
		}
		System.out.println("Average delay: "+(sum / packetsDelay.size())+" timeslots");
	}
	
	/** 1st phase: Selecting the optimal commodity and computing the weight map.<br/>
	 * Note: A "commodity c data" is considered to be the data destined to node c.
	 * @return The weight map: <em>Key: link (a,b) -> Value: weight</em>.
	 */
	private Map<Link, Double> getOptimalWeightMap() {
		// Collecting the buffers with traffic from a node
		Map<Vertex, Buffer> nodeBuffersMap = new HashMap<Vertex, Buffer>();
		for(Link l : getAllLinksWithTraffic()) {
			nodeBuffersMap.put(l.getSource(), getBufferFromLink(l));
		}
		// Initializing the weight map:
		// Key: link (a,b) -> Value: weight
		Map<Link, Double> weightsMap = new HashMap<Link, Double>();
		// Considering each node a with traffic
		for(Vertex a : nodeBuffersMap.keySet()) {
			Buffer aBuffer = nodeBuffersMap.get(a);
			Map<Vertex, List<Packet>> aPacketDestMap = aBuffer.getPacketDestinationMap();
			// Looking at each a's neighbors b
			List<Link> neighborsLinks = TrafficEstimatingFacade.getOptimalLinks(a, LinkType.Outgoing);
			for(Link l : neighborsLinks) {
				Vertex b = l.getDestination();
				Buffer bBuffer = nodeBuffersMap.get(b);
				// Initializing the weight for link (a,b)
				double abWeight = 0.0;
				// Searching the optimal commodity for the link (a,b)
				for(Vertex destination : aPacketDestMap.keySet()) {
					double aCommodityAmount = aBuffer.getTrafficTowardDestination(destination);
					double bCommodityAmount = (bBuffer != null) ? 
							bBuffer.getTrafficTowardDestination(destination) : 0.0;
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
	private TCUnit getMatchingTC(Map<Link, Double> weightsMap) {
		TCUnit selectedTCU = null;		
		try {
			double matchingFactor = 0.0;
			for(TCUnit tcu : this.configurations) {
				double sum = 0.0;
				for(Link l : weightsMap.keySet()) {
					/*TODO
					 * Only add optimal path links... */
					if(this.sourceBuffers.containsKey(l) || this.transmitBuffers.containsKey(l)) {
						if(tcu.containsKey(l)) {
							sum += weightsMap.get(l) * tcu.getRate(l);
						}
					}
				}
				if(sum > matchingFactor) {
					matchingFactor = sum;
					selectedTCU = tcu;
				}
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return selectedTCU;
	}
	
	private Buffer getBufferFromLink(Link l) {
		return this.sourceBuffers.containsKey(l) ? this.sourceBuffers.get(l) : this.transmitBuffers.get(l);
	}

	private Set<Link> getAllLinksWithTraffic() {
		HashSet<Link> linkSet = new HashSet<Link>();
		linkSet.addAll(this.sourceBuffers.keySet());
		linkSet.addAll(this.transmitBuffers.keySet());
		return linkSet;
	}

	@Override
	protected String getName() {
		return "Back Pressure Scheduling Strategy";
	}

}
