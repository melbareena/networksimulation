package topology2graph;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import setting.ApplicationSettingFacade;
import common.FileGenerator;
import common.PrintConsole;
import dataStructure.Graph;
import dataStructure.IncomingLinksMap;
import dataStructure.Link;
import dataStructure.OutcomingLinksMap;
import dataStructure.TopologyGraph;
import dataStructure.Vertex;


class TopologyToGraph
{
	private static TopologyToGraph _selfObj;
	protected static TopologyToGraph Initiate()
	{
		if(_selfObj == null)
			_selfObj = new TopologyToGraph();
		return _selfObj;
	}
	private TopologyToGraph(){}
	
	private TopologyGraph graphT = new TopologyGraph();


	protected IncomingLinksMap incomingLinks = new IncomingLinksMap();
	protected OutcomingLinksMap outcomingLinks = new OutcomingLinksMap();
	
	protected  TopologyGraph Convert()
	{
		graphT.VertexMap = ApplicationSettingFacade.Nodes.getNodes();
				
		int edgeId = 0;
		
		Map<Vertex, Vector<Vertex>> neighbors = ApplicationSettingFacade.Router.getNeigbors();
		for(Entry<Vertex, Vector<Vertex>> neighbor : neighbors.entrySet())
		{
			Vertex source = neighbor.getKey();
			for(Vertex adjacencieNode : neighbor.getValue())
			{
				Link newLink = new Link(edgeId, source, adjacencieNode , 1);
				incomingLinks.put(adjacencieNode, newLink);
				outcomingLinks.put(source,newLink);
				graphT.LinkMap.put(edgeId, newLink); // weight of link is 1
				edgeId++;
			}		
		}
		FileGenerator.LinksInFile(new ArrayList<Link>(graphT.LinkMap.values()));
		PrintConsole.print("Toplogy is converted to a graph successfully.");
		graphT.setPureGrap(new Graph(
										new ArrayList<Vertex>(graphT.VertexMap.values()),
										new ArrayList<Link>(graphT.LinkMap.values())
									)
						  );
		return graphT;
	}
	
	
	
}
