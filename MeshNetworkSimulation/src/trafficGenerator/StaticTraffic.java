package trafficGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import common.FileGenerator;
import common.PrintConsole;
import dataStructure.DownlinkTraffic;
import dataStructure.UplinkTraffic;
import dataStructure.Vertex;
import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.TypeOfGenerationEnum;

public class StaticTraffic
{	
	
	private static UplinkTraffic  uplinkTraffic;
	private static DownlinkTraffic downlinkTraffic;
	public static UplinkTraffic getUplinkTraffic()
	{
		if(uplinkTraffic != null) return uplinkTraffic;
		
		if(ApplicationSettingFacade.Traffic.getTypeOfGenerator() == TypeOfGenerationEnum.RANDOM)
			return randomUpLink();
		return fileUplink();
	}

	private static UplinkTraffic fileUplink()
	{
		uplinkTraffic = new UplinkTraffic();
		Map<Integer, Vertex> routers =  ApplicationSettingFacade.Nodes.getNodes();
		String var = null; 
		String[] tokens = null;
		try
		{
		
		
			BufferedReader reader = new BufferedReader(new FileReader(ApplicationSettingFacade.Traffic.getAddressUp()));
			while((var=reader.readLine())!=null)
			{
				tokens = var.split("[ ]");
				int nodeId = Integer.parseInt(tokens[0]);
				uplinkTraffic.add(routers.get(nodeId) , Integer.parseInt(tokens[1]));
			}
			reader.close();
		}
		catch(Exception ex)
		{
			PrintConsole.printErr("StaticTraffic/fileUplink: message: " + ex.getMessage());
		}
		FileGenerator.UplinkTrafficInFile(uplinkTraffic);
		return uplinkTraffic;
	}

	private static UplinkTraffic randomUpLink()
	{
		
		Map<Integer,Vertex> routerSet = ApplicationSettingFacade.Router.getRouter();
		
		Random rand = new Random();
		for (Entry<Integer,Vertex> routerMap : routerSet.entrySet())
		{
			
			int trf = rand.nextInt(20) + 30;
			uplinkTraffic.add(routerMap.getValue(), trf*100);
		}

		PrintConsole.print("Generating static traffic for uplinks is done.");
		
		FileGenerator.UplinkTrafficInFile(uplinkTraffic);
		
		return uplinkTraffic;
	}
	
	public static DownlinkTraffic getDownlinkTraffic()
	{
		if(downlinkTraffic != null) return downlinkTraffic;
		
		if(ApplicationSettingFacade.Traffic.getTypeOfGenerator() == TypeOfGenerationEnum.RANDOM)
			return downlinkRandom();
		return downlinkFile();
	}

	private static DownlinkTraffic downlinkRandom()
	{
		Map<Integer,Vertex> routerSet = ApplicationSettingFacade.Router.getRouter();
		//int gatewayNumbers = ApplicationSettingFacade.Gateway.getSize();
		Random rand = new Random();
		for (Entry<Integer, Vertex> getway : ApplicationSettingFacade.Gateway.getGateway().entrySet())
		{
			TreeMap<Vertex,Float> grMap = new TreeMap<>();
			for (Entry<Integer,Vertex> routerMap : routerSet.entrySet())
			{
				
				float trf = rand.nextInt(20) + 30;
				
				grMap.put(routerMap.getValue(), trf*100);
			}
			downlinkTraffic.add(getway.getValue(), grMap);
		}
		PrintConsole.print("Generating static traffic for downlinks is done.");
		FileGenerator.dowlinkTrafficInFile(downlinkTraffic.getDownLinkTraffic());
		return downlinkTraffic;
	}
	private static DownlinkTraffic downlinkFile()
	{
		Map<Integer,Vertex> routerSet = ApplicationSettingFacade.Router.getRouter();
		int routerSize = routerSet.size();
		downlinkTraffic = new DownlinkTraffic();
		String var = null; 
		int gatwayIndex = 0;
		int RouterIndex = 0;
		String[] tokens = null;
		Vertex gateway = ApplicationSettingFacade.Gateway.getGateway().get(gatwayIndex);
		
		try
		{
			TreeMap<Vertex, Float> grMap = new TreeMap<>();
			BufferedReader reader = new BufferedReader(new FileReader(ApplicationSettingFacade.Traffic.getAddressDown()));
			while((var=reader.readLine())!=null)
			{
				
				if(RouterIndex == routerSize)
				{
					RouterIndex = 0;
					downlinkTraffic.add(gateway, grMap);
					gatwayIndex++;
					gateway = ApplicationSettingFacade.Gateway.getGateway().get(gatwayIndex);
					grMap = new TreeMap<>();
				}
				tokens = var.split("[ ]");
				int routerID = Integer.parseInt(tokens[0]);
				float traffic = Float.parseFloat(tokens[1]);
				grMap.put(ApplicationSettingFacade.Router.getRouters().get(routerID), traffic);
				RouterIndex++;
			}
			
			downlinkTraffic.add(gateway, grMap);
			reader.close();
		}
		catch(Exception ex)
		{
			PrintConsole.printErr("StaticTraffic/DownlinkTraffic : message: " + ex.getMessage());
		}
		FileGenerator.dowlinkTrafficInFile(downlinkTraffic.getDownLinkTraffic());
		return downlinkTraffic;
	}
}
