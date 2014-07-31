package scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import dataStructure.Buffer;
import dataStructure.BufferMap;
import dataStructure.Link;
import dataStructure.LinkType;
import dataStructure.Packet;
import dataStructure.Vertex;

public class BackPressureSchedulingStrategy extends SchedulingStrategy {

	/**1- For each link (a,b), select the optimal commodity Copt to use.<br/>
	 * 2- Determine which transmission configuration to use.<br/>
	 * 3- Determine the amount of commodity to transmit over the link (a,b).
	 * @see scheduling.SchedulingStrategy#getBufferStrategy(boolean)
	 */
	@Override
	protected Vector<Link> getBufferStrategy(boolean isSourceBuffer) {
		// TODO
		BufferMap targetBuffers = (isSourceBuffer) ? this.sourceBuffers : this.transmitBuffers;
		
		
		/*2nd phase: determining which transmission configuration to use */
		
		
		return null;
	}
	
	/*1st phase: selecting the optimal commodity */
	private void phase1() {
		// Collecting the buffers with traffic from a node
		Map<Vertex, Buffer> nodeBuffersMap = new HashMap<Vertex, Buffer>();
		for(Link l : getAllLinksWithTraffic()) {
			nodeBuffersMap.put(l.getSource(), getBufferFromLink(l));
		}
		
		for(Vertex currentNode : nodeBuffersMap.keySet()) {
			Buffer b = nodeBuffersMap.get(currentNode);
			Map<Vertex, List<Packet>> packetDestinationMap = b.getPacketDestinationMap();
			for(Vertex destination : packetDestinationMap.keySet()) {
				double totalTrafficTowardDestination = 0.0;
				for(Packet p : packetDestinationMap.get(destination)) {
					totalTrafficTowardDestination += p.getTraffic();
				}
				/*TODO*/
				/* Regarder TrafficEstimatingFacade.getOptimalLinks(Vertex v, LinkType lt)
				 * Pour comparer totalTrafficTowardDestination avec le traffic pour
				 * la meme destination dans le buffer des voisins du noeud courant
				 */
			}
		}
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
