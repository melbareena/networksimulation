package trafficEstimating;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import common.IntermediateOutput;
import common.PrintConsole;
import setting.ApplicationSettingFacade;
import topology2graph.TopologyGraphFacade;
import trafficGenerator.DTGFacade;
import trafficGenerator.StaticTraffic;
import dataStructure.BufferMap;
import dataStructure.DownlinkTraffic;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.Traffic;
import dataStructure.Vertex;

class DownlinkEstimating
{
	private static DownlinkEstimating self;
	protected static DownlinkEstimating Initiate(LinkTrafficMap trafficOfLink)
	{
		if(self == null)
			self = new DownlinkEstimating(trafficOfLink);
		return self;
	}
	
	private PathMap DownlinkPaths;
	
	private DownlinkEstimating(LinkTrafficMap trafficOfLink)
	{
		dl_Traffic_l = trafficOfLink;
	}

	private LinkTrafficMap dl_Traffic_l;
	
	
	protected LinkTrafficMap estimating()
	{
		if(ApplicationSettingFacade.Traffic.isDynamicType()) return dynamicEstimating();
		
		DownlinkPaths = TopologyGraphFacade.getOptimalDownLinkPath();
	
		Map<Vertex, TreeMap<Vertex, Double>> staticTraffic = StaticTraffic.getDownlinkTraffic(DownlinkPaths).getTraffic();
		
		for (Entry<Vertex, List<Path>> dlPaths : DownlinkPaths.entrySet())
		{
			for (Path path : dlPaths.getValue())
			{	
				Double traffic  = staticTraffic.get(path.getSource()).get(path.getDestination());
				for (Link edge : path.getEdgePath())	
				{
					dl_Traffic_l.put(edge, traffic);
				}		
			}
			
		}
		IntermediateOutput.downLinkTrafficEstimationResult(dl_Traffic_l);
		PrintConsole.print("Estimating downlink traffic is done successfully.");
		return dl_Traffic_l;
	}


	private LinkTrafficMap dynamicEstimating()
	{
		DTGFacade dyTraffic = DTGFacade.Initilization();
		DownlinkPaths = TopologyGraphFacade.getOptimalDownLinkPath();
			
		Map<Vertex, TreeMap<Vertex, Double>> dynamicTraffic = dyTraffic.getDownlink().getTraffic();
		for (Entry<Vertex, List<Path>> dlPaths : DownlinkPaths.entrySet())
		{
			for (Path path : dlPaths.getValue())
			{	
				Vertex source = path.getSource();
				Vertex  destination = path.getDestination();			
				if(dynamicTraffic.containsKey(source) && dynamicTraffic.get(source).containsKey(destination))
				{
						//Double traffic  = dynamicTraffic.get(path.getSource()).get(path.getDestination());
						
					Double traffic = dyTraffic.NodesRates.get(source);
					
						for (Link edge : path.getEdgePath())	
							dl_Traffic_l.put(edge, traffic);	
				}
				
			}
			
		}
		
		IntermediateOutput.downLinkTrafficEstimationResult(dl_Traffic_l);
		PrintConsole.print("Estimating downlink traffic is done successfully.");
		
		return dl_Traffic_l;
	}


	static LinkTrafficMap dynamicEstimating(int startTime, int stopTime, BufferMap sourceBuffer, BufferMap transmitBuffer)
	{
		
		LinkTrafficMap downlink_tarffic = new LinkTrafficMap();
		
		
		DTGFacade dyTraffic = DTGFacade.Initilization();
		PathMap dlPaths = TopologyGraphFacade.getOptimalDownLinkPath();
		
		Map<Integer, Traffic> traffic = dyTraffic.getDynamicTraffic(startTime,stopTime,sourceBuffer,transmitBuffer);
			
		Map<Vertex, TreeMap<Vertex, Double>> dynamicTraffic = getDownTraffic(traffic.values()).getTraffic();
		
		for (Entry<Vertex, List<Path>> path : dlPaths.entrySet())
		{
			for (Path p : path.getValue())
			{	
				Vertex source = p.getSource();
				Vertex  destination = p.getDestination();			
				if(dynamicTraffic.containsKey(source) && dynamicTraffic.get(source).containsKey(destination))
				{
					Double trafficValue  = dynamicTraffic.get(p.getSource()).get(p.getDestination());
						
					//Double trafficValue = dyTraffic.NodesRates.get(source);
					
						for (Link edge : p.getEdgePath())	
							downlink_tarffic.put(edge, trafficValue);	
				}
				
			}
			
		}
		
		return downlink_tarffic;
	}


	private static DownlinkTraffic getDownTraffic(Collection<Traffic> traffics)
	{
		DownlinkTraffic dt = new DownlinkTraffic();
		for (Traffic traffic : traffics)	
			dt.addAll(traffic.clone().getDownlinkTraffic().getTraffic());
		return dt;
	}
}
