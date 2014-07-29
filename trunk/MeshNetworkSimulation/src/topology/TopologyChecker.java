package topology;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;

import common.PrintConsole;

public class TopologyChecker
{
	private static int ACCESSIBLE = 0;
	private int _minDistance;
	private int _transmissionRate ;
	private Map<String, Point> _markedRouters =  new HashMap<String, Point>();
	public TopologyChecker (int minDistance, int transmitionRate)
	{
		_minDistance = minDistance;
		_transmissionRate = transmitionRate;
	}
	
	
	public boolean hasDuplicatedPoint(Map<String, Point> routersLocations, Map<String, Point> gateway)
	{
		for(Entry<String,Point> item : routersLocations.entrySet())
		{
			if(gateway.containsKey(item.getKey()))
			{
				if(ApplicationSettingFacade.AppOutput.showIntermediateOutput())
					PrintConsole.printErr("FAIL: Duplicated Point (X:" + item.getValue().x + " Y:" +  item.getValue().y + ")");
				return true;
			}
		}
		PrintConsole.print("PASS: There is no duplicated nodes' position.");
		return false;
	}
	public boolean isDuplicatedPoint(int x , int y, Map<String, Point> routersLocations, Map<String, Point> gatewayLocation)
	{
		String key = String.valueOf(x) + y;
		
		if(routersLocations.containsKey(key) || gatewayLocation.containsKey(key))
		{
			if(ApplicationSettingFacade.AppOutput.showIntermediateOutput())
				PrintConsole.printErr("Reject: Duplicated Point (X:" + x + " Y:" + y + ")");
			return true;
		}
			
		return false;
	}
	
	public boolean isPointFarEnoughFromOthers(int x, int y, Map<String, Point> routersLocations)
	{
		Point p1 = new Point(x,y);
		Point p2;
		for(Entry<String,Point> item : routersLocations.entrySet())
		{
			
			p2 = item.getValue();
			double dist = getDistance(p1, p2);
			
			if(dist < _minDistance)
			{
				if(ApplicationSettingFacade.AppOutput.showIntermediateOutput())
					PrintConsole.printErr("Reject: Point (X:" + x + 
							" Y:" + y + ") becuase of its distance (" + dist + ") " +
							"with point (X:" + p2.x + " Y:" + p2.y +").");
			
				return false;
			}
		}
		return true;
	}
	public boolean isPointFarEnoughFromOthers(Map<String, Point> routersLocations)
	{
		Point p1;
		Point p2;
		for(Entry<String,Point> node1 : routersLocations.entrySet())
		{
			p1 = node1.getValue();
			for(Entry<String,Point> node2 : routersLocations.entrySet())
			{
				p2 = node2.getValue();
				
				if(node1.getKey() != node2.getKey())
				{
					double dist = getDistance(p1, p2);
					
					if(dist < _minDistance)
					{
						if(ApplicationSettingFacade.AppOutput.showIntermediateOutput())
							PrintConsole.printErr("FAIL: Point (X:" + p1.x + 
									" Y:" + p1.y + ") becuase of its distance (" + dist + ") " +
									"with the point (X:" + p2.x + " Y:" + p2.y +").");
					
						return false;
					}
				}
			}
		}
		return true;
	}


	
	
	public boolean isAllRoutersAccessibleFromGateway( Map<String, Point> routersLocations,
			Map<String, Point> gatewayLocations)
	{
		
		
		PrintConsole.print("Checking all routers are accessible from at least one getway.");
		
		Stack<String> getwayNeighborsStack = new Stack<>();
		for(Entry<String, Point> getway : gatewayLocations.entrySet() )
		{
			
			ACCESSIBLE = 0;
			for(Entry<String, Point> router : routersLocations.entrySet())
			{
				if(isInTranssmisonRateRate(getway.getValue(), router.getValue()) 
						&& ! _markedRouters.containsKey(router.getKey()) )
				{							
					_markedRouters.put(router.getKey(), router.getValue());
					ACCESSIBLE++;
					getwayNeighborsStack.push(router.getKey());
				}
			}
			
			if(getwayNeighborsStack.size() > 0)
				findMarkedRouterNeighbors(getwayNeighborsStack, routersLocations);
			
				PrintConsole.print("The number of accessible router from the getway " +
						"<"+getway.getValue().x+", "+getway.getValue().y +
						"> is: " + ACCESSIBLE);
			
		}
		
		if(routersLocations.size() != _markedRouters.size())
		{
			printIsolatedRouters(routersLocations);
			return false;
		}
		PrintConsole.print("PASS: all routers are accessible from atleast one gateway");
		return true;
		
		
	}
	private void printIsolatedRouters(Map<String, Point> routersLocations)
	{
		PrintConsole.printErr("FAIL: following routers are isolated.");
		for(Entry<String, Point> router : routersLocations.entrySet())
		{
			if(! _markedRouters.containsKey(router.getKey()))
			{
				PrintConsole.printErr("The node < "+router.getValue().x
						+ "," + router.getValue().y + " > is isolated");
			}
		}
		
	}
	private void findMarkedRouterNeighbors(Stack<String> neighbors , Map<String, Point> routersLocations) {
		
		 Stack<String> NeighborsStack = new Stack<>();
		 
		 for(String parentId : neighbors)
		 {
			 
			 Point parentPoint = routersLocations.get(parentId);
			 for(Entry<String, Point> router : routersLocations.entrySet())
			 {
				 if(isInTranssmisonRateRate(parentPoint , router.getValue()))
				 {
				 	if(! _markedRouters.containsKey(router.getKey()))
					{
				 		_markedRouters.put(router.getKey(), router.getValue());
						ACCESSIBLE ++;
						NeighborsStack.push(router.getKey());
					}
				}
			 }
				
			 if(NeighborsStack.size() > 0)
				findMarkedRouterNeighbors(NeighborsStack, routersLocations);
		 }	
	}
	private boolean isInTranssmisonRateRate(Point p1, Point p2)
	{
		double distance = Math.sqrt(Math.pow((p1.x-p2.x), 2) + Math.pow((p1.y-p2.y), 2));
		if(distance <= _transmissionRate)
			return true;
		return false;
	}
	private double getDistance(Point p1, Point p2)
	{
		return Math.sqrt(Math.pow((p1.x-p2.x), 2) + Math.pow((p1.y-p2.y), 2));
	}
}
