package topology;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;
import common.FileGenerator;
import common.PrintConsole;
import dataStructure.Vertex;
import setting.ApplicationSettingFacade;




/** 7/16/2013
 * @author Mahdi Negahi
 * generate random position of routers 
 */
public class RandomTopology extends BaseTopology
{
	
	
	private static RandomTopology selfObj;
	/**
	 * @param minDistance : indicates the minimum distance of each router can have with other routers
	 * @param numberOfNodes : indicates the number of routers 
	 * @param tranmistionRate : each router can cover a specific distance around itself
	 * @param stest : when a point will generated, the point must have some conditions, if these conditions will failed 
	 * the application generate new point instead of the previous one, so <strong> safetyTest </strong> prevents to generating new point 
	 * for unlimited time.
	 */
	public static RandomTopology Initiate(int minimumDistance,int numberOfNodes, int tranmistionRate,long randSeed, int stest)
	{
		if(selfObj == null)
			selfObj = new RandomTopology(minimumDistance, numberOfNodes, tranmistionRate,randSeed, stest);
		return selfObj;
	}
	
	private static final int _environmentY = ApplicationSettingFacade.Environment.getHeight();
	private static final int _environmentX = ApplicationSettingFacade.Environment.getWidth();	
	private int numberOfRouters;	
	private int safetyTest;
	private long seed;
			
	
	private RandomTopology(int minimumDistance,int numberOfNodes, int tranmistionRate,long randSeed, int stest)
	{
		safetyTest = stest;
		numberOfRouters = numberOfNodes;
		seed = randSeed;
		super.transmissionRate = tranmistionRate;
		super.minDistance = minimumDistance;
		super.checkingTopology = new TopologyChecker(minimumDistance, tranmistionRate);
		initializeGatewayLocationSet();
	}
	
	
	
	
	@Override
	public Map<Integer, Vertex> CreateTopology()
	{
		if(generateRandomPosition())
		{
			
			gatewaySet = ApplicationSettingFacade.Gateway.getGateway();
			routerSet = generatingRouterSetsWithID(routerLocationSet);	
			neighbors = NodesNeighbors.getNeighbors(nodeSet, transmissionRate);
			FileGenerator.NodesInFile(nodeSet);		
			return nodeSet;
		}
		return null;
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
			newRouter = new Vertex(NODEID,  item.getValue());
			routers.put(newRouter.getId(),  newRouter);
			nodeSet.put(newRouter.getId(),  newRouter);
			NODEID++;
		}
		PrintConsole.print("Routers Id: ["+firstRouterId+","+ String.valueOf(NODEID-1) + "]");
		return routers;
	}
	
	private boolean generateRandomPosition()
	{
		
		
		long lSeed = this.seed;

		Random rand = new Random();
		int x;
		int y;
		int numNode = 0;
		int numberOfTest = 0;
		while(numberOfTest < this.safetyTest)
		{
			rand.setSeed(lSeed);
			while(numNode < this.numberOfRouters )
			{
				x = Math.abs(rand.nextInt(_environmentX));
				y = Math.abs(rand.nextInt(_environmentY));
				if(addInRouterLocationSet(x, y))
					numNode++;	
				
			}
			if(checkingTopology.isAllRoutersAccessibleFromGateway(routerLocationSet, gatewayLocationSet))
			{
				System.out.println("The topology generate based on random method (the seed is = " + lSeed);
				return true;
			}
			else
			{
				numberOfTest++;
				lSeed = System.nanoTime();
				if(numberOfTest >= this.safetyTest)
				{
					PrintConsole.printErr("FAIL: Program cannot generate " + numberOfRouters + 
							" nodes which each of them far away from others at least " + minDistance + " meter" );
					System.exit(0);

				}
				routerLocationSet.clear();
				numNode = 0;
			}
		}
		return false;
	}
	private boolean addInRouterLocationSet(int x, int y)
	{
		
		Point newPoint = new Point(x, y);
		String mapKey = String.valueOf(x) + String.valueOf(y);	
	
		if(! checkingTopology.isDuplicatedPoint(x, y, routerLocationSet, gatewayLocationSet) 
				&& checkingTopology.isPointFarEnoughFromOthers(x, y, routerLocationSet))
		{
			routerLocationSet.put(mapKey, newPoint);
			return true;
		}
		return false;
	}
	private void initializeGatewayLocationSet()
	{
		Map<Integer,Vertex> gateways = ApplicationSettingFacade.Gateway.getGateway();
		gatewayLocationSet = new HashMap<String, Point>();
		for(Vertex node : gateways.values())
		{
			String mapKey = String.valueOf(node.getLocation().x) + String.valueOf(node.getLocation().y);
			gatewayLocationSet.put(mapKey, node.getLocation());
		}
		
	}
}
