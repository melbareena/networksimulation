package trafficGenerator;

import java.util.Random;
import java.util.TreeMap;

import dataStructure.DownlinkTraffic;
import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;

/** Produce dynamic traffic for the network, with poisson arrival for the packets.
 * @author Benjamin
 */
public class DynamicTrafficGenerator {
	
	private final int rateCoeff = 10;
	
	private double lambda;
	
	private long seed;
	
	/**Create a DynamicTrafficGenerator with the given trafficRate.
	 * All links are considered fairly.
	 * @param trafficRate The traffic rate of the network.
	 * @param seed The seed for the random number generator.
	 */
	public DynamicTrafficGenerator(double trafficRate, long seed) {
		this.lambda = trafficRate;
		this.seed = seed;
	}
	
	/**Give the traffic rate of this Generator.
	 * @return The traffic rate.
	 */
	public double getTrafficRate() {
		return lambda;
	}
	
	/**Generate some traffic for the <code>uplinks</code>.<br/>
	 * The traffic is generated following a Poisson law, generating an amount
	 * of traffic independently for each router.
	 * @param uplinks The uplinks to generate traffic for.
	 * @return The traffic generated.
	 */
	public UplinkTraffic generateTimeSlotUplinkTraffic(PathMap uplinks) {
		UplinkTraffic uplinkTraffic = new UplinkTraffic();
		for(Vertex router : uplinks.keySet()) {
			int rate = getPoissonArrival(this.lambda, this.seed);
			uplinkTraffic.add(router, rate*this.rateCoeff);
		}
		return uplinkTraffic;
	}
	
	/**Generate some traffic for the <code>downlinks</code>.<br/>
	 * The traffic is generated following a Poisson law, generating an amount
	 * of traffic independently for each router reachable by each gateway.
	 * @param downlinks The downlinks to generate traffic for.
	 * @return The traffic generated.
	 */
	public DownlinkTraffic generateTimeSlotDownlinkTraffic(PathMap downlinks) {
		DownlinkTraffic uplinkTraffic = new DownlinkTraffic();
		for(Vertex gateway : downlinks.keySet()) {
			TreeMap<Vertex,Float> gatewayTrafficMap = new TreeMap<>();
			for(Path p : downlinks.get(gateway)) {
				int rate = getPoissonArrival(this.lambda, this.seed);
				gatewayTrafficMap.put(p.getDestination(), rate*this.rateCoeff*1.0F);
			}
			uplinkTraffic.add(gateway, gatewayTrafficMap);
		}
		return uplinkTraffic;
	}
	
	/**Generate randomly a number of Poisson arrivals, given the <code>lambda</code> parameter.
	 * <br/>Note: Based on Knuth algorithm.
	 * @param lambda The lambda parameter for the Poisson distribution used, corresponding to
	 * the average number of arrivals in a given time interval.
	 * @param seed The seed for the random number generator.
	 * @return The number of arrivals, generated ramdomly following the Poisson law (<code>lambda</code>).
	 * @see <a href="http://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables">D. Knuth algorithm</a>
	 */
	public static int getPoissonArrival(double lambda, long seed) {
	    Random r = new Random(seed);
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
