package trafficEstimating;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import common.FileGenerator;
import setting.ApplicationSettingFacade;
import topology2graph.TopologyGraphFacade;
import trafficGenerator.DTGFacade;
import dataStructure.BufferMap;
import dataStructure.DownlinkTraffic;
import dataStructure.LinkType;
import dataStructure.Packet;
import dataStructure.Path;
import dataStructure.TopologyGraph;
import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.PathMap;
import dataStructure.Traffic;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;

public class TrafficEstimatingFacade
{
	private static TrafficEstimatingFacade self;
	
	private static List<Link> optimalLinks; 
	
	
	private static DTGFacade dyTraffic = DTGFacade.Initilization();
	
	
	public static BufferMap getSourceBuffers(int currentTimeslot)
	{
		
		BufferMap bfMap = new BufferMap();
		
		
		PathMap uplinks =   TopologyGraphFacade.getOptimalUplinkPaths();
		
		UplinkTraffic uplinkTraffic = null;
		if(!ApplicationSettingFacade.Traffic.isDynamicType())
			uplinkTraffic = trafficGenerator.StaticTraffic.getUplinkTraffic(uplinks);
		else
			uplinkTraffic = dyTraffic.getUplinkTraffic(); 

		
		PathMap downlinkPaths = TopologyGraphFacade.getOptimalDownLinkPath();
		DownlinkTraffic downlinkTraffic = null;
		if(!ApplicationSettingFacade.Traffic.isDynamicType())
			downlinkTraffic = trafficGenerator.StaticTraffic.getDownlinkTraffic(downlinkPaths);
		else
			downlinkTraffic = dyTraffic.getDownlink(); 
		
		
		for (Entry<Integer, Vertex> vertexList : ApplicationSettingFacade.Nodes.getNodes().entrySet())
		{
			
			Vertex v = vertexList.getValue();
			
			if(uplinkTraffic.hasUplinkTraffic(v))
			{
				double upTraffic = uplinkTraffic.getUplinkTraffic(v);
				
				for (Path p : uplinks.get(v))
				{		
					Packet newPacket = new Packet(p, upTraffic, currentTimeslot);
					bfMap.put( p.getEdgePath().getFirst(), newPacket);
				}
			}
			
			if(downlinkTraffic.hasTraffic(v))
			{
				for (Path p : downlinkPaths.get(v))
				{
					double downTraffic = downlinkTraffic.getTraffic(p.getSource(), p.getDestination());
					Packet newPacket = new Packet(p, downTraffic, currentTimeslot);
					bfMap.put( p.getEdgePath().getFirst(), newPacket);
				}

			}
		}
		return bfMap;
	
		
	}

	public static int totalGeneratedPackets = 0;
	public static BufferMap getDynamicSourceBuffers(int currentTimeslot) 
	{
		
		
		PathMap uplinks = TopologyGraphFacade.getOptimalUplinkPaths();
		PathMap downlinks = TopologyGraphFacade.getOptimalDownLinkPath();
		
		

		
		BufferMap bfMap = null;
		//Creating a new BufferMap if the current one is null
	

		//Initializing
		Traffic globalTraffic = dyTraffic.getDynamicTraffic(currentTimeslot);
		
		UplinkTraffic uplinkTraffic = globalTraffic.getUplinkTraffic();
		DownlinkTraffic downlinkTraffic = globalTraffic.getDownlinkTraffic();
		Map<Integer, Vertex> nodesMap = ApplicationSettingFacade.Nodes.getNodes();
		
		if(uplinkTraffic.size() > 0 || downlinkTraffic.size() > 0)
		{
			bfMap = new BufferMap();
			for (int vertexIndex : nodesMap.keySet()) {
			Vertex v = nodesMap.get(vertexIndex);
			// Add uplink traffic if there is
			if(uplinkTraffic.hasUplinkTraffic(v)) 
			{
				double upTraffic = uplinkTraffic.getUplinkTraffic(v);
				
				if(upTraffic > 0)
				{
					List<Path> pathList = uplinks.get(v);
					upTraffic = upTraffic / pathList.size();
					for (Path p : pathList) 
					{
						Packet newPacket = new Packet(p, upTraffic, currentTimeslot);
						bfMap.put( p.getEdgePath().getFirst(), newPacket);	
						totalGeneratedPackets++;
					}
				}
			}
			// Add downlink traffic if there is
			if(downlinkTraffic.hasTraffic(v))
			{
				
				List<Path> dPath = downlinks.get(v);
				
				for (Path p : dPath)
				{
						double downTraffic = downlinkTraffic.getTraffic(p.getSource(), p.getDestination());
						if(downTraffic > 0) 
						{
	
							Packet newPacket = new Packet(p, downTraffic, currentTimeslot);
							bfMap.put( p.getEdgePath().getFirst(), newPacket);
							totalGeneratedPackets++;
						}
					}
				}
			}
		}
			
		return bfMap;
	}
	

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
	
	
	private static int _startTime = -1;
	public static LinkTrafficMap getLinksTraffic(int startTime, int stopTime, BufferMap sourceBuffer, BufferMap transmitBuffer)
	{
		if(_startTime != startTime)
		{
			self = new TrafficEstimatingFacade();
			LinksTraffic = self.dynamicEstimating(startTime,stopTime,sourceBuffer,transmitBuffer);
			_startTime = startTime;
		}
		return LinksTraffic;
	}

	private LinkTrafficMap dynamicEstimating(int startTime, int stopTime, BufferMap sourceBuffer, BufferMap transmitBuffer)
	{
		TopologyGraphFacade.buildGraphFromTopology();		
		//create map for all links
		
		
	
		LinkTrafficMap dl_traffic_l  = DownlinkEstimating.dynamicEstimating(startTime,stopTime,sourceBuffer,transmitBuffer);
		LinkTrafficMap traffic_l = UplinkEstimating.dynamicEstimating(startTime,stopTime,sourceBuffer,transmitBuffer,dl_traffic_l);
		
		traffic_l = this.filterZeroTraffic(traffic_l);
		FileGenerator.TrafficOfLinksInFile(traffic_l, startTime);
		return traffic_l;
	}
	private LinkTrafficMap Estimating()
	{
			
		TopologyGraph gtd = TopologyGraphFacade.buildGraphFromTopology();		
		LinkTrafficMap trafficOfLink = Initialization(gtd);
		
		
		DownlinkEstimating downlinkEstimatingTraffic = DownlinkEstimating.Initiate(trafficOfLink);
		LinkTrafficMap downlink_taffic_l  = downlinkEstimatingTraffic.estimating();
		
		
		
		
		UplinkEstimating uplinkEstimating = new UplinkEstimating(downlink_taffic_l);
		
		LinkTrafficMap tarrfic_l = uplinkEstimating.estimating();
		
		
		

		
		tarrfic_l = this.filterZeroTraffic(tarrfic_l);
		
		FileGenerator.TrafficOfLinksInFile(tarrfic_l);
		
		return tarrfic_l;
		
		
		
	}
	private LinkTrafficMap filterZeroTraffic(LinkTrafficMap tarrfic_l)
	{
		LinkTrafficMap optimalTrafficLinks = new LinkTrafficMap();
		optimalLinks = new ArrayList<>();
		for (Entry<Link, Double> links : tarrfic_l.entrySet())
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
			TrafficOfLinks.put(item.getValue(), 0d);
		}
		return TrafficOfLinks;	
	}
}
