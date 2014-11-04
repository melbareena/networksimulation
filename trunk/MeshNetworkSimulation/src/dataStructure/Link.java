package dataStructure;


public class Link implements Comparable<Link>
{
	private final Integer id;
	private final Vertex source;
	private final Vertex destination;
	private final int weight;

	public Link(int id, Vertex source, Vertex destination, int weight)
	{
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}

	public int getId()
	{
		return id;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 3571 ;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	public Vertex getDestination()
	{
		return destination;
	}

	public Vertex getSource()
	{
		return source;
	}

	public int getWeight()
	{
		return weight;
	}

	@Override
	public String toString()
	{
		return id +"[" + source + "-> " + destination + "]";
	}

	public double getDistance()
	{
		 return source.getDistance(destination);
	}
	
	/**
	 * 
	 * @param l: link (i)
	 * @return cross distance between current object (j) and other link (j)
	 */
	public double getCrossDistance(Link l)
	{
		 return source.getDistance(l.getDestination());
	}
	
	@Override
	public int compareTo(Link o)
	{
		return Integer.compare(id, o.id);
	}
	@Override
	public boolean equals(Object obj)
	{
		  if (obj == null || obj.getClass() != this.getClass())
	            return false;
		  else if( ((Link)obj).getId() == this.getId())
			  return true;
		return false;
	}
}
