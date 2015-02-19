package trafficGenerator;
import java.util.Hashtable;
import java.util.Map;


import java.util.Map.Entry;

import common.FileGenerator;
import setting.ApplicationSettingFacade;
import topology2graph.TopologyGraphFacade;
import dataStructure.*;

public class DTGFacade
{
	private DTGFacade(){}
	
	private static DTGFacade  _self;
	private Map<Integer, Traffic> _dynamicTraffic;
	private final long _duration = ApplicationSettingFacade.Traffic.getDuration();
	private final DynamicTrafficGenerator _dynamicTrafficGenerator = new DynamicTrafficGenerator();
	
	
	
	public Map<Vertex, Double> NodesRates;
	public static double offeredLoad = 0;
	public static int totalPackets = 0;
	
	
	
	public Traffic getDynamicTraffic(int timeSlot)
	{
		Traffic t = _dynamicTraffic.get(timeSlot).clone();	
		return t;
	}
	public static DTGFacade Initilization()
	{
		if(_self == null)
		{
			PathMap uplink = TopologyGraphFacade.getOptimalUplinkPaths();
			PathMap downLink = TopologyGraphFacade.getOptimalDownLinkPath();
			_self = new DTGFacade();
			//_self.Initialization();
			_self._dynamicTraffic = new Hashtable<Integer, Traffic>();
			for(int i = 0; i < _self._duration ; i++)
			{
				_self._dynamicTraffic.put(i, _self._dynamicTrafficGenerator.generateTraffic(uplink, downLink));
			}
			_self.NodesRates = DynamicTrafficGenerator._nodesRates;
			offeredLoad = DynamicTrafficGenerator._offerloadTraffic;
			totalPackets = DynamicTrafficGenerator._numPackets;
			System.out.println("Number Of Packets: " + totalPackets);
			System.out.println("Total Traffic: " + DynamicTrafficGenerator.totalTraffic());
			System.out.println("Total Traffic in TrafficMAP: " + getTotalReaffic());
		}
	
		return _self;
	}
	
	
	private static Double totalTraffic = null;
	public static double getTotalReaffic()
	{
		if(totalTraffic != null) return totalTraffic;
		double total = 0;
		
		for (Entry<Integer, Traffic> tt : _self._dynamicTraffic.entrySet())
		{
			
			total += tt.getValue().getDownlinkTraffic().size();
			total += tt.getValue().getUplinkTraffic().size();
		}
		
		
		FileGenerator.dynamicTraffic(_self._dynamicTraffic);
		return (double)Math.round(total * 100000) / 100000;	
	}

	private DownlinkTraffic _downlinkTraffic;
	private UplinkTraffic _uplinkTraffic;
	
	public DownlinkTraffic getDownlink(PathMap downlinkPaths)
	{
       
		if(_downlinkTraffic != null) return _downlinkTraffic;
		
		
		_downlinkTraffic = new DownlinkTraffic();
		for (Traffic traffic : _dynamicTraffic.values())	
			_downlinkTraffic.addAll(traffic.clone().getDownlinkTraffic().getTraffic());

	
		return _downlinkTraffic;
	}

	public UplinkTraffic getUplinkTraffic()
	{
		if(_uplinkTraffic != null) return _uplinkTraffic;
		
		
		_uplinkTraffic = new UplinkTraffic();
		for (Traffic traffic : _dynamicTraffic.values())	
			_uplinkTraffic.addAll(traffic.clone().getUplinkTraffic().getTraffic()); 
		
		return _uplinkTraffic;
		
	}
}
