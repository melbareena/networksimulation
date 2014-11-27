package topology2graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import common.FileGenerator;
import setting.ApplicationSettingFacade;
import dataStructure.IncomingLinksMap;
import dataStructure.Link;
import dataStructure.OutcomingLinksMap;
import dataStructure.Path;
import dataStructure.TopologyGraph;
import dataStructure.PathMap;
import dataStructure.Vertex;

public class TopologyGraphFacade
{
	
	private static TopologyGraph _topologyGraph = null;
	
	
	
	private static TopologyToGraph topology = TopologyToGraph.Initiate();	
	public static TopologyGraph buildGraphFromTopology()
	{  
		if(_topologyGraph == null)
			_topologyGraph = topology.Convert();
		return _topologyGraph;
	}
	
	private static PathMap _downLinkPaths = null;
	
	public static PathMap downlinkShortestPath(TopologyGraph gtd)
	{	
		
		if(_downLinkPaths == null)
		{
			ShortestPath topologyPathes = ShortestPath.Initiate(gtd);
			_downLinkPaths = topologyPathes.getDownLinkShortestPathMap();
		}
		return _downLinkPaths;
	}
	
	private static PathMap _uplinkPaths = null;
	public static PathMap uplinkShortestPath(TopologyGraph gtd)
	{	
		if(_uplinkPaths == null)
		{
			ShortestPath topologyPathes = ShortestPath.Initiate(gtd);
			_uplinkPaths = topologyPathes.getUplinkShortestPathMap();
		}
		return _uplinkPaths;
	}
	public static OutcomingLinksMap getOutcomingLinks()
	{
		return topology.outcomingLinks;
	}
	public static IncomingLinksMap getIncomingLinks()
	{
		return topology.incomingLinks;
	}
	public static boolean isIncomingLinkToGateway(Link l)
	{
		Vector<Link> links = new Vector<Link>();
		
		for (Entry<Integer, Vertex> gateways : ApplicationSettingFacade.Gateway.getGateway().entrySet())
		{
			links.addAll(getIncomingLinks().get(gateways.getValue()));
		}
		return links.contains(l);
	}
	public static boolean isOutgoingLinkToGateway(Link l)
	{
		Vector<Link> links = new Vector<Link>();
		
		for (Entry<Integer, Vertex> gateways : ApplicationSettingFacade.Gateway.getGateway().entrySet())
		{
			links.addAll(getOutcomingLinks().get(gateways.getValue()));
		}
		return links.contains(l);
	}
	public static boolean isGatewayLink(Link l)
	{
		return isOutgoingLinkToGateway(l) || isIncomingLinkToGateway(l) ;
	}
	
	private static PathMap _optimalUplinkPath;
	public static PathMap getOptimalUplinkPaths()
	{
		if(_optimalUplinkPath == null)
		{
			PathMap allPaths = TopologyGraphFacade.uplinkShortestPath(buildGraphFromTopology()); // extract uplink paths
			_optimalUplinkPath = getOptimalUplinkPath(allPaths); // get optimal of them
		}
		return _optimalUplinkPath;
	}
	
	private static PathMap getOptimalUplinkPath(PathMap uplinkPathMap)
	{
		PathMap resultMap = new PathMap();
		for(Entry<Vertex, List<Path>> pathForRouters : uplinkPathMap.entrySet())
		{
			List<Path> allPathForARouter = pathForRouters.getValue();
			if(allPathForARouter.size() == 1)
			{
					resultMap.put(pathForRouters.getKey(), allPathForARouter);
					continue;
			}
			else
			{
				List<Path> optimalPaths = new ArrayList<>();
				int hopsP = allPathForARouter.get(0).getNodePath().size();
				optimalPaths.add(allPathForARouter.get(0));
				
				for(int i = 1 ; i < allPathForARouter.size() ; i++)
				{
					int hopsPP = allPathForARouter.get(i).getNodePath().size();
					float trashHold =(float) (hopsPP-hopsP) / hopsP;
					if(trashHold < 0.3 )
						optimalPaths.add( allPathForARouter.get(i));
				}
				resultMap.put(pathForRouters.getKey(), optimalPaths);
			}
		}
		FileGenerator.OptimalUplinkPath(resultMap);
		return resultMap;
	}
	
	
	private static PathMap _optimalDownPath;
	public static PathMap getOptimalDownLinkPath()
	{
		
		if(_optimalDownPath == null)
			OptimizeDownlinkPaths(downlinkShortestPath(buildGraphFromTopology()));
		
		return _optimalDownPath; 
	}
	private static void OptimizeDownlinkPaths(PathMap allPath)
	{
		
		PathMap optimalsPathMap = new PathMap();
		
		Path optimal = null;
		for (Entry<Vertex, List<Path>> vp : allPath.entrySet())
		{
			List<Path> optimalPaths = new ArrayList<>();
			for (Path path : vp.getValue())
			{
				Vertex destination = path.getDestination();
				optimal = path;
				
				for (Entry<Vertex, List<Path>> vps : allPath.entrySet())
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
		_optimalDownPath = optimalsPathMap;
		
		FileGenerator.optimalDownlink(_optimalDownPath);
		
	}
}
