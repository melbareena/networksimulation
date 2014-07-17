package common;

import java.util.List;
import java.util.Map.Entry;

import setting.ApplicationSettingFacade;

import dataStructure.Link;
import dataStructure.LinkTrafficMap;
import dataStructure.LinksChannelMap;
import dataStructure.Path;
import dataStructure.PathMap;

public class IntermediateOutput
{
	private static int counter = 0;
	public static void downLinkTrafficEstimationResult(LinkTrafficMap input)
	{
		

		if(!ApplicationSettingFacade.AppOutput.showIntermediateOutput()) return;
		PrintConsole.printErr("***********************DOWNLINK TRAFFIC ESTIMATING************************");
		for (Entry<Link, Float> traffic : input.entrySet())
		{
			
			PrintConsole.print(traffic.getKey() + ":" + traffic.getValue());
			counter++;
			
			if(counter % 100 == 0)
			{
				try
				{
					PrintConsole.print("press any key to continue.");
					System.in.read();
				}
				catch(Exception ex)
				{
					
				}
			}
		}
		PrintConsole.printErr("**************************************************************************");
	}

	public static void uplinkTrafficEstimationResult(LinkTrafficMap input)
	{
		if(!ApplicationSettingFacade.AppOutput.showIntermediateOutput()) return;
		PrintConsole.printErr("***********************UPLINK TRAFFIC ESTIMATING************************");
		for (Entry<Link, Float> traffic : input.entrySet())
		{
			PrintConsole.print(traffic.getKey() + ":" + traffic.getValue());
			counter++;
			
			if(counter % 100 == 0)
			{
				try
				{
					PrintConsole.print("press any key to continue.");
					System.in.read();
				}
				catch(Exception ex)
				{
					
				}
			}
		}
		PrintConsole.printErr("**************************************************************************");
		
	}

	public static void showDownLinkPath(PathMap downLinkShortestPathMap)
	{
		if(!ApplicationSettingFacade.AppOutput.showIntermediateOutput()) return;
		PrintConsole.printErr("**************************showing downlink paths****************************");
		for (List<Path> paths : downLinkShortestPathMap.values())
		{
				
			for (Path path : paths)
			{
				PrintConsole.print(path.toString()+ "\n");
				counter++;
				
				if(counter % 100 == 0)
				{
					try
					{
						PrintConsole.print("press any key to continue.");
						System.in.read();
					}
					catch(Exception ex)
					{
						
					}
				}
			}
			PrintConsole.print("-----------------------");
			
			PrintConsole.printErr("*****************************************************************************");
		}
		
	}

	public static void ChannelAssignment(LinksChannelMap linksChannel)
	{
		if(!ApplicationSettingFacade.AppOutput.showIntermediateOutput()) return;
			PrintConsole.print("" + linksChannel);	
		
	}
}
