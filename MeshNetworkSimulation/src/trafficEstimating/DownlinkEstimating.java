package trafficEstimating;


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
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.Path;
import dataStructure.PathMap;
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
			
		Map<Vertex, TreeMap<Vertex, Double>> dynamicTraffic = dyTraffic.getDownlink(DownlinkPaths).getTraffic();
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
}
