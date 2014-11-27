package trafficGenerator;
import java.util.Hashtable;
import java.util.Map;


import setting.ApplicationSettingFacade;
import topology2graph.TopologyGraphFacade;
import dataStructure.*;

public class DynamicTraffic
{
	private DynamicTraffic(){}
	
	private static DynamicTraffic  _self;
	
	private Map<Integer, Traffic> _dynamicTraffic;
	private final long _duration = ApplicationSettingFacade.Traffic.getDuration();
	private final DynamicTrafficGenerator _dynamicTrafficGenerator = new DynamicTrafficGenerator();
	
	
	public Traffic getDynamicTraffic(Integer timeSlot)
	{
		return _dynamicTraffic.get(timeSlot);
	}
	public static DynamicTraffic Initilization()
	{
		if(_self == null)
		{
			PathMap uplink = TopologyGraphFacade.getOptimalUplinkPaths();
			PathMap downLink = TopologyGraphFacade.getOptimalDownLinkPath();
			_self = new DynamicTraffic();
			//_self.Initialization();
			_self._dynamicTraffic = new Hashtable<Integer, Traffic>();
			for(int i = 0; i < _self._duration ; i++)
			{
				_self._dynamicTraffic.put(i, _self._dynamicTrafficGenerator.generateTraffic(uplink, downLink));
			}
		}
		
		return _self;
	}
	
	private DownlinkTraffic _downlinkTraffic;
	private UplinkTraffic _uplinkTraffic;
	
	public DownlinkTraffic getDownlink(PathMap downlinkPaths)
	{
		if(_downlinkTraffic != null) return _downlinkTraffic;
		
		
		_downlinkTraffic = new DownlinkTraffic();
		for (Traffic traffic : _dynamicTraffic.values())	
			_downlinkTraffic.addAll(traffic.getDownlinkTraffic().getTraffic());

	
		return _downlinkTraffic;
	}

	public UplinkTraffic getUplinkTraffic()
	{
		if(_uplinkTraffic != null) return _uplinkTraffic;
		
		
		_uplinkTraffic = new UplinkTraffic();
		for (Traffic traffic : _dynamicTraffic.values())	
			_uplinkTraffic.addAll(traffic.getUplinkTraffic().getTraffic()); 
		
		return _uplinkTraffic;
		
	}
}
