package topology;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import dataStructure.Vertex;

public abstract class BaseTopology
{
	public BaseTopology(){}
	protected int transmissionRate ;
	protected int minDistance;
	
	
	protected Map<Vertex, Vector<Vertex>> neighbors;
	
	
	public Map<Vertex, Vector<Vertex>> getNeighbors()
	{
		return neighbors;
	}
	protected Map<Integer, Vertex> gatewaySet;
	protected Map<Integer, Vertex> routerSet;
	
	public Map<Integer, Vertex> getRouterSet()
	{
		return routerSet;
	}
	protected Map<Integer, Vertex> getGatewaySet()
	{
		return gatewaySet;
	}

	
	protected Map<Integer, Vertex> nodeSet =  new TreeMap<Integer, Vertex>();
	protected Map<Integer, Vertex> getNodeSet()
	{
		return nodeSet;
	}
	protected TopologyChecker checkingTopology;
	
	/**
	 * before finalizing of routers position, we maintain all routers on this structure, 
	 * this pair key/value help to checking duplicated point is so easily.
	 * the key of map is obtained form connecting x and y as a single string 
	 * for example if there is point with following x and y
	 * x= 105 
	 * y= 123
	 * the key is "105123"
	 */
	protected Map<String, Point> routerLocationSet = new HashMap<String, Point>();
	protected Map<String, Point> gatewayLocationSet;
	
	/**
	 * @return all nodes ( routers + gateway ) which is generated randomly.
	 */
	public abstract Map<Integer, Vertex> CreateTopology();

}
