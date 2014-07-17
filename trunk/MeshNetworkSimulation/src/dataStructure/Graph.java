package dataStructure;

import java.util.List;



public class Graph
{
	private final List<Vertex> vertexes;
	private final List<Link> links;

	public Graph(List<Vertex> vertexes, List<Link> edges)
	{
		this.vertexes = vertexes;
		this.links = edges;
	}

	public List<Vertex> getVertexes()
	{
		return vertexes;
	}

	public List<Link> getEdges()
	{
		return links;
	}
}
