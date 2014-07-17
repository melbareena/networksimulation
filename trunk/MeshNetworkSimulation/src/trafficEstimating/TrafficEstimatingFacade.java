package trafficEstimating;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import common.FileGenerator;
import setting.ApplicationSettingFacade;
import topology2graph.TopologyGraphFacade;
import dataStructure.BufferMap;
import dataStructure.DownlinkTraffic;
import dataStructure.LinkType;
import dataStructure.Packet;
import dataStructure.Path;
import dataStructure.TopologyGraph;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.PathMap;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;

public class TrafficEstimatingFacade
{
	private static TrafficEstimatingFacade self;
	
	
	private static PathMap optimalUplinkPath;
	private static PathMap downlinkPath;
	private static List<Link> optimalLinks; 
	
	
	public static PathMap getOptimalUplinkPath()
	{
		if(self == null)
		{
			self = new TrafficEstimatingFacade();
			LinksTraffic = self.Estimating();
		}
		return optimalUplinkPath;
	}
	public static PathMap getDownlinkPath()
	{
		if(self == null)
		{
			self = new TrafficEstimatingFacade();
			LinksTraffic = self.Estimating();
		}
		return downlinkPath;
	}
	
	
	
	/*private static void addToBuffers(Vertex v , BufferSet bs)
	{
		if(!buffers.containsKey(v))
			buffers.put(v, bs);
		else
		{
			BufferSet storedBS = buffers.get(v);
			storedBS.Append(bs);
		}
	}*/
	
	
	public static BufferMap getSourceBuffers()
	{
		
		BufferMap bfMap = new BufferMap();
		UplinkTraffic uplinkTraffic = trafficGenerator.StaticTraffic.getUplinkTraffic();
		DownlinkTraffic downlinkTraffic = trafficGenerator.StaticTraffic.getDownlinkTraffic();
		PathMap uplinks = getOptimalUplinkPath();
		PathMap downlinkPaths = getDownlinkPath();
		for (Entry<Integer, Vertex> vertexList : ApplicationSettingFacade.Nodes.getNodes().entrySet())
		{
			
			Vertex v = vertexList.getValue();
			
			if(uplinkTraffic.hasUplinkTraffic(v))
			{
				int upTraffic = uplinkTraffic.getUplinkTraffic(v);
				
				for (Path p : uplinks.get(v))
				{		
					Packet newPacket = new Packet(p, upTraffic);
					bfMap.put( p.getEdgePath().getFirst(), newPacket);
				}
			}
			
			if(downlinkTraffic.hasDownLinkTraffic(v))
			{
				for (Path p : downlinkPaths.get(v))
				{
					float downTraffic = downlinkTraffic.getDownLinkTraffic(p.getSource(), p.getDestination());
					Packet newPacket = new Packet(p, downTraffic);
					bfMap.put( p.getEdgePath().getFirst(), newPacket);
				}

			}
		}
		//summation(bfMap);
		return bfMap;
	
		
	}
	
	/*private static void summation(BufferMap bfMap)
	{
		double allTraffic = 0;
		for (Entry<Link, Buffer> lb : bfMap.sort().entrySet())
		{
			allTraffic += lb.getValue().size();
			System.out.println(lb.getKey().getId() + "--->" + lb.getValue().size());
		}
		
		System.out.println(allTraffic + " ..... ");
		
	}*/
	public static List<Link> getOptimalLinks()
	{
		if(self == null)
		{
			self = new TrafficEstimatingFacade();
			LinksTraffic = self.Estimating();
		}
		return optimalLinks;
	}
	public static List<Link> getOptimalLinks(Vertex v, LinkType lt)
	{
		List<Link> result = new ArrayList<>();
		
		List<Link> optimalLink = getOptimalLinks();
		Vector<Link> links = null;
		if(lt == LinkType.Incoming)
			links = TopologyGraphFacade.getIncomingLinks().get(v);
		else
			links = TopologyGraphFacade.getOutcomingLinks().get(v);
		
		for (Link link : links)
		{
			if(optimalLink.contains(link))
				result.add(link);
		}
		
		return result;
		
	}
	private static LinkTrafficMap LinksTraffic;
	
	
	
	
	/**
	 * @return return traffic of each link (TRAFFIC_l)
	 */
	public static LinkTrafficMap getLinksTraffic()
	{
		if(self == null)
		{
			self = new TrafficEstimatingFacade();
			LinksTraffic = self.Estimating();
		}
		return LinksTraffic;
	}
	
	
	private LinkTrafficMap Estimating()
	{
		TopologyGraph gtd = TopologyGraphFacade.buildGraphFromTopology();
		
		LinkTrafficMap trafficOfLink = Initialization(gtd);
		
		
		DownlinkEstimating downlinkEstimatingTraffic = DownlinkEstimating.Initiate(trafficOfLink);
		
		LinkTrafficMap downlink_taffic_l  = downlinkEstimatingTraffic.estimating(gtd);
		
		
		downlinkPath = downlinkEstimatingTraffic.getDownlinkPaths(); 
		
		UplinkEstimating uplinkEstimating = new UplinkEstimating(downlink_taffic_l, gtd);
		
		LinkTrafficMap tarrfic_l = uplinkEstimating.estimating();
		
		optimalUplinkPath = uplinkEstimating.getOptimalUplinkPaths();
		
	
		
		tarrfic_l = this.filterZeroTraffic(tarrfic_l);
		
		FileGenerator.TrafficOfLinksInFile(tarrfic_l);
		
		return tarrfic_l;
		
		
		
	}
	private LinkTrafficMap filterZeroTraffic(LinkTrafficMap tarrfic_l)
	{
		LinkTrafficMap optimalTrafficLinks = new LinkTrafficMap();
		optimalLinks = new ArrayList<>();
		for (Entry<Link, Float> links : tarrfic_l.entrySet())
		{
			if(links.getValue() > 0)
			{
				optimalLinks.add(links.getKey());
				optimalTrafficLinks.put(links.getKey(),links.getValue());
			}
		}
		FileGenerator.optimalLinksInFile(optimalLinks);
		return optimalTrafficLinks;
	}
	
	private LinkTrafficMap Initialization(TopologyGraph gtd)
	{
		LinkTrafficMap TrafficOfLinks = new LinkTrafficMap();
		for (Entry<Integer, Link> item : gtd.LinkMap.entrySet())
		{
			TrafficOfLinks.put(item.getValue(), 0f);
		}
		return TrafficOfLinks;	
	}
}
