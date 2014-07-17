package trafficEstimating;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import common.FileGenerator;
import common.IntermediateOutput;
import common.PrintConsole;
import topology2graph.TopologyGraphFacade;
import trafficGenerator.StaticTraffic;
import dataStructure.Link;
import dataStructure.TopologyGraph;
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
	protected PathMap getDownlinkPaths()
	{
		return DownlinkPaths;
	}

	
	protected LinkTrafficMap estimating(TopologyGraph gtd)
	{
		DownlinkPaths = TopologyGraphFacade.downlinkShortestPath(gtd);

		Map<Vertex, TreeMap<Vertex, Float>> staticTraffic = StaticTraffic.getDownlinkTraffic().getDownLinkTraffic();
		
		OptimizeDownlinkPaths();
	
		for (Entry<Vertex, List<Path>> dlPaths : DownlinkPaths.entrySet())
		{
			for (Path path : dlPaths.getValue())
			{	
				float traffic  = staticTraffic.get(path.getSource()).get(path.getDestination());
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


	private void OptimizeDownlinkPaths()
	{
		
		PathMap optimalsPathMap = new PathMap();
		
		Path optimal = null;
		for (Entry<Vertex, List<Path>> vp : DownlinkPaths.entrySet())
		{
			List<Path> optimalPaths = new ArrayList<>();
			for (Path path : vp.getValue())
			{
				Vertex destination = path.getDestination();
				optimal = path;
				
				for (Entry<Vertex, List<Path>> vps : DownlinkPaths.entrySet())
				{
					if(vp.getKey() != vps.getKey())
					{
						for (Path path2 : vps.getValue())
						{
							if(path2.getDestination() == destination)
							{
								if(path2.getEdgePath().size() < optimal.getEdgePath().size())
									optimal = path2;
							}
						}
					}
				}
				
				optimalPaths.add(optimal);
				
				
			}
			
			optimalsPathMap.put(vp.getKey(), optimalPaths);
			
		}
		DownlinkPaths = optimalsPathMap;
		
		FileGenerator.optimalDownlink(DownlinkPaths);
		
	}
}
