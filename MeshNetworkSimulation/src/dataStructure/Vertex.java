package dataStructure;

import java.awt.Point;

public class Vertex implements Comparable<Vertex>
{

	private final Integer id;

	public Integer getId()
	{
		return id;
	}

	public Point getLocation()
	{
		return location;
	}

	public final Point location;

	public Vertex(Integer ID, Point Location)
	{
		id = ID;
		location = Location;
	}

	public String toString()
	{
		return   location.x + "," + location.y + " " + id;
	}

	@Override
	public int hashCode()
	{
		final int prime = 997 ;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Vertex o)
	{
		return Integer.compare(id, o.id);

	}
	
	

}
