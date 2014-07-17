package trafficEstimating;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;

import common.FileGenerator;
import common.PrintConsole;

import dataStructure.Path;
import dataStructure.PathMap;
import dataStructure.Vertex;

class ExtractOptimalUplinkPaths
{
	protected static PathMap getOptimal(PathMap uplinkPathMap)
	{
		PathMap resultMap = new PathMap();
		for(Entry<Vertex, List<Path>> pathForRouters : uplinkPathMap.entrySet())
		{
			List<Path> allPathForARouter = pathForRouters.getValue();
			if(allPathForARouter.size() == 1)
			{
					resultMap.put(pathForRouters.getKey(), allPathForARouter);
					continue;
			}
			else
			{
				List<Path> optimalPaths = new ArrayList<>();
				int hopsP = allPathForARouter.get(0).getNodePath().size();
				optimalPaths.add(allPathForARouter.get(0));
				
				for(int i = 1 ; i < allPathForARouter.size() ; i++)
				{
					int hopsPP = allPathForARouter.get(i).getNodePath().size();
					float trashHold =(float) (hopsPP-hopsP) / hopsP;
					if(trashHold < 0.3 )
						optimalPaths.add( allPathForARouter.get(i));
				}
				resultMap.put(pathForRouters.getKey(), optimalPaths);
			}
		}
		
		if(ApplicationSettingFacade.AppOutput.showIntermediateOutput())
		{
			PrintConsole.printErr("**************************Optimal set of uplink paths****************************");
			for (List<Path> paths : resultMap.values())
			{
				
				for (Path path : paths)
				{
					PrintConsole.print(path.toString()+ "\n");
				}
				PrintConsole.print("-----------------------");
				
			}
			PrintConsole.printErr("*****************************************************************************");
		}
		FileGenerator.OptimalUplinkPath(resultMap);
		return resultMap;
	}
}
