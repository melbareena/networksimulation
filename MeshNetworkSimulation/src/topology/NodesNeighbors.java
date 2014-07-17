package topology;

import java.awt.Point;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;

import common.FileGenerator;
import common.PrintConsole;
import dataStructure.Vertex;

class NodesNeighbors
{
	
	
	protected static Map<Vertex, Map<Vertex,Double>> getDistanceOfNodes()
	{
		Map<Vertex, Map<Vertex,Double>> output = new TreeMap<>();
		Map<Vertex,Double> distanceOfNode;
		 
		Map<Integer,Vertex> nodes1 = ApplicationSettingFacade.Nodes.getNodes();
		Map<Integer,Vertex> nodes2 = ApplicationSettingFacade.Nodes.getNodes();
		
		double dis = 0;
		Vertex node1 = null,node2 = null;
		for (Entry<Integer, Vertex> sourceNode : nodes1.entrySet())
		{
			node1 = sourceNode.getValue();
			distanceOfNode = new TreeMap<>();
			for (Entry<Integer, Vertex> targetNode : nodes2.entrySet())
			{
				node2 = targetNode.getValue();
				dis  = getDistance(node1.location, node2.location);
				distanceOfNode.put(node2, dis);			
			}
			output.put(node1, distanceOfNode);
		}		 
		return output;
	}
	



	protected static Map<Vertex,Vector<Vertex>> getNeighbors(Map<Integer,Vertex> nodes, int transmissionRate)
	{
		Map<Vertex,Vector<Vertex>> neighborSet = new TreeMap<Vertex, Vector<Vertex>>();
		Vector<Vertex> neighborsNodes;
		double distance = 0;
		try
		{
			for(Entry<Integer,Vertex> node1 : nodes.entrySet())
			{
				neighborsNodes = new Vector<Vertex>();
				for(Entry<Integer,Vertex> node2 : nodes.entrySet())
				{
					if(node1.getKey() != node2.getKey())
					{		
						distance = getDistance(node1.getValue().getLocation(), node2.getValue().getLocation());
						if(distance < transmissionRate)
							neighborsNodes.add(node2.getValue());
					}			
				}
				if(neighborsNodes.size() > 0)
					neighborSet.put(node1.getValue(), neighborsNodes);
				
				
			}
			
			FileGenerator.NeighborInFile(neighborSet);
			PrintConsole.print("Calculating neighbors is done successfully.");
			return neighborSet;
		
		}
		catch(Exception ex)
		{
			PrintConsole.printErr("Exception: GenerateTopolgy/calcNeighbors " + ex.getMessage());
		}
		return null;
	}


	private static double getDistance(Point p1, Point p2)
	{
		return Math.sqrt(Math.pow((p1.x-p2.x), 2) + Math.pow((p1.y-p2.y), 2));
	}
}
