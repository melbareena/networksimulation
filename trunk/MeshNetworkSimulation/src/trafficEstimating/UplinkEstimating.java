package trafficEstimating;


import java.util.ArrayList;
//import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import common.IntermediateOutput;
import topology2graph.TopologyGraphFacade;
import trafficGenerator.StaticTraffic;
import dataStructure.Link;
import dataStructure.TopologyGraph;
import dataStructure.LinkTrafficMap;
import dataStructure.UplinkPathTraffic;
import dataStructure.Path;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;

import dataStructure.PathMap;


class UplinkEstimating
{
	private LinkTrafficMap linksTraffic;
	private PathMap UplinkPaths;
	private TopologyGraph gtd;
	
	
	protected PathMap getOptimalUplinkPaths()
	{
		return UplinkPaths;
	}
	
	
	
	protected UplinkEstimating(LinkTrafficMap downlinkTraffic,TopologyGraph graph)
	{
		this.linksTraffic = downlinkTraffic;
		this.gtd = graph;
	}
	

	protected LinkTrafficMap estimating()
	{
		PathMap allPaths = TopologyGraphFacade.uplinkShortestPath(gtd); // extract uplink paths
		this.UplinkPaths = ExtractOptimalUplinkPaths.getOptimal(allPaths); // get optimal of them
		List<UplinkPathTraffic> tMAXvg = new ArrayList<UplinkPathTraffic>();
		UplinkTraffic uplinkTraffic = StaticTraffic.getUplinkTraffic();
		// the uplink traffic  tULv
		for (Entry<Vertex, Integer> tULv : uplinkTraffic.getUplinkTraffic().entrySet())
		{
			tMAXvg.clear();
					
			Vertex router = tULv.getKey();
			
			int trafficV = tULv.getValue();
				
 			List<Path> paths = this.UplinkPaths.get(router);
	
			List<UplinkPathTraffic> pathsTraffic = getTrafficOfPath(paths);
			
			
			//tMAXvg.addAll(getTmaxVG(paths));
			
			List<UplinkPathTraffic> tULp = waterFilling(pathsTraffic, trafficV, router);		
			
			ModifyTarfficOfLink(tULp);
			
		
		}
		
		IntermediateOutput.uplinkTrafficEstimationResult(linksTraffic);
		
		return linksTraffic;
	}


	


	private List<UplinkPathTraffic> getTrafficOfPath(List<Path> paths)
	{
		List<UplinkPathTraffic> result = new ArrayList<>();
		float trafficL = 0;
		float max = 0;
		Link maxLink = null;
		TreeSet<Float> traffics = new TreeSet<>();
		for (Path path : paths)
		{
			traffics = new TreeSet<>();
			max = 0;
			for (Link lp : path.getEdgePath())
			{
				trafficL = linksTraffic.get(lp);
				traffics.add(trafficL);
			
				if(trafficL > max)
				{
					max = trafficL ;
					maxLink = lp;
				}
			}
			result.add(new UplinkPathTraffic(path, maxLink, max, traffics));
		}
		
		return result;
	}



