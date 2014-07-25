package topology;

import java.util.Map;
import dataStructure.Vertex;

public class TopologyFacade
{
	public static void getRandomTopology2(int minimumDistance,int numberOfNodes,int tranmistionRate,long randSeed,int stest)
	{
		RandomTopology rand = RandomTopology.Initiate(minimumDistance, numberOfNodes, tranmistionRate, randSeed, stest);
		rand.CreateTopology();
	}
	
	public static Map<Vertex, Map<Vertex,Double>> getDistanceForEachNode()
	{
		 return NodesNeighbors.getDistanceOfNodes();
	}
	
}
