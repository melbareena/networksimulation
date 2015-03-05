package dataStructure;

import java.util.LinkedList;

public class Packet implements Comparable<Packet>
{
	private Vertex destination;
	private Vertex source;
	private LinkedList<Link> packetPath;
	private Vertex currentNode;
	private double traffic;
	private int dateOfBirth;
	private int dateOfDeath;
	
	private boolean isOriginal;
	
	private boolean isReceived;
	private Path orginalPath;
	
	public Packet(Path path, double traffic, int currentTimeSlot)
	{
		this.destination = path.getDestination();
		this.source = path.getSource();
		this.packetPath = path.Clone().getEdgePath();
		this.currentNode = source;
		this.isReceived = false;
		this.isOriginal = true;
		this.traffic = traffic;

		this.orginalPath = path;
		
		this.dateOfBirth = currentTimeSlot;
		this.dateOfDeath = 0;
	}
	
	/**
	 * create a packet which is a part of a bigger packet
	 * @param path
	 * @param currentNode
	 * @param packetPathLinks
	 * @param traffic
	 * @param currentTimeSlot
	 */
	public Packet(Path path,Vertex currentNode, LinkedList<Link> packetPathLinks, double traffic,
			int currentTimeSlot)
	{
		this.destination = path.getDestination();
		this.source = path.getSource();
		this.traffic = traffic;

		this.packetPath = packetPathLinks;
		this.orginalPath = path;
		
		this.currentNode = currentNode;
		
		this.dateOfBirth = currentTimeSlot;
		
		this.isOriginal = false;
		
		if(currentNode == this.destination) {
			this.isReceived = true;
			this.dateOfDeath = currentTimeSlot;
		}
	}
	
	
	public Path getOriginalPath()
	{
		return this.orginalPath;
	}
	
	public int getDelay()
	{
		return isReceived ? Math.max( ((dateOfDeath - dateOfBirth ) - orginalPath.getEdgePath().size()), 0) : 0;
	}
	
	public double getTraffic()
	{
		return traffic;
	}
	public Vertex getDestination()
	{
		return destination;
	}
	public Vertex getSource()
	{
		return source;
	}
	public LinkedList<Link> getPacketPath()
	{
		return packetPath;
	}
	public Vertex getCurrentNode()
	{
		return currentNode;
	}
	public boolean isReceived()
	{
		return isReceived;
	}
	public boolean isOrginalPacket()
	{
		return isOriginal;
	}
	public Link getCurrentLink()
	{
		if(packetPath.size() > 0)
			return packetPath.getFirst();
		return null;
	}
	
	public void addTarffic(double value)
	{
		this.traffic += value;
	}
	public void reduceTarffic (double value)
	{
		this.traffic -= value;
	}
	
	public Packet send(double dataRate, int currentTimeSlot)
	{
		if(!this.isReceived)
		{
			if(dataRate > this.traffic)
			{
				Link l = packetPath.remove();
				this.currentNode = l.getDestination();
				if(this.currentNode == this.destination) {
					this.isReceived = true;
					this.dateOfDeath = currentTimeSlot;
				}
				return this;
			}
			this.traffic -= dataRate;
			
	
			@SuppressWarnings("unchecked")
			LinkedList<Link> sentPacketPath = (LinkedList<Link>) this.packetPath.clone();
			Link l = sentPacketPath.remove();
			
			Packet sentPacket = new Packet(this.orginalPath ,l.getDestination(), sentPacketPath, dataRate, currentTimeSlot);
			return sentPacket;
				
		}
		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null) return false;
	    if (o == this) return true;
	    if (!(o instanceof Packet))return false;
	    Packet p = (Packet)o;
		if(		this.source == p.getSource() 
				&& this.destination == p.getDestination() 
				&& this.getOriginalPath() == p.getOriginalPath() && this.dateOfBirth == p.dateOfBirth)
			return true;
		return false;
	}
	
	@Override
	public int compareTo(Packet o)
	{
		if(this.equals(o)) return 0;
			return Double.compare(this.traffic, o.getTraffic() );
	}
	
	@Override
	public String toString() 
	{
		String out = "S:" + this.getSource().getId() + ",D:" + this.getDestination().getId();
		if(!isReceived)
			 out += ",CL:" + getCurrentLink().getId();
		else
			out += ",CL: Reached";
		
		out += ", Traffic:" + this.getTraffic();
		
		return out;
	}
	
	
}
