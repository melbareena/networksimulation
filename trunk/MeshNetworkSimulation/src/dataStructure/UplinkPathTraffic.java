package dataStructure;

import java.util.TreeSet;




/**
 * @author Mahdi
 * traffic of uplink path is equal with the traffic of uplink link which is participating in 
 * <strong>bottleneck edge</strong> in the path.
 */
public class UplinkPathTraffic implements Comparable<UplinkPathTraffic>
{
	Path path;
	Edge BottleNeckEdge;
	Link bottleNeckLink;
	float TrafficOfPath;
	double percentage;

	public TreeSet<Float> linksTraffic;
	
	


	/**
	 * @param p : is a path
	 * @param e : bottleneck edge
	 * @param tarfficOfPath : the traffic of uplink link which is participating in bottleneck edge
	 */
	public UplinkPathTraffic(Path p, Edge e, float tarfficOfPath, TreeSet<Float> linkstraffic)
	{
		this.path = p;
		this.BottleNeckEdge = e;
		this.TrafficOfPath = tarfficOfPath;
		linksTraffic = linkstraffic;
	}
	public UplinkPathTraffic(Path p, Edge e, float tarfficOfPath)
	{
		this.path = p;
		this.BottleNeckEdge = e;
		this.TrafficOfPath = tarfficOfPath;
		linksTraffic = new TreeSet<>();
	}
	public UplinkPathTraffic(Path p, Link l, float tarfficOfPath, TreeSet<Float> linkstraffic)
	{
		this.path = p;
		this.bottleNeckLink = l;
		this.TrafficOfPath = tarfficOfPath;
		linksTraffic = linkstraffic;
	}

	
	
	public Link getBottleNeckLink()
	{
		return bottleNeckLink;
	}
	
	public Path getPath()
	{
		return path;
	}


	public Edge getBottleNeckEdge()
	{
		return BottleNeckEdge;
	}

	/**
	 * @return the amount of uplink link which it is participating in bottleneck edge.
	 */
	public float getPathTraffic()
	{
		return TrafficOfPath;
	}


	public void setPathTraffic(float traffic)
	{
		this.TrafficOfPath = traffic;
	}


	@Override
	public int compareTo(UplinkPathTraffic o)
	{
		return Float.compare(this.getPathTraffic(), o.getPathTraffic());
	}
	public String toString()
	{
		return   "Path: " + path + "\n\t --- Max Edge Tarffic: " + TrafficOfPath + "\n\t --- percentage:" + this.percentage + "\n";
	}
	public double getPercentage()
	{
		return percentage;
	}


	public void setPercentage(double d)
	{
		this.percentage = d;
	}
}
