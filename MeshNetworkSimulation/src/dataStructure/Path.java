package dataStructure;
import java.util.LinkedList;



/**
 * @author Mahdi
 * Demonstrate a path which refer to shortest path in graph
 */
public class Path implements Comparable<Path>
{
	
	
	private Integer Id;
	public Integer getId()
	{
		return Id;
	}
	public Path(Vertex source, Vertex destination,int Id)
	{
		this.source = source;
		this.destination = destination;
		this.Id = Id;
	}
	private Vertex source;
	private Vertex destination;
	
	
	/**
	 * refers a linked list which includes nodes' id in the path. 
	 */
	private LinkedList<Vertex> nodePath;

	/**
	 * refers a linked list which includes edges' id in the path. 
	 */
	private LinkedList<Link> edgePath;
	
	public Link getNextLink(Link currentLink)
	{
		LinkedList<Link> links = this.getEdgePath();
		int index = -1;
		for (Link link : links)
		{
			index++;
			if(currentLink == link)
				break;
			
				
		}
		index++;
		return links.get(index);
	}
	
	
	// bug here , if we received to the end point
	public Vertex getNextNode(Vertex currentNode)
	{
		
		LinkedList<Vertex> nodes = this.getNodePath();
		
		int index = -1;
		for (Vertex node : nodes)
		{
			index++;
			if(node == currentNode)
				break;
			
				
		}
		
		index++;
		return nodes.get(index);
	}
	public Vertex getSource()
	{
		return source;
	}
	public void setSource(Vertex source)
	{
		this.source = source;
	}
	public Vertex getDestination()
	{
		return destination;
	}
	public void setDestination(Vertex target)
	{
		this.destination = target;
	}
	public LinkedList<Vertex> getNodePath()
	{
		return nodePath;
	}
	public void setNodePath(LinkedList<Vertex> nodePath)
	{
		this.nodePath = nodePath;
	}
	public LinkedList<Link> getEdgePath()
	{
		return edgePath;
	}
	public void setEdgePath(LinkedList<Link> edgePath)
	{
		this.edgePath = edgePath;
	}
	
	public void addNode(Vertex v)
	{
		if(nodePath == null)
			nodePath = new LinkedList<>();
		nodePath.add(v);
	}
	
	
	@Override
	public int compareTo(Path o)
	{
		if(this.nodePath != null && o.getNodePath() != null)
		{
			return Integer.compare(this.nodePath.size(), o.getNodePath().size());
		}
		return (Integer) null;
	}	
	
	@Override
	public int hashCode()
	{
		final int prime = 929;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		return result;
	}
	
	
	@Override
	public String toString()
	{
		String out = "Path from " +  this.source.getId() + " to " + this.destination.getId() + "\n";
		
		if(nodePath != null && nodePath.size() > 0)
		{
			for(Vertex n : nodePath)
			{
				out += "n(" + n.getId() + ")" + " > ";
			}
			out = out.substring(0, out.length() - 3);
		}
		
		out += "\n";
		if(edgePath != null && edgePath.size() > 0)
		{
			
			for(Link e : edgePath)
			{
				out += "l(" + e.getId() + ")" + " > ";
			}
			out = out.substring(0, out.length() - 3);
		}
		out += "\n";
		return out;
		
		
	}
	
	
	public String toString(boolean edge, boolean node)
	{
		String out = "Path from " +  this.source.getId() + " to " + this.destination.getId() + "\n";
		
	
		
		if(nodePath != null && nodePath.size() > 0 && node)
		{
			for(Vertex n : nodePath)
			{
				out += "n(" + n.getId() + ")" + " > ";
			}
			out = out.substring(0, out.length() - 3);
		}
		if(edgePath != null && edgePath.size() > 0 && edge)
		{
			
			for(Link e : edgePath)
			{
				out += "l(" + e.getId() + ")" + " > ";
			}
			out = out.substring(0, out.length() - 3);
		}
		return out;
		
		
	}
	
	public Path Clone()
	{
		Path p = new Path(this.getSource(), this.getDestination(), this.getId());
		LinkedList<Vertex> nodesList = new LinkedList<>();
		LinkedList<Link> linkList = new LinkedList<>();
		
		
		for (Vertex v : this.getNodePath())
			nodesList.add(v);
		
		for (Link l : this.getEdgePath())
			linkList.add(l);
		
		p.setEdgePath(linkList);
		p.setNodePath(nodesList);
		
		return p;
	}
	
}
