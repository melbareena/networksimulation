package topology2graph;

import dataStructure.IncomingLinksMap;
import dataStructure.OutcomingLinksMap;
import dataStructure.TopologyGraph;
import dataStructure.PathMap;

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
}
