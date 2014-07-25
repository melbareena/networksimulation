package trafficGenerator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import setting.ApplicationSettingFacade;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import dataStructure.DownlinkTraffic;
import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.Traffic;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;

/** Produce dynamic traffic for the network, with poisson arrival for the packets.
 * @author Benjamin
 * @version 1.1
 */
public class DynamicTrafficGenerator {
	
	/** Coefficient used to normalize the amount of traffic generated.<br/>
	 * The rate generated by the <code>getPoissonArrival</code> method
	 * is multiplied by this coefficient.
	 * @see DynamicTrafficGenerator#getPoissonArrival(double, long)
	 * @see DynamicTrafficGenerator#generateTimeSlotUplinkTraffic(PathMap)
	 * @see DynamicTrafficGenerator#generateTimeSlotDownlinkTraffic(PathMap) */
	private final int rateCoeff = 10;
	
	/** The parameter of the Poisson law used to generate traffic.
	 * <code>lambda</code> corresponds to the mean traffic rate of the network. */
	private double lambda;
	
	/** The random number generator. */
	private Random randomGenerator;
	
	/** The number of nodes considered at each new time slot. In other words,
	 * the number of nodes that this generator will try to generate traffic for,
	 * at each time slot. */
	private int nodesToConsider;
	
	/**The ratio for downlink traffic over uplink traffic.
	 * This generator will generate '<code>downOverUpRatio</code>' times more
	 * downlink traffic than uplink traffic. */
	private int downOverUpRatio;
	
	/**Create a DynamicTrafficGenerator with the given <code>trafficRate</code>,
	 * considering a given number of nodes at each time slot.<br/>
	 * All links are considered fairly, with the given <code>downOverUpRatio</code>.
	 * @param trafficRate The mean traffic rate of the network.
	 * @param seed The seed for the random number generator.
	 * @param nodesToConsider The number of nodes considered at each
	 * new time slot. In other words, the number of nodes that this generator will
	 * try to generate traffic for, at each time slot.
	 * @param downOverUpRatio The ratio for downlink traffic over uplink traffic.
	 * This generator will generate '<code>downOverUpRatio</code>' times more
	 * downlink traffic than uplink traffic.
	 */
	public DynamicTrafficGenerator(double trafficRate, long seed,
			int nodesToConsider, int downOverUpRatio) {
		this.lambda = trafficRate;
		this.randomGenerator = new Random(seed);
		this.nodesToConsider = nodesToConsider;
		this.downOverUpRatio = downOverUpRatio;
	}
	
	/**Generate some traffic in the network for {@link DynamicTrafficGenerator#nodesToConsider n}
	 * randomly selected nodes, and respecting the
	 * {@link DynamicTrafficGenerator#downOverUpRatio given ratio}.
	 * @param uplinks The uplinks to consider.
	 * @param downlinks The downlinks to consider.
	 * @return The traffic generated.
	 * @see DynamicTrafficGenerator#pickUpRandomNodes(int)
	 * @see DynamicTrafficGenerator#generateTimeSlotUplinkTraffic(Set)
	 * @see DynamicTrafficGenerator#generateTimeSlotDownlinkTraffic(Set, PathMap, int)
	 */
	public Traffic generateTraffic(PathMap uplinks, PathMap downlinks) {
		Set<Vertex> selectedNodes = pickUpRandomNodes(nodesToConsider);
		
		// Clone the original set of nodes in the uplink PathMap
		Set<Vertex> cloneUplinks = new HashSet<Vertex>(uplinks.keySet());
		// Intersect the set of nodes with the selectedNodes set
		cloneUplinks.retainAll(selectedNodes);
		UplinkTraffic uplinkTraffic = generateTimeSlotUplinkTraffic(cloneUplinks);
		
		// Clone the original set of nodes in the downlink PathMap
		Set<Vertex> cloneDownlinks = new HashSet<Vertex>(downlinks.keySet());
		// Intersect the set of nodes with the selectedNodes set
		cloneDownlinks.retainAll(selectedNodes);
		DownlinkTraffic downlinkTraffic = generateTimeSlotDownlinkTraffic(cloneDownlinks, downlinks, downOverUpRatio);
		
		return new Traffic(uplinkTraffic, downlinkTraffic);
	}
	
	/**Generate some traffic for the given <code>routers</code>.<br/>
	 * The traffic is generated following a Poisson law, generating an amount
	 * of traffic independently for each router.
	 * @param routers The set of routers to generate uplink traffic for.
	 * @return The traffic generated.
	 */
	protected UplinkTraffic generateTimeSlotUplinkTraffic(Set<Vertex> routers) {
		UplinkTraffic uplinkTraffic = new UplinkTraffic();
		if(routers != null) {
			for(Vertex router : routers) {
				int rate = getPoissonArrival(this.lambda, this.randomGenerator);
				uplinkTraffic.add(router, rate*this.rateCoeff);
			}
		}
		return uplinkTraffic;
	}
	
	/**Generate some traffic for the given <code>gateways</code>, considering
	 * <code>n</code> of their links.<br/>
	 * The traffic is generated following a Poisson law, generating an amount
	 * of traffic independently for each router reachable by each gateway.
	 * @param gateways The set of gateways to generate traffic for.
	 * @param downlinks The <code>PathMap</code> containing all the downlinks in the network.
	 * @param n The number of downlinks to consider per gateway (randomly chosen).
	 * @return The traffic generated.
	 */
	protected DownlinkTraffic generateTimeSlotDownlinkTraffic(Set<Vertex> gateways,
			PathMap downlinks, int n) {
		DownlinkTraffic downlinkTraffic = new DownlinkTraffic();
		if(gateways != null) {
			for(Vertex gateway : gateways) {
				TreeMap<Vertex,Float> gatewayTrafficMap = new TreeMap<>();
				// Shuffling the list of path from the gateway
				Collections.shuffle(downlinks.get(gateway));
				// Adding traffic for n links
				for(int i = 0; i < n; i++) {
					Path p = downlinks.get(gateway).get(i);
					int rate = getPoissonArrival(this.lambda, this.randomGenerator);
					gatewayTrafficMap.put(p.getDestination(), rate*this.rateCoeff*1.0F);
				}
				downlinkTraffic.add(gateway, gatewayTrafficMap);
			}
		}
		return downlinkTraffic;
	}
	
	/**Pick up randomly <code>n</code> nodes from the set of all nodes,
	 * and return them.
	 * @param n The number of nodes to pick up.
	 * @return A list of <code>n</code> randomly chosen.
	 */
	public Set<Vertex> pickUpRandomNodes(int n) {
		List<Vertex> shuffledList = Lists.newArrayList(ApplicationSettingFacade.Nodes.getNodes().values());
		Collections.shuffle(shuffledList);
		return Sets.newHashSet(shuffledList.subList(0, n));
	}
	
	/**Generate randomly a number of Poisson arrivals, given the <code>lambda</code> parameter.
	 * <br/>Note: Based on Knuth algorithm.
	 * @param lambda The lambda parameter for the Poisson distribution used, corresponding to
	 * the average number of arrivals in a given time interval.
	 * @param r The random number generator.
	 * @return The number of arrivals, generated ramdomly following the Poisson law (<code>lambda</code>).
	 * @see <a href="http://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables">D. Knuth algorithm</a>
	 */
	public static int getPoissonArrival(double lambda, Random r) {
	    double L = Math.exp(-lambda);
	    int k = 0;
	    double p = 1.0;
	    do {
	        p *= r.nextDouble();
	        k++;
	    } while (p > L);
	    return k - 1;
	}

}