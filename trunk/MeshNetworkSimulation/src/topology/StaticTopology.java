package topology;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import common.FileGenerator;
import common.PrintConsole;
import dataStructure.Vertex;

import setting.ApplicationSettingFacade;


public class StaticTopology extends BaseTopology
{
	
	private static StaticTopology selfObj;
	public static StaticTopology Initiate(int minimumDistance, int tranmistionRate, Vector<Point> routers)
	{
		if(selfObj == null)
			selfObj = new StaticTopology(minimumDistance,tranmistionRate, routers);
		return selfObj;
	}
	private Map<String, Point> gatewayLocation;
	private Map<String, Point> routerLocation;
	private StaticTopology(int minimumDistance, int tranmistionRate, Vector<Point> routers)
	{
		gatewayLocation =  initializeGatewayLocationSet();
		routerLocation = convertToLocationSet(routers);
		super.transmissionRate = tranmistionRate;
		super.minDistance = minimumDistance;
		super.checkingTopology = new TopologyChecker(minimumDistance, tranmistionRate);
	}
	
	@Override
	public Map<Integer, Vertex> CreateTopology()
	{
			
		if(checkingTopology.isAllRoutersAccessibleFromGateway(routerLocation, gatewayLocation) 
				&& checkingTopology.isPointFarEnoughFromOthers(routerLocation) 
				&& !checkingTopology.hasDuplicatedPoint(routerLocation, gatewayLocation))
		{
			gatewaySet = ApplicationSettingFacade.Gateway.getGateway();
			routerSet = generatingRouterSetsWithID(routerLocation);		
			neighbors = NodesNeighbors.getNeighbors(nodeSet, transmissionRate);
			FileGenerator.NodesInFile(nodeSet);	
			return nodeSet;
		}
		
		
		return null;
	}
	private Map<String,Point> convertToLocationSet(Vector<Point> routers)
	{
		Map<String, Point> routersLocation = new HashMap<String, Point>();
		
		String mapKey;
		for(Point p : routers)
		{
			
			mapKey = String.valueOf(p.x) + p.y;
			routersLocation.put(mapKey,p);
		}
		return routersLocation;
	}
	private Map<String, Point> initializeGatewayLocationSet()
	{
		Map<Integer,Vertex> gateways = ApplicationSettingFacade.Gateway.getGateway();
		Map<String, Point> gatewaylocationset= new HashMap<String, Point>();
		String mapKey;
		for(Vertex node : gateways.values())
		{
			mapKey = String.valueOf(node.getLocation().x) + String.valueOf(node.getLocation().y);
			gatewaylocationset.put(mapKey, node.getLocation());
		}
		return gatewaylocationset;
		
	}
	private Map<Integer, Vertex> generatingRouterSetsWithID(Map<String,Point> routersPoint)
	{
		Integer[] gatewayIds = ApplicationSettingFacade.Gateway.getGateway().keySet().toArray(new Integer[]{});	
		int NODEID = gatewayIds[gatewayIds.length-1];
		NODEID++;
		int firstRouterId = NODEID;
		
		nodeSet.putAll(ApplicationSettingFacade.Gateway.getGateway());
		Map<Integer, Vertex> routers =new TreeMap<Integer,Vertex>();
		
		Vertex newRouter;
		for(Entry<String, Point> item : routersPoint.entrySet())
		{
			newRouter = new Vertex(NODEID, item.getValue());
			routers.put(newRouter.getId(), newRouter);
			nodeSet.put(newRouter.getId(), newRouter);
			NODEID++;
		}
		PrintConsole.print("Routers Id: ["+firstRouterId+","+ String.valueOf(NODEID-1) + "]");
		return routers;
	}
	
}
