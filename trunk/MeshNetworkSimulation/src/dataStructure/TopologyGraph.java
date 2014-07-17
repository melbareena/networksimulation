package dataStructure;

import java.util.Map;
import java.util.TreeMap;


public class TopologyGraph
{
	
	/**
	 *  vertexes of graph which is generated based on topology. the key is node's ID and the value is Vertex object which is created 
	 * based on topology's nodes.
	 */
	public  Map<Integer, Vertex> VertexMap;
	
	/**
	 * edges of graph which is generated based on topology. the key is link's ID and the value is Edges object which is created 
	 * based on topology's nodes.
	 */
	public  Map<Integer,Link> LinkMap;
	
	private Graph PureGrap;
	public void setPureGrap(Graph pureGrap)
	{
		PureGrap = pureGrap;
	}

	public Graph getPureGraph()
	{
		return PureGrap;
	}
	public TopologyGraph( Map<Integer, Vertex> mapOfGraphVertex,  Map<Integer, Link> mapOfGraphEdge, Graph puregraph)
	{
		LinkMap = mapOfGraphEdge;
		VertexMap = mapOfGraphVertex;
		PureGrap = puregraph;
	}

	public TopologyGraph()
	{
		LinkMap = new TreeMap<Integer, Link>();
		VertexMap = new TreeMap<Integer, Vertex>();
	}
			
}
