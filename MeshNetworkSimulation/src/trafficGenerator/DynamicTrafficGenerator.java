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
 * @version 2
 */
class DynamicTrafficGenerator {
	
	
	/**
	 * Internal Veriables
	 */

	static double _offerloadTraffic = 0;
	static int _numPackets = 0;
	
	static double totalTraffic()
	{
		return (double)Math.round(_totalTraffic * 100000) / 100000;	
	}
	
	
	private static double _totalTraffic;
	
	
	static Map<Vertex, Double> _nodesRates;
	
	private static Map<Vertex, Double> _nodesLambda;
	
	private static float _lambda_max = ApplicationSettingFacade.Traffic.getLambdaMax();
	private static float _lambda_min = ApplicationSettingFacade.Traffic.getLambdaMin();
	private static int _downOverUpRatio = ApplicationSettingFacade.Traffic.getRatio();
	private static long _seed = ApplicationSettingFacade.Traffic.getSeed();
	private static Random  random = new Random(_seed);
	
	
	private static double generateLambda(int ratio)
	{
		return random.nextFloat() * ( (_lambda_max * ratio) - (_lambda_min * ratio) ) + (_lambda_min * ratio);
	}
	
	
	private static Map<Vertex, Double> getNodeLambda()
	{
		if(_nodesLambda == null)
		{
			_nodesLambda = new HashMap<Vertex, Double>();
			
			for (Vertex router :  ApplicationSettingFacade.Router.getRouter().values())
				_nodesLambda.put(router, generateLambda(1));
			
			for(Vertex gateway : ApplicationSettingFacade.Gateway.getGateway().values())
				_nodesLambda.put(gateway, generateLambda(_downOverUpRatio));
			
			_nodesRates = calcRates();
		}
		return _nodesLambda;
	}

	private static Map<Vertex, Double> calcRates()
	{
		
		if(_nodesRates != null) return _nodesRates;
		
		 Map<Vertex, Double> ratesMap = new HashMap<Vertex, Double>();
		 
		 for(Entry<Vertex, Double> numPackets : _nodesLambda.entrySet())
		 {
			 double x = numPackets.getValue() * 12000;
			 double rate = x / (Math.pow(10, 6));
			 ratesMap.put(numPackets.getKey(), rate);
		 }
		 return ratesMap;
		
	}


	private static int currentTimeSlot;
	
	
	Traffic generateTraffic(PathMap uplinks, PathMap downlinks) 
	{
		
		List<Vertex> routers = Lists.newArrayList(ApplicationSettingFacade.Router.getRouter().values());
		UplinkTraffic uplinkTraffic = generateTimeSlotUplinkTraffic(routers);
		
		
		List<Vertex> geteways = Lists.newArrayList(ApplicationSettingFacade.Gateway.getGateway().values());
		DownlinkTraffic downlinkTraffic = generateTimeSlotDownlinkTraffic(geteways, downlinks, _downOverUpRatio);
		
		
		if(currentTimeSlot==49)
		{
			System.out.println("Number Of Packets in one second: " + _numPackets);
			_offerloadTraffic = calcSpeed(_numPackets);
		}
		
		currentTimeSlot ++;
	
		return new Traffic(uplinkTraffic, downlinkTraffic);
	}
	

	private UplinkTraffic generateTimeSlotUplinkTraffic(List<Vertex> routers) {
		UplinkTraffic uplinkTraffic = new UplinkTraffic();
		if(routers != null) 
		{
			for(Vertex router : routers)
			{
				int numberOfPackets = getPoissonArrival(router);
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
					int numberOfPackets = getPoissonArrival(gateway);
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
	
	
	private int getPoissonArrival(Vertex sender)
	{
		 
		 int r = 0;
		 double a = random.nextDouble();
		 
		 double lambda = getNodeLambda().get(sender);

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
