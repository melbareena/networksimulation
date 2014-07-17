package topology2graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import setting.ApplicationSettingFacade;

import common.FileGenerator;
import common.IntermediateOutput;
import common.PrintConsole;
import dataStructure.Link;
import dataStructure.TopologyGraph;
import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.Vertex;

/**
 * @author umroot
 *
 */
class ShortestPath
{
	private static ShortestPath _selfObj;
	protected static ShortestPath Initiate(TopologyGraph graphData)
	{
		if(_selfObj == null)
			_selfObj = new ShortestPath(graphData);
		return _selfObj;
	}

	
	
	//
	private TopologyGraph graphData;
	
	
	private ShortestPath(TopologyGraph graphData)
	{
		this.graphData = graphData; 
		calcShortestPaths();
	}
	
	
	private PathMap DownLinkShortestPathMap = null;
	
	private void calcShortestPaths()
	{
		
		if(UplinkShortestPathMap == null)
		{
			this.calcDownlinkShortestPath();
			this.calcUplinkShortestPath();
		}
	}
	
	private void calcDownlinkShortestPath()
	{	
		int PathID = 0;
		DownLinkShortestPathMap = new PathMap();
		Map<Integer, Vertex> gateways = ApplicationSettingFacade.Gateway.getGateway();
		Map<Integer, Vertex> routers = ApplicationSettingFacade.Router.getRouters();
		Dijkstra dijkstraAlg = new Dijkstra(graphData.getPureGraph());
		
		
		Vertex sourceVertex ;
		List<Path> collectionPath;
		for(Entry<Integer, Vertex> gatewayEntry : gateways.entrySet())
		{
			collectionPath = new ArrayList<>(); // all path which their target is current gateway
			
			sourceVertex = graphData.VertexMap.get(gatewayEntry.getKey());
			dijkstraAlg.execute(sourceVertex);
			for(Entry<Integer, Vertex> routerEntry : routers.entrySet())
			{	
				Vertex targetVertex = routerEntry.getValue();
				LinkedList<Vertex> findedPath = dijkstraAlg.getPath(targetVertex);
				if(findedPath != null) // if there is a path
				{
					Path aPath = new Path(sourceVertex,  targetVertex , PathID);
					aPath.setNodePath(findedPath);
					aPath.setEdgePath(getEdgePath(findedPath));
					collectionPath.add(aPath);
					PathID++;
				}
			}
			if(collectionPath.size() > 0)
				DownLinkShortestPathMap.put(sourceVertex , collectionPath);
			
		}
				
		IntermediateOutput.showDownLinkPath(DownLinkShortestPathMap);

		
		PrintConsole.print("Calculating shortest path for each gateway (downlink) is done successfully.");
		FileGenerator.allShortestPathsInFile(DownLinkShortestPathMap, true);
	}
	
	private PathMap UplinkShortestPathMap = null;
	
	
	public PathMap getDownLinkShortestPathMap()
	{
		return DownLinkShortestPathMap;
	}



	public PathMap getUplinkShortestPathMap()
	{
		return UplinkShortestPathMap;
	}



	private void calcUplinkShortestPath()
	{
		int PathID = 0;
		UplinkShortestPathMap = new PathMap();
		if(DownLinkShortestPathMap == null)
			this.calcDownlinkShortestPath();
		for (Entry<Vertex, List<Path>> pathsForGatwayEntry : DownLinkShortestPathMap.entrySet())
		{
			for(Path path : pathsForGatwayEntry.getValue())
			{
				Path reversePath = getReversePath(path , PathID);
				UplinkShortestPathMap.put(reversePath.getSource(), reversePath);	
				PathID++;
			}
		}
		
		
		showUplinkPath();
		
		
		
		PrintConsole.print("Calculating all possible shortest path for each routers (uplink) is done successfully.");
		FileGenerator.allShortestPathsInFile(UplinkShortestPathMap, false);		
	}



	private void showUplinkPath()
	{
		if(ApplicationSettingFacade.AppOutput.showIntermediateOutput())
		{
			PrintConsole.printErr("**************************showing uplink paths****************************");
		
			for (List<Path> paths : UplinkShortestPathMap.values())
			{
				
				for (Path path : paths)
				{
					PrintConsole.print(path.toString()+ "\n");
				}
				PrintConsole.print("-----------------------");
				
			}
			PrintConsole.printErr("*****************************************************************************");
		}
	}
	
	
	private Path getReversePath(Path path, int PathID)
	{
		Path p = path.Clone();
		LinkedList<Vertex> nodesInOrginalPath = p.getNodePath();
		Path reversePath = new Path(p.getDestination(), p.getSource(),PathID);
		LinkedList<Vertex> nodesInReversePath = nodesInOrginalPath;
		Collections.reverse(nodesInReversePath);
		reversePath.setNodePath(nodesInReversePath);
		reversePath.setEdgePath(this.getEdgePath(reversePath.getNodePath()));
		return reversePath;
		
	}
	
	private LinkedList<Link> getEdgePath(LinkedList<Vertex> findedPath)
	{
		LinkedList<Link> linkedEdgeAsPath = new LinkedList<>();
		Vertex node_1 = null;
		Vertex node_2 = null;
		
		for(int i = 0 ; i< findedPath.size() -1 ; i++)
		{
			node_1 = findedPath.get(i);
			node_2 = findedPath.get(i+1);
			linkedEdgeAsPath.add(findEdge(node_1,node_2));
		}
		
		return linkedEdgeAsPath;
	}
	private Link findEdge(Vertex node_1, Vertex node_2)
	{
		Link aEdge;
		for(Entry<Integer,Link> edgeEntry :graphData.LinkMap.entrySet())
		{
			aEdge = edgeEntry.getValue();
			if(aEdge.getSource().equals(node_1) && aEdge.getDestination().equals(node_2))
				return aEdge;
		}
		PrintConsole.printErr("Error in converting shortestpath vertex to shortestpath edges");
		return null;
	}
}
