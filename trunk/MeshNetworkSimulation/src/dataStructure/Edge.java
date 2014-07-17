package dataStructure;

public class Edge
{
	private Link UpLink;
	private Link DownLink;
	
	public Edge(Link uplink, Link downlink)
	{
		this.UpLink = uplink;
		this.DownLink = downlink;
	}

	public Link getUpLink()
	{
		return UpLink;
	}

	public Link getDownLink()
	{
		return DownLink;
	}
}
