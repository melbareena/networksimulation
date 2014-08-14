package topology2graph;

import java.util.Vector;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;
import dataStructure.IncomingLinksMap;
import dataStructure.Link;
import dataStructure.OutcomingLinksMap;
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
}