	/**
	 * @param uplinkPathwithTraffic: list of uplink paths with their traffic.
	 * @param trafficUL_V : traffic for a specific routers
	 * @param router : a specific router
	 * @return: a list of uplink paths for a router with their traffic ( tULp )
	 */
	private List<UplinkPathTraffic> waterFilling(List<UplinkPathTraffic> trafficDistribute, int trafficUL_V, Vertex router)
	{
		
		if(trafficDistribute.size() < 2 )
		{
			trafficDistribute.get(0).setPathTraffic(trafficDistribute.get(0).getPathTraffic() + trafficUL_V);
			trafficDistribute.get(0).setPercentage(100);
			return trafficDistribute;
		}
		
		float tMaxV = 0;
		Path maxPath = null;
		for (UplinkPathTraffic tulp : trafficDistribute)
		{
			if(tulp.linksTraffic.last() >= tMaxV)
			{
				tMaxV = tulp.linksTraffic.last();
				maxPath = tulp.getPath();
			}
		}
		
		int trashHold  = 0, percentage = 100, percentagePart;
		
		
		// it would be possible there are more than one path for a vertex to different gateways. So, It is necessarily 
		// to calculate the max of them. in water filling it calls T(Max,V);
		// tMaxV <---  max(tMAXgv)
	//	TrafficOfUplinkPath tMaxV = getTmaxV(uplinkPathwithTraffic);
		
		
		// ----- calc: sigma [ T ( max / v ) - T ( max / p ) ] --------------------------------------
		
		
		
		for (UplinkPathTraffic tPath : trafficDistribute)
			trashHold += tMaxV - tPath.getPathTraffic();
		//--------------------------------------------------------------------------------------------
		
		
		
		
		//*******************************************************************************************************************
		if(trafficUL_V > trashHold)
		{
			// set traffic of all paths with the maximum of its edge's traffic------------------------- 
			
			
			for(int index = 0 ; index < trafficDistribute.size() ; index++)
			{	
				UplinkPathTraffic current = trafficDistribute.get(index);
				if(maxPath.getId() != current.getPath().getId())
				{
					
					percentagePart = (int) ((100 * (tMaxV - current.getPathTraffic())) / trafficUL_V);
					// replaces traffic of path with max of them
					trafficDistribute.get(index).setPathTraffic(tMaxV);
					trafficDistribute.get(index).setPercentage(percentagePart);
				}
			}
			percentagePart = (int) ((100 * trashHold) / trafficUL_V);
			percentage -= percentagePart;
			trafficUL_V -= trashHold;
			
			
			//---------------------------------------------------------------------------------------------------
			
			if(trafficUL_V != 0)
			{
				float divide = trafficUL_V / trafficDistribute.size();
				for(int index = 0 ; index < trafficDistribute.size() ; index++)
				{
					float traff = trafficDistribute.get(index).getPathTraffic();
					double pre =  trafficDistribute.get(index).getPercentage();
					trafficDistribute.get(index).setPathTraffic(traff + divide);
					trafficDistribute.get(index).setPercentage(pre + (percentage / trafficDistribute.size()) );	
				}
			}
		}
		//*******************************************************************************************************************
		else
		{
			
			
			UplinkPathTraffic p_prime;	
			Collections.sort(trafficDistribute);
			List<UplinkPathTraffic> p = new ArrayList<>();
			
			List<UplinkPathTraffic> consider = new ArrayList<>();
			consider.addAll(trafficDistribute);
			
			p.add(trafficDistribute.get(0));  // add the path with min of traffic
			consider.remove(p.get(0));
			Collections.sort(consider);
			
			int totalPercentage = 100;
			
			while(consider.size() > 0 && trafficUL_V > 0)
			{
				p_prime = consider.remove(0);
				
				
				float t_p = p.get(0).getPathTraffic();
				float t_p_prime = p_prime.getPathTraffic();
				
				float delta = t_p_prime - t_p;
				
				if(delta * p.size() < trafficUL_V)
				{
					int partPercentage =(int) (totalPercentage * delta) / trafficUL_V;
					for (UplinkPathTraffic upt : p)
					{
						upt.setPathTraffic(upt.getPathTraffic() + delta);
						upt.setPercentage(upt.getPercentage() + partPercentage);
						trafficUL_V -= delta;
					}
					totalPercentage -= (partPercentage * p.size());
					p.add(p_prime);
				}
				else
				{
					delta  = trafficUL_V / p.size();
					int partPercentage =(int) (totalPercentage * delta) / trafficUL_V;
					
					for (UplinkPathTraffic upt : p)
					{
						upt.setPathTraffic(upt.getPathTraffic() + delta);
						upt.setPercentage(upt.getPercentage() + partPercentage);
					}
					
					trafficUL_V = 0;
					
				}
				
			}
			 
			for (UplinkPathTraffic upt : p)
			{
				for (UplinkPathTraffic upwt : trafficDistribute)
				{
					if(upwt.getPath().equals(upt.getPath()))
					{
						upwt.setPathTraffic(upt.getPathTraffic());
						upwt.setPercentage(upt.getPercentage());
					}
				}
			}
			
		}
			return trafficDistribute;
			
		/*	TrafficOfUplinkPath p_prime;		
			float  preTraffic,newTraffic;
			
			
			
			
			int ITER = 0, index = 0 ;
			
			
			List<TrafficOfUplinkPath> pULx = new ArrayList<>();
			while(consider.size() > 0 && trafficUL_V > 0)
			{
				p_prime = uplinkPathwithTraffic.get(index);
				ITER++;
				pULx.add(p_prime);
				
				
				
				// for p'' in pULp'' 
				for (int pdp = 0 ; pdp < uplinkPathwithTraffic.size() ; pdp++)
				{
					TrafficOfUplinkPath p_double_prime = uplinkPathwithTraffic.get(pdp);
					if(p_double_prime.getPath().equals(p_prime.getPath())) continue;
					
					
					if(p_double_prime.getPathTraffic() < p_prime.getPathTraffic() && trafficUL_V != 0)
					{
						float delta =  p_prime.getPathTraffic() - p_double_prime.getPathTraffic();
						if(ITER * delta < trafficUL_V)
						{
							preTraffic  = p_double_prime.getPathTraffic();
							newTraffic  = preTraffic + delta;
							p_double_prime.setPathTraffic(newTraffic);
							trafficUL_V -= delta;
							
							double prcentagePart = (percentage * delta) / trafficUL_V;
							
							p_double_prime.setPercentage(p_double_prime.getPercentage() + prcentagePart);
							
							percentage -= prcentagePart;
						}
						else
						{
							for (TrafficOfUplinkPath tULx : pULx)
							{
								tULx.setPathTraffic(tULx.getPathTraffic() + trafficUL_V / ITER);
								tULx.setPercentage(tULx.getPercentage() + (percentage / ITER) );
							}
							
							trafficUL_V = 0;
						}

					}
				}
				consider.remove(p_prime);
				index++;
			}
			
			for (TrafficOfUplinkPath pu : pULx)
			{
				for (TrafficOfUplinkPath upt : uplinkPathwithTraffic)
				{
					if(pu.getPath().equals(upt.getPath()))
					{
						upt.setPercentage(pu.getPercentage());
					}
				}
			}*/

		
	}

