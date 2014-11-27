package trafficEstimating;


import java.util.ArrayList;
//import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import common.IntermediateOutput;
import setting.ApplicationSettingFacade;
import topology2graph.TopologyGraphFacade;
import trafficGenerator.DynamicTraffic;
import trafficGenerator.StaticTraffic;
import dataStructure.Link;
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

	
	
	protected UplinkEstimating(LinkTrafficMap downlinkTraffic)
	{
		this.linksTraffic = downlinkTraffic;
	}
	

	protected LinkTrafficMap estimating()
	{
		if(ApplicationSettingFacade.Traffic.isDynamicType()) return dynamicEstimating();
			
		
		UplinkPaths = TopologyGraphFacade.getOptimalUplinkPaths();
		
		List<UplinkPathTraffic> tMAXvg = new ArrayList<UplinkPathTraffic>();
		UplinkTraffic uplinkTraffic = StaticTraffic.getUplinkTraffic();
		// the uplink traffic  tULv
		for (Entry<Vertex, Integer> tULv : uplinkTraffic.getTraffic().entrySet())
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


	


	private LinkTrafficMap dynamicEstimating()
	{
		DynamicTraffic dyTraffic = DynamicTraffic.Initilization();
		UplinkPaths = TopologyGraphFacade.getOptimalUplinkPaths();
		
		
		
		List<UplinkPathTraffic> tMAXvg = new ArrayList<UplinkPathTraffic>();
		UplinkTraffic uplinkTraffic = dyTraffic.getUplinkTraffic();
		
		for (Entry<Vertex, Integer> tULv : uplinkTraffic.getTraffic().entrySet())
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
			trafficDistribute.get(0).setPathTraffic(trafficUL_V);
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
	}

	
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
