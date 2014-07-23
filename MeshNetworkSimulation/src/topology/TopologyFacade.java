package topology;

import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;
import topology2graph.TopologyGraphFacade;
import dataStructure.Link;
import dataStructure.Vertex;

public class TopologyFacade
{
	public static void getRandomTopology(int minimumDistance,int numberOfNodes,int tranmistionRate,long randSeed,int stest)
	{
		RandomTopology rand = RandomTopology.Initiate(minimumDistance, numberOfNodes, tranmistionRate, randSeed, stest);
		rand.CreateTopology();
	}
	
	public static Map<Vertex, Map<Vertex,Double>> getDistanceForEachNode()
	{
		 return NodesNeighbors.getDistanceOfNodes();
	}
	
}