	/*private UplinkPathTraffic getTmaxV(List<UplinkPathTraffic> trafficMax)
	{
		UplinkPathTraffic maxTraffic = null;
		int MaxValue = 0;
		for (UplinkPathTraffic maxTrafficOfPath : trafficMax)
		{
			if(maxTrafficOfPath.getPathTraffic() >= MaxValue)
				maxTraffic = maxTrafficOfPath;
		}
		return maxTraffic;
	}

	private UplinkPathTraffic getTmaxVG(Path ulPath)
	{
		Edge MaxEdge = null;
		Float max = 0f;
		Float trafficL = 0f;
		TreeSet<Float> lt = new TreeSet<>();
		
		for(int i = 0 ; i < ulPath.getEdgePath().size()  ; i++)
		{
				
				Link link = ulPath.getEdgePath().get(i);		
				trafficL = linksTraffic.get(link);
				lt.add(trafficL);
				Link reverseLink = getReverseLink(link);
				Float trafficLprim = linksTraffic.get(reverseLink);
				if(trafficL + trafficLprim > max)
				{
					max = trafficL + trafficLprim ;
					MaxEdge = new Edge(link, reverseLink);
				}
		}
		
		return new UplinkPathTraffic(ulPath, MaxEdge, max,lt);
	}
	
	private Collection<? extends UplinkPathTraffic> getTmaxVG(List<Path> uplinkPathForRouter)
	{
		List<UplinkPathTraffic> output = new ArrayList<UplinkPathTraffic>();
		
		for (Path trafficOfPath : uplinkPathForRouter)
		{
			output.add(getTmaxVG(trafficOfPath));
		}
			
		return output; 
	}

	private Link getReverseLink(Link edge)
	{
		for (Entry<Integer, Link> edgesMap : gtd.LinkMap.entrySet())
		{
			Link currentEdge = edgesMap.getValue();
			if(edge.getSource() == currentEdge.getDestination() && edge.getDestination() == currentEdge.getSource())
				return currentEdge;
		}
		return null;
	}*/
	
	private void ModifyTarfficOfLink(List<UplinkPathTraffic> listOf_tULp)
	{
		// tULp
		for (UplinkPathTraffic tULp : listOf_tULp)
		{
			
			// for l in p
			for (Link link : tULp.getPath().getEdgePath())
			{
				linksTraffic.put(link, tULp.getPathTraffic());
			}
		}
		
	}


	
}
