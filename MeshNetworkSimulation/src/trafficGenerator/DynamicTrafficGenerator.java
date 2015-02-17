package trafficGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import setting.ApplicationSettingFacade;

import com.google.common.collect.Lists;

import dataStructure.DownlinkTraffic;
import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.Traffic;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;

/** Produces dynamic traffic for the network, with poisson arrival for the packets.
 * @author Mahdi
 * @version 1.1
 */
public class DynamicTrafficGenerator {
	

	private static double _totalTraffic;
	
	public static double totalTraffic()
	{
		return (double)Math.round(_totalTraffic * 100000) / 100000;	
	}
	
	private float lambda_max;
	private float lambda_min;
	private int downOverUpRatio;
	private long _seed;
	
	public static double _offerloadTraffic = 0;
	public static int _numPackets = 0;
	
	private DynamicTrafficGenerator(float lambdamin, float lambdamax, long seed, int downOverUpRatio) {
		this.lambda_max = lambdamax;
		this.lambda_min = lambdamin;
		//this.nodesToConsider = nodesToConsider;
		this.downOverUpRatio = downOverUpRatio;
		this._seed = seed;
	}
	
	
	public DynamicTrafficGenerator() {
		this(ApplicationSettingFacade.Traffic.getLambdaMax(),
				ApplicationSettingFacade.Traffic.getLambdaMin(),
				ApplicationSettingFacade.Traffic.getSeed(),
				ApplicationSettingFacade.Traffic.getRatio());
	}
	
	private int currentTimeSlot;
	Traffic generateTraffic(PathMap uplinks, PathMap downlinks) 
	{
		
		List<Vertex> routers = Lists.newArrayList(ApplicationSettingFacade.Router.getRouter().values());
		UplinkTraffic uplinkTraffic = generateTimeSlotUplinkTraffic(routers);
		
		
		List<Vertex> geteways = Lists.newArrayList(ApplicationSettingFacade.Gateway.getGateway().values());
		DownlinkTraffic downlinkTraffic = generateTimeSlotDownlinkTraffic(geteways, downlinks, downOverUpRatio);
		
		
		if(currentTimeSlot==49)
		{
			System.out.println("Number Of Packets in one second: " + _numPackets);
			_offerloadTraffic = calcSpeed(_numPackets);
		}
		
		currentTimeSlot ++;
	/*	Set<Vertex> selectedNodes = pickUpRandomNodes(nodesToConsider);
		
		// Clone the original set of nodes in the uplink PathMap
		Set<Vertex> cloneUplinks = new HashSet<Vertex>(uplinks.keySet());
		// Intersect the set of nodes with the selectedNodes set
		cloneUplinks.retainAll(selectedNodes);
		UplinkTraffic uplinkTraffic = generateTimeSlotUplinkTraffic(cloneUplinks);
		
		// Clone the original set of nodes in the downlink PathMap
		Set<Vertex> cloneDownlinks = new HashSet<Vertex>(downlinks.keySet());
		// Intersect the set of nodes with the selectedNodes set
		cloneDownlinks.retainAll(selectedNodes);
		
		*/
		return new Traffic(uplinkTraffic, downlinkTraffic);
	}
	

	private UplinkTraffic generateTimeSlotUplinkTraffic(List<Vertex> routers) {
		UplinkTraffic uplinkTraffic = new UplinkTraffic();
		if(routers != null) 
		{
			for(Vertex router : routers)
			{
				int numberOfPackets = getPoissonArrival(1);
				if(numberOfPackets > 0) 
				{
					uplinkTraffic.add(router, calcTraffic(numberOfPackets));
				}
			}
		}
		return uplinkTraffic;
	}
	
	
	private DownlinkTraffic generateTimeSlotDownlinkTraffic(List<Vertex> gateways, PathMap downlinks, int downOverUp) 
	{
		DownlinkTraffic downlinkTraffic = new DownlinkTraffic();
		if(gateways != null && gateways.size() > 0)
		{
			for(Vertex gateway : gateways)
			{
				TreeMap<Vertex,Double> gatewayTrafficMap = new TreeMap<Vertex,Double>();
				// Shuffling the list of path from the gateway
				// Adding traffic for n links
				for(int i = 0; i < downlinks.get(gateway).size() ; i++)
				{
					Path p = downlinks.get(gateway).get(i);
					int numberOfPackets = getPoissonArrival(downOverUp);
					if(numberOfPackets > 0) 
					{
						gatewayTrafficMap.put(p.getDestination(), calcTraffic(numberOfPackets));
					}

				}
				if(gatewayTrafficMap.size() > 0)
					downlinkTraffic.add(gateway, gatewayTrafficMap);
			}
		}
		return downlinkTraffic;
	}
	
	/**Picks up randomly <code>n</code> nodes from the set of all nodes,
	 * and return them.
	 * @param n The number of nodes to pick up.
	 * @return A list of <code>n</code> randomly chosen.
	 
	private Set<Vertex> pickUpRandomNodes(int n) {
		List<Vertex> shuffledList = Lists.newArrayList(ApplicationSettingFacade.Nodes.getNodes().values());
		//Collections.shuffle(shuffledList, this.randomGenerator);
		//return Sets.newHashSet(shuffledList.subList(0, n));
		
		return Sets.newHashSet(shuffledList);
	}*/
	
	/**
	 * 
	 * @param ratio : refers to ratio of downlink traffic over uplink traffic
	 * @return the number of packets in each time slot
	 */
	
	Random random = new Random(_seed);
	private int getPoissonArrival(int ratio)
	{
		 
		 int r = 0;
		 double a = random.nextDouble();
		 
		 double lambda = random.nextFloat() * ( (lambda_max * ratio) - (lambda_min * ratio) ) + (lambda_min * ratio);

		 double p = Math.exp(-lambda);

		    while (a > p) {
		        r++;
		        a = a - p;
		        p = p * lambda / r;
		    }
		    
		    _numPackets += r;
		    return r;
	}
	
	
	/**
	 * 
	 * @param numPacket: the number of tarffic
	 * @return packet size in mega bits 
	 */
	private double calcTraffic(int numPacket)
	{
		double traffic = (numPacket*12000)/ Math.pow(10, 6);
		_totalTraffic += traffic;
		return traffic;
	}
	
	private double calcSpeed(double numberOfPackets)
	{
		double x = (numberOfPackets ) * 12000;
		double rate = x / (Math.pow(10, 6));
		return rate;
	}

}
